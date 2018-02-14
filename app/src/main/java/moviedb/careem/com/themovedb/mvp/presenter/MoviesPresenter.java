package moviedb.careem.com.themovedb.mvp.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import moviedb.careem.com.themovedb.BuildConfig;
import moviedb.careem.com.themovedb.data.remote.MoviesApiService;
import moviedb.careem.com.themovedb.base.BasePresenter;
import moviedb.careem.com.themovedb.mvp.model.Movie;
import moviedb.careem.com.themovedb.mvp.model.MoviesResponse;
import moviedb.careem.com.themovedb.data.local.MovieLocalDB;
import moviedb.careem.com.themovedb.mvp.view.MainView;
import moviedb.careem.com.themovedb.utilities.Const;
import moviedb.careem.com.themovedb.utilities.Utils;

/**
 * Created by Hassan Sayed on 2/11/2018.
 */

public class MoviesPresenter extends BasePresenter<MainView> implements Observer<MoviesResponse> {

    @Inject
    MoviesApiService mApiService;
    @Inject
    MovieLocalDB mStorage;
    private Map<String, String> mParamOptions = new HashMap<>();
    private int totalPages = 0;
    private int startPage = 1;
    private Disposable mDisposable;

    // init the optional parameters with the token
    @Inject
    MoviesPresenter() {
        mParamOptions.put(Const.API_TOKEN, BuildConfig.API_TOKEN);
    }

    /**
     * method used to fetch the data from localdb
     * if movies are found it will be displayed
     * otherwise empty view will be called
     */
    public void getOfflineMovies() {
        List<Movie> movieList = mStorage.getMovies();
        if (Utils.isEmptyList(movieList))
            getView().showOfflineView();
        else
            getView().onMoviesLoaded(movieList);
    }

    // subscribe to observable and load the movies
    public void getMovies() {
        getView().showLoadingProgress();
        Observable<MoviesResponse> moviesPresenterObservable = mApiService.getMovies(mParamOptions);
        subscribe(moviesPresenterObservable, this);
    }

    // increment current paget and load new movies
    public void getMoreMovies() {
        if (totalPages != 0 && startPage < totalPages) {
            mParamOptions.put(Const.REQUEST_PAGE, String.valueOf(startPage));
            getMovies();
        }
    }

    // add filter criteria to the parameters and load the movies
    public void getMoviesFiltered(String startDate) {
        clearLoadedData();
        mParamOptions.put(Const.FILTER_START_DATE, startDate);
        mParamOptions.put(Const.FILTER_END_DATE, Utils.getTodayDate());
        getMovies();
    }

    // reset the current filter and reload data
    public void clearFilter() {
        clearLoadedData();
        getMovies();
    }

    // clear local db and reload movies
    public void forceRefreshMovies() {
        clearLoadedData();
        mStorage.clearMovies();
        getMovies();
    }

    // reset parameters to default
    private void clearLoadedData() {
        mParamOptions.remove(Const.FILTER_START_DATE);
        mParamOptions.remove(Const.FILTER_END_DATE);
        mParamOptions.remove(Const.REQUEST_PAGE);
        totalPages = 0;
        startPage = 1;
        getView().clearRecentMovies();
    }


    @Override
    public void onSubscribe(Disposable disposable) {
        mDisposable = disposable;
    }

    // we got a response , check if has data and draw it
    @Override
    public void onNext(MoviesResponse moviesResponse) {
        getView().hideLoadingProgress();
        if (totalPages == 0)
            totalPages = moviesResponse.getTotalPages();
        if (Utils.isEmptyList(moviesResponse.getResults()))
            getView().onNoMovies();
        else {
            startPage++;
            getView().onMoviesLoaded(moviesResponse.getResults());
            mStorage.addMovies(moviesResponse.getResults(),System.currentTimeMillis());
        }
    }

    // oops , we have an error
    @Override
    public void onError(Throwable e) {
        getView().hideLoadingProgress();
        getView().onRequestError(e);
    }

    @Override
    public void onComplete() {

    }

    // clear the subscription if view is destroyed
    public void dispose() {
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }
}
