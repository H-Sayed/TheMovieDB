package moviedb.careem.com.themovedb.di.components;

import dagger.Component;
import moviedb.careem.com.themovedb.modules.movieshome.MainActivity;
import moviedb.careem.com.themovedb.di.module.MovieModule;
import moviedb.careem.com.themovedb.di.scope.PerActivity;

/**
 * Created by Hassan Sayed on 2/11/2018.
 * Component used to inject the main screen activity
 * depending on the application component
 */

@PerActivity
@Component(modules = MovieModule.class, dependencies = ApplicationComponent.class)
public interface MovieComponent {

    void inject(MainActivity activity);
}
