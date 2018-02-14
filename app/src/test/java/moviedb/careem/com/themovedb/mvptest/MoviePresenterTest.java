package moviedb.careem.com.themovedb.mvptest;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import moviedb.careem.com.themovedb.BuildConfig;
import moviedb.careem.com.themovedb.data.remote.MoviesApiService;
import moviedb.careem.com.themovedb.mvp.model.Movie;
import moviedb.careem.com.themovedb.mvp.model.MoviesResponse;
import moviedb.careem.com.themovedb.data.local.MovieLocalDB;
import moviedb.careem.com.themovedb.mvp.presenter.MoviesPresenter;
import moviedb.careem.com.themovedb.mvp.view.MainView;
import moviedb.careem.com.themovedb.utilities.Const;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by Hassan Sayed on 2/13/2018.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Observable.class, AndroidSchedulers.class, MoviesResponse.class})
public class MoviePresenterTest {


    final String TEST_ERROR_MESSAGE = "Error in internet connection";
    @InjectMocks
    private MoviesPresenter mPresenter;
    @Mock
    private MoviesApiService mApiService;
    @Mock
    private MovieLocalDB mStorage;
    @Mock
    private MainView mView;
    @Mock
    private Observable<MoviesResponse> mObservable;
    private Map<String, String> mParams;


    // since we have no main thread in testing
    // this work around is used to mock this
    @BeforeClass
    public static void setupClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                __ -> Schedulers.trampoline());
    }

    // init mocks
    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mParams = new HashMap<>();
        mParams.put(Const.API_TOKEN, BuildConfig.API_TOKEN);
    }

    // test begin of get movies call
    // expected to show the progress dialog
    @Test
    public void getMovies() throws Exception {
        Mockito.when(mApiService.getMovies(mParams)).thenReturn(mObservable);
        mPresenter.getMovies();
        Mockito.verify(mView, atLeastOnce()).showLoadingProgress();
    }

    // test on complete
    @Test
    public void onCompleted() throws Exception {
        mPresenter.onComplete();
    }

    // test on error received
    // expected is to hide dialog and show error message
    @Test
    public void onError() throws Exception {
        Throwable throwable = new Throwable(TEST_ERROR_MESSAGE);
        mPresenter.onError(throwable);
        Mockito.verify(mView, times(1)).hideLoadingProgress();
        Mockito.verify(mView, times(1)).onRequestError(throwable);
    }

    // test if no movies are found
    // expected to hide dialog and show no movies message
    @Test
    public void testNoMovies() throws Exception {
        MoviesResponse response = Mockito.mock(MoviesResponse.class);
        List<Movie> responseMovies = new ArrayList<>();
        Mockito.when(response.getResults()).thenReturn(responseMovies);
        mPresenter.onNext(response);
        Mockito.verify(mView, times(1)).hideLoadingProgress();
        Mockito.verify(mView, times(1)).onNoMovies();
    }

    // test movies are loaded
    // expected to hide dialog and display the loaded movies
    @Test
    public void testMoviesResult() throws Exception {
        MoviesResponse response = Mockito.mock(MoviesResponse.class);
        List<Movie> responseMovies = new ArrayList<>();
        responseMovies.add(new Movie());
        Mockito.when(response.getResults()).thenReturn(responseMovies);
        mPresenter.onNext(response);
        Mockito.verify(mView, times(1)).hideLoadingProgress();
        Mockito.verify(mView, times(1)).onMoviesLoaded(anyList());
    }

    // test getting movies from storage
    // expected to draw the loaded movies
    @Test
    public void loadMoviesFromStorage() throws Exception {
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie());
        Mockito.when(mStorage.getMovies()).thenReturn(movies);
        mPresenter.getOfflineMovies();
        Mockito.verify(mView, times(1)).onMoviesLoaded(anyList());
    }

    // test loading empty movies from db
    // expected is drawing empty view
    @Test
    public void loadEmptyMoviesFromStorage() throws Exception {
        List<Movie> movies = new ArrayList<>();
        Mockito.when(mStorage.getMovies()).thenReturn(movies);
        mPresenter.getOfflineMovies();
        Mockito.verify(mView, times(1)).showOfflineView();
    }


}
