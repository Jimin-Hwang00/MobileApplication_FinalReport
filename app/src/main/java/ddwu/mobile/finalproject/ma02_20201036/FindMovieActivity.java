package ddwu.mobile.finalproject.ma02_20201036;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import ddwu.mobile.finalproject.ma02_20201036.model.json.Movie;
import ddwu.mobile.finalproject.ma02_20201036.model.json.MovieRoot;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FindMovieActivity extends AppCompatActivity {
    final static String TAG = "FindMovieActivity";

    EditText movieTextView;

    String naverId;
    String naverSecret;

    private Retrofit retrofit = null;
    private INaverMovieAPIService naverAPIService;

    MovieAdapter adapter;
    ArrayList<Movie> resultList;
    ListView listView;

    EditText searchEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_movie_layout);

        naverId = getResources().getString(R.string.client_id);
        naverSecret = getResources().getString(R.string.client_secret);

        searchEditText = findViewById(R.id.movieEditText);

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

        naverAPIService = retrofit.create(INaverMovieAPIService.class);

        resultList = new ArrayList<Movie>();
        adapter = new MovieAdapter(this, R.layout.movie_item_layout, resultList);
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie selectedMovie = new Movie();

                selectedMovie.setImage(resultList.get(i).getImage());
                selectedMovie.setTitle(resultList.get(i).getTitle());
                selectedMovie.setDirector(resultList.get(i).getDirector());
                selectedMovie.setActor(resultList.get(i).getActor());

                Intent intent = new Intent(FindMovieActivity.this, AddReviewActivity.class);
                intent.putExtra("selectedMovie", selectedMovie);
                startActivity(intent);
            }
        });
    }

    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.searchPlaceBtn:
                String query = searchEditText.getText().toString();
                Call<MovieRoot> apiCall = naverAPIService.getMovieList(naverId, naverSecret, query);
                apiCall.enqueue(apiCallback);
                break;
        }
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

    Callback<MovieRoot> apiCallback = new Callback<MovieRoot>() {
        @Override
        public void onResponse(Call<MovieRoot> call, Response<MovieRoot> response) {
            if (response.isSuccessful() && response.body() != null) {
                List<Movie> items = response.body().getItems();

                resultList.clear();

                for (Movie movie : items) {
                    String title = movie.getTitle();
                    title = replaceTag(title);
                    movie.setTitle(title);

                    String director = movie.getDirector();
                    director = replaceTag(director);
                    director = director.replaceAll(".$", "");
                    movie.setDirector(director);

                    String actor = movie.getActor();
                    actor = replaceTag(actor);
                    actor = actor.replaceAll(".$", "");
                    movie.setActor(actor);

                    Movie newMovie = new Movie();
                    newMovie.setTitle(title);
                    newMovie.setImage(movie.getImage());
                    newMovie.setDirector(director);
                    newMovie.setActor(actor);

                    resultList.add(newMovie);
                }

                adapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "실패 : " + response.body() + ", " + response.code());
            }
        }

        @Override
        public void onFailure(Call<MovieRoot> call, Throwable t) {
            Log.e(TAG, t.getMessage());
        }
    };
}
