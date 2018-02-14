package moviedb.careem.com.themovedb.base;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import moviedb.careem.com.themovedb.mvp.view.BaseView;

/**
 * Created by Hassan Sayed on 2/11/2018.
 *
 * Generic presenter that will accept any view inherits
 * from @{{@link BaseView\}
 * Presenter has a subscribe method used to register callbacks
 * for network calls to the presenter if needed
 */

public class BasePresenter<V extends BaseView> {


    @Inject
    V mView;

    protected V getView() {
        return mView;
    }

    protected <T> void subscribe(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
