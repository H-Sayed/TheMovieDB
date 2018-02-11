package moviedb.careem.com.themovedb.di.module;

import dagger.Module;
import dagger.Provides;
import moviedb.careem.com.themovedb.api.MoviesApiService;
import moviedb.careem.com.themovedb.di.scope.PerActivity;
import moviedb.careem.com.themovedb.mvp.view.MainView;
import retrofit2.Retrofit;

/**
 * Created by Hassan Sayed on 2/11/2018.
 */
@Module
public class MovieModule {

    private MainView mView;

    public MovieModule(MainView view) {
        mView = view;
    }

    @PerActivity
    @Provides
    MoviesApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(MoviesApiService.class);
    }

    @PerActivity
    @Provides
    MainView provideView() {
        return mView;
    }
}
