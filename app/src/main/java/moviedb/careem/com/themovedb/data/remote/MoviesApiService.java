package moviedb.careem.com.themovedb.data.remote;

import java.util.Map;

import io.reactivex.Observable;
import moviedb.careem.com.themovedb.mvp.model.MoviesResponse;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Hassan Sayed on 2/11/2018.
 * API service which hits the moviedb apis to get the movies by
 * filter or list all movies
 */

public interface MoviesApiService {

    @GET("discover/movie")
    Observable<MoviesResponse> getMovies(@QueryMap Map<String, String> options);
}
