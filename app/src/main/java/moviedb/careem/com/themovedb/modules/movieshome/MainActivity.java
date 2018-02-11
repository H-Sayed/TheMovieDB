package moviedb.careem.com.themovedb.modules.movieshome;


import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import butterknife.BindView;
import moviedb.careem.com.themovedb.R;
import moviedb.careem.com.themovedb.base.BaseActivity;
import moviedb.careem.com.themovedb.di.components.DaggerMovieComponent;
import moviedb.careem.com.themovedb.di.module.MovieModule;
import moviedb.careem.com.themovedb.modules.moviesdetails.DetailActivity;
import moviedb.careem.com.themovedb.modules.movieshome.adapter.MoviesAdapter;
import moviedb.careem.com.themovedb.mvp.model.Movie;
import moviedb.careem.com.themovedb.mvp.presenter.MoviesPresenter;
import moviedb.careem.com.themovedb.mvp.view.MainView;
import moviedb.careem.com.themovedb.utilities.NetworkUtils;
import moviedb.careem.com.themovedb.utilities.RequestUtilities;

public class MainActivity extends BaseActivity implements MainView {

    @Inject
    protected MoviesPresenter mPresenter;
    @BindView(R.id.moviesRecycler)
    protected RecyclerView mMoviesList;
    @BindView(R.id.emptyView)
    protected TextView mEmptyView;
    private MoviesAdapter mAdapter;
    private MenuItem mFilterMenuItem;

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        initializeList();
        if (NetworkUtils.isNetAvailable(this))
            mPresenter.getMovies();
        else
            mPresenter.getOfflineMovies();
    }

    private void initializeList() {
        mMoviesList.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mMoviesList.setLayoutManager(manager);
        mAdapter = new MoviesAdapter(getLayoutInflater());
        mAdapter.setOnMovieClickListener(mMovieClickListener);
        mMoviesList.setAdapter(mAdapter);
        mMoviesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastPosition = manager
                        .findLastCompletelyVisibleItemPosition();
                if (lastPosition == mAdapter.getItemCount() - 1) {
                    mPresenter.getMoreMovies();
                }
            }
        });
    }

    @Override
    protected void resolveDaggerDependency() {
        DaggerMovieComponent.builder()
                .applicationComponent(getApplicationComponent())
                .movieModule(new MovieModule(this))
                .build().inject(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }


    @Override
    public void showLoadingProgress() {
        showDialog();
    }

    @Override
    public void hideLoadingProgress() {
        hideDialog();
    }

    @Override
    public void onMoviesLoaded(List<Movie> movies) {
        if (mEmptyView.getVisibility() == View.VISIBLE)
            mEmptyView.setVisibility(View.GONE);
        mAdapter.addMovies(movies);
    }

    @Override
    public void onNoMovies() {
        onError(getString(R.string.generic_empty_movie_list));
    }

    @Override
    public void clearRecentMovies() {
        mAdapter.clear();
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestError(Throwable throwable) {
        onError(throwable.getLocalizedMessage());
    }

    @Override
    public void showOfflineView() {
        mEmptyView.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_filter_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mFilterMenuItem = menu.findItem(R.id.action_filter);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!NetworkUtils.isNetAvailable(this)) {
            onError(getString(R.string.generic_connection_error));
            return true;
        }
        if (item.getItemId() == R.id.action_filter) {
            boolean isActiveFilter = item.getIcon().getConstantState().equals(
                    getResources().getDrawable(R.drawable.ic_filter_active).getConstantState());
            if (isActiveFilter) {
                mFilterMenuItem.setIcon(R.drawable.ic_filter_not_active);
                mPresenter.clearFilter();
            } else
                showDatePicker();
        } else if (item.getItemId() == R.id.action_refresh) {
            mPresenter.forceRefreshMovies();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mPresenter.dispose();
        super.onDestroy();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog startDateDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            mPresenter.getMoviesFiltered(RequestUtilities.getFilterDate(newDate));
            mFilterMenuItem.setIcon(R.drawable.ic_filter_active);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        startDateDialog.show();
    }

    private MoviesAdapter.OnMovieClickListener mMovieClickListener = (v, movie) -> {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.MOVIE, movie);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, v, "cakeImageAnimation");
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    };


}
