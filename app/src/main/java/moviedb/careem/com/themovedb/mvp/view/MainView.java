package moviedb.careem.com.themovedb.mvp.view;

import java.util.List;

import moviedb.careem.com.themovedb.mvp.model.Movie;

/**
 * Created by Hassan Sayed on 2/11/2018.
 */

public interface MainView extends BaseView {

    void showLoadingProgress();
    void hideLoadingProgress();
    void onMoviesLoaded(List<Movie> movies);
    void onNoMovies();
    void clearRecentMovies();
    void onError(String message);
    void onRequestError(Throwable throwable);
    void showOfflineView();

}
