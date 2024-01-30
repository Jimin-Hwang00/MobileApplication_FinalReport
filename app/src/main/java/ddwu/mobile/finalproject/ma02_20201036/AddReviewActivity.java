package ddwu.mobile.finalproject.ma02_20201036;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ddwu.mobile.finalproject.ma02_20201036.model.dto.Review;
import ddwu.mobile.finalproject.ma02_20201036.model.json.Movie;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AddReviewActivity extends AppCompatActivity {
    final static String TAG = "AddReviewActivity";

    final static int FIND_MOVIE_THEATER_ACTIVITY_CODE = 100;

    ImageView moviePosterImage;
    EditText titleEditText;
    EditText directorEditText;
    EditText actorEditText;
    EditText commentEditText;
    EditText cinemaEditText;
    Button saveReviewBtn;

    Movie selectedMovie;

    String imageUrl;
    File saveFile;

    double lat;
    double lng;
    String theater;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_layout);

        moviePosterImage = findViewById(R.id.moviePosterImage);
        titleEditText = findViewById(R.id.titleEditText);
        directorEditText = findViewById(R.id.directorEditText);
        actorEditText = findViewById(R.id.actorEditText);
        commentEditText = findViewById(R.id.commentEditText);
        cinemaEditText = findViewById(R.id.cinemaText);
        saveReviewBtn = findViewById(R.id.saveReviewBtn);

        cinemaEditText.setEnabled(false);

        Intent intent = getIntent();
        selectedMovie = (Movie) intent.getSerializableExtra("selectedMovie");

        String imageFile = selectedMovie.getImage().replace("https://ssl.pstatic.net/imgmovie/mdi/mit110/", "");
        imageFile = imageFile.replace("/", "_");

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "myreview");
        if (imageFile != null) {
            saveFile = new File(file.getPath(), imageFile);
            imageUrl = saveFile.getPath().toString();
        }

        if (!saveFile.exists()) {
            Glide.with(AddReviewActivity.this)
                            .load(selectedMovie.getImage())
                                    .into(moviePosterImage);
        } else {
            if (isExternalStorageWritable()) {
                File readFile = new File(imageUrl);
                Glide.with(this)
                        .load(readFile)
                        .into(moviePosterImage);
            }
        }

        titleEditText.setText(selectedMovie.getTitle());
        directorEditText.setText(selectedMovie.getDirector());
        actorEditText.setText(selectedMovie.getActor());

        lat = Double.parseDouble(getResources().getString(R.string.default_lat));
        lng = Double.parseDouble(getResources().getString(R.string.default_lng));
    }

    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private void saveImage() {
        Glide.with(AddReviewActivity.this)
                .asBitmap()
                .load(selectedMovie.getImage())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (isExternalStorageWritable()) {
                            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                    "myreview");
                            if (!file.mkdirs()) {
                                Log.d(TAG, "directory not created");
                            }

                            try {
                                FileOutputStream fos = new FileOutputStream((saveFile));
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public void saveReview() {
        String title = titleEditText.getText().toString();
        String director = directorEditText.getText().toString();
        String actor = actorEditText.getText().toString();
        String comment = commentEditText.getText().toString();
        Review newReview = new Review(imageUrl, title, director, actor, comment, lat, lng, theater);

        ReviewDB reviewDB = ReviewDB.getDatabase(AddReviewActivity.this);
        ReviewDao dao = reviewDB.reviewDao();

        Single<Long> insertResult = dao.insertReview(newReview);
        mDisposable.add(
                insertResult.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> Toast.makeText(AddReviewActivity.this, "리뷰를 저장하였습니다.", Toast.LENGTH_SHORT).show(),
                                throwable -> Toast.makeText(AddReviewActivity.this, "저장에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()));
    }

    public void onClick(View v) throws InterruptedException {
        switch (v.getId()) {
            case R.id.searchCinemaBtn:
                Intent cinemaIntent = new Intent(this, FindMovieTheaterActivity.class);
                cinemaIntent.putExtra("lat", lat);
                cinemaIntent.putExtra("lng", lng);
                startActivityForResult(cinemaIntent, FIND_MOVIE_THEATER_ACTIVITY_CODE);
                break;

            case R.id.saveReviewBtn:
                saveImage();
                saveReview();

                finish();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case FIND_MOVIE_THEATER_ACTIVITY_CODE:
                if (resultCode == RESULT_OK) {
                    lat = data.getDoubleExtra("lat", 0);
                    lng = data.getDoubleExtra("lng", 0);
                    theater = data.getStringExtra("movieTheaterName");
                    cinemaEditText.setText(theater);
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }
}
