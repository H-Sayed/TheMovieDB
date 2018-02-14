package moviedb.careem.com.themovedb.di.module;

import android.content.Context;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import moviedb.careem.com.themovedb.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hassan Sayed on 2/11/2018.
 */

@Module
public class ApplicationModule {

    private Context mContext;
    public ApplicationModule(Context context) {
        mContext = context;
    }

    /**
     * builder for  json conversion
     * @return Gson factory
     */
    @Singleton
    @Provides
    GsonConverterFactory provideGsonConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Singleton
    @Provides
    @Named("ok-1")
    OkHttpClient provideOkHttpClient1() {
        return new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    /**
     * we can provide different http client with different criteria
     * @return Http client with 60 seconds timeout for requests
     */
    @Singleton
    @Provides
    @Named("ok-2")
    OkHttpClient provideOkHttpClient2() {
        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    /**
     * build synchronous observables which is not  operating on any scheduler by default
     * @return RAadapter
     */
    @Singleton
    @Provides
    RxJava2CallAdapterFactory provideCallAdapter() {
        return RxJava2CallAdapterFactory.create();
    }


    /**
     * build retrofit client based on the provided builders
     * @param client OkHttp client
     * @param converterFactory Gson factory
     * @param adapterFactory adapterFactory
     * @param url server url
     * @return Retrofit client
     */
    @Singleton
    @Provides
    Retrofit provideRetrofit(@Named("ok-2") OkHttpClient client, GsonConverterFactory converterFactory, RxJava2CallAdapterFactory adapterFactory,String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(adapterFactory)
                .client(client)
                .build();
    }

    /**
     * providing the application context
     * @return App context
     */
    @Provides
    @Singleton
    Context provideContext() {
        return mContext;
    }

    /**
     * return moviedb url used in loading movies
     * @return server url
     */
    @Provides
    @Singleton
    String getBaseUrl() {
        return BuildConfig.API_END_POINT;
    }
}
