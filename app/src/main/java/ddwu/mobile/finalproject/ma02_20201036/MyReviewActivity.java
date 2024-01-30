package ddwu.mobile.finalproject.ma02_20201036;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import ddwu.mobile.finalproject.ma02_20201036.model.dto.Review;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyReviewActivity extends AppCompatActivity {
    final static String TAG = "MyReview";

    final int FIND_MOVIE_THEATER_CODE = 200;

    Button saveReviewBtn;
    ImageView moviePosterImage;
    EditText titleEditText;
    EditText directorEditText;
    EditText actorEditText;
    EditText commentEditText;
    EditText cinemaEditText;

    Review review;

    double lat;
    double lng;
    String theater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_layout);

        saveReviewBtn = findViewById(R.id.saveReviewBtn);
        saveReviewBtn.setText("수정");

        moviePosterImage = findViewById(R.id.moviePosterImage);
        titleEditText = findViewById(R.id.titleEditText);
        directorEditText = findViewById(R.id.directorEditText);
        actorEditText = findViewById(R.id.actorEditText);
        commentEditText = findViewById(R.id.commentEditText);
        cinemaEditText = findViewById(R.id.cinemaText);

        Intent intent = getIntent();
        review = (Review) intent.getSerializableExtra("review");

        setMoviePosterImage();
        titleEditText.setText(review.getTitle());
        directorEditText.setText(review.getDirector());
        actorEditText.setText(review.getActor());
        commentEditText.setText(review.getComment());
        cinemaEditText.setText(review.getTheater());

        lat = review.getLat();
        lng = review.getLng();
        theater = review.getTheater();
    }

    class GeoTask extends AsyncTask<Location, Void, List<Address>> {
        Geocoder geocoder = new Geocoder(MyReviewActivity.this, Locale.getDefault());

        @Override
        protected List<Address> doInBackground(Location... locations) {
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(locations[0].getLatitude(), locations[0].getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            String addressLine = addresses.get(0).getAddressLine(0).toString();

        }
    }

    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public void setMoviePosterImage() {
        if ((review.getImage()) != null) {
            File readFile = new File(review.getImage());
            if (readFile.exists()) {
                if (isExternalStorageWritable()) {
                    Glide.with(this)
                            .load(readFile)
                            .into(moviePosterImage);
                }
            }
        }
    }

    public void updateReview() {
        ReviewDB reviewDB = ReviewDB.getDatabase(MyReviewActivity.this);
        ReviewDao dao = reviewDB.reviewDao();

        long id = review.getId();
        String image = review.getImage();
        String title = titleEditText.getText().toString();
        String director = directorEditText.getText().toString();
        String actor = actorEditText.getText().toString();
        String comment = commentEditText.getText().toString();

        Review updatedReview = new Review(id, image, title, director, actor, comment, lat, lng, theater);

        Single<Integer> updateResult = dao.updateReview(updatedReview);
        updateResult.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result == 1) {
                        Toast.makeText(this, "리뷰가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                        review = updatedReview;
                    }
                }, throwable -> Log.d(TAG, "error"));
    }

    public void onClick(View v) throws InterruptedException {
        switch (v.getId()) {
            case R.id.searchCinemaBtn:
                Intent cinemaIntent = new Intent(this, FindMovieTheaterActivity.class);
                cinemaIntent.putExtra("lat", lat);
                cinemaIntent.putExtra("lng", lng);
                startActivityForResult(cinemaIntent, FIND_MOVIE_THEATER_CODE);
                break;

            case R.id.saveReviewBtn:
                updateReview();
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.twitter:
                shareReviewTwitter();
                break;
            case R.id.blog:
                Intent blogIntent = new Intent(MyReviewActivity.this, BlogListActivity.class);
                blogIntent.putExtra("title", review.getTitle());
                startActivity(blogIntent);
                break;
        }

        return true;
    }

    public void shareReviewTwitter() {
        String reviewText = "제목 : " + review.getTitle() + "\n";
        reviewText += "감독 : " + review.getDirector() + "\n";
        reviewText += "배우 : " + review.getActor() + "\n";
        reviewText += "리뷰 : " + review.getComment() + "\n";
        reviewText += "관람 영화관 : " + review.getTheater() + "\n";

        try {
            String sharedText = String.format("http://twitter.com/intent/tweet?text=%s", URLEncoder.encode(reviewText, "utf-8"));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharedText));
            startActivity(intent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case FIND_MOVIE_THEATER_CODE:
                if (resultCode == RESULT_OK) {
                    lat = data.getDoubleExtra("lat", 0);
                    lng = data.getDoubleExtra("lng", 0);
                    theater = data.getStringExtra("movieTheaterName");
                    cinemaEditText.setText(theater);
                }
        }
    }
}
