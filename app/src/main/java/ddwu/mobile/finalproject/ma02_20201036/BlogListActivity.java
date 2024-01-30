package ddwu.mobile.finalproject.ma02_20201036;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import ddwu.mobile.finalproject.ma02_20201036.model.json.Blog;
import ddwu.mobile.finalproject.ma02_20201036.model.json.BlogRoot;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BlogListActivity extends AppCompatActivity {
    final static String TAG = "BlogListActivity";

    String naverId;
    String naverSecret;

    private Retrofit retrofit = null;
    private INaverBlogAPIService naverAPIService;

    BlogAdapter adapter;
    ArrayList<Blog> resultList;
    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_list_layout);

        resultList = new ArrayList<Blog>();
        adapter = new BlogAdapter(this, R.layout.blog_item_layout, resultList);
        listView = findViewById(R.id.blogListView);
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        String movieTitle = intent.getStringExtra("title");
        blogSearch(movieTitle);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int pos = i;

                Blog blog = resultList.get(pos);
                Intent blogItemIntent = new Intent(BlogListActivity.this, BlogActivity.class);
                blogItemIntent.putExtra("blog", blog);
                blogItemIntent.putExtra("movieTitle", movieTitle);
                startActivity(blogItemIntent);
            }
        });
    }

    public void blogSearch(String query) {
        naverId = getResources().getString(R.string.client_id);
        naverSecret = getResources().getString(R.string.client_secret);

        if (retrofit == null) {
            try {
                retrofit = new Retrofit.Builder()
                        .baseUrl("https://openapi.naver.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        naverAPIService = retrofit.create(INaverBlogAPIService.class);

        Call<BlogRoot> apiCall = naverAPIService.getMovieList(naverId, naverSecret, query);
        apiCall.enqueue(apiCallback);
    }

    public String replaceTag(String s) {
        s = s.replaceAll("<b>", "");
        s = s.replaceAll("</b>", "");
        s = s.replaceAll("&amp", "&");
        s = s.replaceAll("<br>", "\n");
        s = s.replaceAll("&gt;", ">");
        s = s.replaceAll("&lt;", "<");
        s = s.replaceAll("&quot;", "");
        s = s.replaceAll("&nbsp;", " ");
        s = s.replaceAll("&amp;", "&");

        return s;
    }

    Callback<BlogRoot> apiCallback = new Callback<BlogRoot>() {
        @Override
        public void onResponse(Call<BlogRoot> call, Response<BlogRoot> response) {
            if (response.isSuccessful() && response.body() != null) {
                List<Blog> items = response.body().getItems();

                resultList.clear();

                for (Blog blog : items) {
                    String title = blog.getTitle();
                    title = replaceTag(title);
                    blog.setTitle(title);

                    String description = blog.getDescription();
                    description = replaceTag(description);
                    blog.setDescription(description);

                    resultList.add(blog);
                }

                adapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "실패 : " + response.body() + ", " + response.code());
            }
        }

        @Override
        public void onFailure(Call<BlogRoot> call, Throwable t) {

        }
    };
}
