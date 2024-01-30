package ddwu.mobile.finalproject.ma02_20201036;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ddwu.mobile.finalproject.ma02_20201036.model.dto.Review;

@Database(entities = {Review.class}, version=1)
public abstract class ReviewDB extends RoomDatabase {
    public abstract ReviewDao reviewDao();

    private static volatile ReviewDB INSTANCE;

    static ReviewDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ReviewDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ReviewDB.class, "review_db.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
