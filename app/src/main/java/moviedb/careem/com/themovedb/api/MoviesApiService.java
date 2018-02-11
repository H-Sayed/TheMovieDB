package moviedb.careem.com.themovedb.api;

import java.util.Map;

import io.reactivex.Observable;
import moviedb.careem.com.themovedb.mvp.model.MoviesResponse;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Hassan Sayed on 2/11/2018.
 */

public interface MoviesApiService {

    @GET("discover/movie")
    Observable<MoviesResponse> getMovies(@QueryMap Map<String, String> options);
}
