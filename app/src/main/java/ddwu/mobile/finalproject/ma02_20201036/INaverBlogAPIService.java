package ddwu.mobile.finalproject.ma02_20201036;

import ddwu.mobile.finalproject.ma02_20201036.model.json.BlogRoot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface INaverBlogAPIService {
    @GET("/v1/search/blog.json")
    Call<BlogRoot> getMovieList(@Header("X-Naver-Client-Id") String clientId,
                                @Header("X-Naver-Client-Secret") String clientSecret,
                                @Query("query") String query);
}
