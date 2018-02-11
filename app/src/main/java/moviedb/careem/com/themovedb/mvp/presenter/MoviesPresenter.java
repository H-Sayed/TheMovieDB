package moviedb.careem.com.themovedb.mvp.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import moviedb.careem.com.themovedb.BuildConfig;
import moviedb.careem.com.themovedb.api.MoviesApiService;
import moviedb.careem.com.themovedb.base.BasePresenter;
import moviedb.careem.com.themovedb.mvp.model.Movie;
import moviedb.careem.com.themovedb.mvp.model.MoviesResponse;
import moviedb.careem.com.themovedb.mvp.model.local.MovieLocalDB;
import moviedb.careem.com.themovedb.mvp.view.MainView;
import moviedb.careem.com.themovedb.utilities.Const;
import moviedb.careem.com.themovedb.utilities.RequestUtilities;

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

    @Inject
    MoviesPresenter() {
        mParamOptions.put(Const.API_TOKEN, BuildConfig.API_TOKEN);
    }

    public void getOfflineMovies() {
        List<Movie> movieList = mStorage.getMovies();
        if (RequestUtilities.isEmptyList(movieList))
            getView().showOfflineView();
        else
            getView().onMoviesLoaded(movieList);
    }

    public void getMovies() {
        getView().showLoadingProgress();
        Observable<MoviesResponse> moviesPresenterObservable = mApiService.getMovies(mParamOptions);
        subscribe(moviesPresenterObservable, this);
    }

    public void getMoreMovies() {
        if (totalPages != 0 && startPage < totalPages) {
            mParamOptions.put(Const.REQUEST_PAGE, String.valueOf(startPage));
            getMovies();
        }
    }

    public void getMoviesFiltered(String startDate) {
        clearLoadedData();
        mParamOptions.put(Const.FILTER_START_DATE, startDate);
        mParamOptions.put(Const.FILTER_END_DATE, RequestUtilities.getTodayDate());
        getMovies();
    }

    public void clearFilter() {
        clearLoadedData();
        getMovies();
    }

    public void forceRefreshMovies() {
        clearLoadedData();
        mStorage.clearMovies();
        getMovies();
    }

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

    @Override
    public void onNext(MoviesResponse moviesResponse) {
        getView().hideLoadingProgress();
        if (totalPages == 0)
            totalPages = moviesResponse.getTotalPages();
        if (RequestUtilities.isEmptyList(moviesResponse.getResults()))
            getView().onNoMovies();
        else {
            startPage++;
            getView().onMoviesLoaded(moviesResponse.getResults());
            mStorage.addMovies(moviesResponse.getResults());
        }
    }

    @Override
    public void onError(Throwable e) {
        getView().hideLoadingProgress();
        getView().onRequestError(e);
    }

    @Override
    public void onComplete() {

    }

    public void dispose() {
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }
}
