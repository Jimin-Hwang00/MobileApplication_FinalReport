package ddwu.mobile.finalproject.ma02_20201036;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.videoCamImageView:
                Intent addReiviewIntent = new Intent(this, FindMovieActivity.class);
                startActivity(addReiviewIntent);
                break;
            case R.id.fileImageView:
                Intent myReviewIntent = new Intent(this, MyReviewListActivity.class);
                startActivity(myReviewIntent);
                break;
        }
    }
}