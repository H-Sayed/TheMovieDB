package moviedb.careem.com.themovedb.di.components;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import moviedb.careem.com.themovedb.di.module.ApplicationModule;
import retrofit2.Retrofit;

/**
 * Created by Hassan sayed on 2/11/2018.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    Retrofit exposeRetrofit();

    Context exposeContext();
}
