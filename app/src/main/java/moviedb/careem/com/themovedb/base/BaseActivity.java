package moviedb.careem.com.themovedb.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import moviedb.careem.com.themovedb.R;
import moviedb.careem.com.themovedb.application.MovieDbApplication;
import moviedb.careem.com.themovedb.di.components.ApplicationComponent;

/**
 * Created by Hassan sayed on 2/11/2018.
 * base activity class where every child will inherit
 * Butterknife is used for views injections .
 * any child can resolve the dagger dependency on his own context
 */

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private Unbinder mViewBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        mViewBinder = ButterKnife.bind(this);
        onViewReady(savedInstanceState, getIntent());
    }

    @CallSuper
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        resolveDaggerDependency();
        //To be used by child activities
    }

    @Override
    protected void onDestroy() {
        mViewBinder.unbind();
        super.onDestroy();
    }

    protected void resolveDaggerDependency() {
    }


    protected void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.progress_message_loading));
        }
        mProgressDialog.show();
    }

    protected void hideDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((MovieDbApplication) getApplication()).getApplicationComponent();
    }


    protected abstract int getContentView();
}
