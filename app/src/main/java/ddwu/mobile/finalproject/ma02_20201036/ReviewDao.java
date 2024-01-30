package ddwu.mobile.finalproject.ma02_20201036;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ddwu.mobile.finalproject.ma02_20201036.model.dto.Review;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface ReviewDao {
    @Insert
    Single<Long> insertReview(Review review);

    @Update
    Single<Integer> updateReview(Review review);

    @Delete
    Completable deleteReview(Review review);

    @Query("SELECT * FROM review_table")
    Flowable<List<Review>> getAllReviews();

    @Query("SELECT * FROM review_table WHERE title IN (:query) || director IN (:query) || actor IN (:query)")
    Single<Review> getReviewByQuery(String query);
}
