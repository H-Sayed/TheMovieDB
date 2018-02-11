package moviedb.careem.com.themovedb.application;

import android.app.Application;

import moviedb.careem.com.themovedb.di.components.ApplicationComponent;
import moviedb.careem.com.themovedb.di.components.DaggerApplicationComponent;
import moviedb.careem.com.themovedb.di.module.ApplicationModule;

/**
 * Created by Hassan Sayed on 2/11/2018.
 */

public class MovieDbApplication extends Application{

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeApplicationComponent();
    }

    private void initializeApplicationComponent() {
        mApplicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }
}
