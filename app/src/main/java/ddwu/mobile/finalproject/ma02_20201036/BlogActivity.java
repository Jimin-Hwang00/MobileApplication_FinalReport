package ddwu.mobile.finalproject.ma02_20201036;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ddwu.mobile.finalproject.ma02_20201036.model.json.Blog;

public class BlogActivity extends AppCompatActivity {
    Blog blog;
    String movieTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_layout);

        Intent intent = getIntent();
        blog = (Blog) intent.getSerializableExtra("blog");
        movieTitle = intent.getStringExtra("movieTitle");

        TextView title = findViewById(R.id.blogTitleTextView);
        TextView bloggerName = findViewById(R.id.bloggerNameTextView);
        TextView description = findViewById(R.id.descriptionTextView);

        title.setText(blog.getTitle());
        bloggerName.setText(blog.getBloggername());
        description.setText(blog.getDescription());
    }

    public void onClick(View v) {
        Intent linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(blog.getLink()));
        startActivity(linkIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        stopPlay(); //이 액티비티에서 종료되어야 하는 활동 종료시켜주는 함수
        Intent intent = new Intent(BlogActivity.this, BlogListActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }
}
