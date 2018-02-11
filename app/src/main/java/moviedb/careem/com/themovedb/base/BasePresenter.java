package moviedb.careem.com.themovedb.base;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import moviedb.careem.com.themovedb.mvp.view.BaseView;

/**
 * Created by Hassan Sayed on 2/11/2018.
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
