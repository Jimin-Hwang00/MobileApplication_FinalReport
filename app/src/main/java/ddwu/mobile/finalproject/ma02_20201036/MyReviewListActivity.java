package ddwu.mobile.finalproject.ma02_20201036;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ddwu.mobile.finalproject.ma02_20201036.model.dto.Review;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyReviewListActivity extends AppCompatActivity {
    final static String TAG = "MyReviewListActivity";

    ArrayList<Review> myReviews;
    ListView listView;
    MyReviewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_review_list_layout);

        myReviews = new ArrayList<>();
        listView = findViewById(R.id.myReviewList);
        adapter = new MyReviewAdapter(this, R.layout.movie_item_layout, myReviews);
        listView.setAdapter(adapter);

        ReviewDB reviewDB = ReviewDB.getDatabase(MyReviewListActivity.this);
        ReviewDao dao = reviewDB.reviewDao();

        Flowable<List<Review>> selectResult = dao.getAllReviews();
        selectResult.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reviews -> {
                    myReviews.clear();
                    for (Review review : reviews) {
                        myReviews.add(review);
                    }
                    adapter.notifyDataSetChanged();
                }, throwable -> Log.d(TAG, "error"));



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int pos = i;

                Review review = myReviews.get(i);

                Intent intent = new Intent(MyReviewListActivity.this, MyReviewActivity.class);
                intent.putExtra("review", review);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int pos = i;

                Review review = myReviews.get(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(MyReviewListActivity.this)
                        .setTitle("리뷰 삭제")
                        .setMessage("\"" + review.getTitle() + "\"에 대한 리뷰를 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteReview(review);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("취소", null);

                builder.show();

                return true;
            }
        });
    }

    public void deleteReview(Review review) {
        String image = review.getImage();

        ReviewDB reviewDB = ReviewDB.getDatabase(MyReviewListActivity.this);
        ReviewDao dao = reviewDB.reviewDao();

        Completable deleteResult = dao.deleteReview(review);
        deleteResult.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(this, "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    File file = new File(image);
                    if (file.exists()) {
                        file.delete();
                    }}, throwable -> Log.d(TAG, "error"));

        return;
    }
}
