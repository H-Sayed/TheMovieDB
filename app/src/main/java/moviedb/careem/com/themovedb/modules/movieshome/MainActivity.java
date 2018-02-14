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
import java.util.Date;
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
import moviedb.careem.com.themovedb.utilities.Utils;

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
        // if we have active internet connection then call api
        // otherwise load from cache if exist
        if (NetworkUtils.isNetAvailable(this))
            mPresenter.getMovies();
        else
            mPresenter.getOfflineMovies();
    }

    // setup the recyclerview and add onScroll Observer for loading more operations
    private void initializeList() {
        mMoviesList.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mMoviesList.setLayoutManager(manager);
        mAdapter = new MoviesAdapter(getLayoutInflater());
        mAdapter.setOnMovieClickListener(mMovieClickListener);
        mMoviesList.setAdapter(mAdapter);
        mMoviesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            // if we reached the latest item in the recycler view and it is fully visible load more will be called

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

    // setup dagger depenedency
    @Override
    protected void resolveDaggerDependency() {
        DaggerMovieComponent.builder()
                .applicationComponent(getApplicationComponent())
                .movieModule(new MovieModule(this))
                .build().inject(this);
    }

    // return activity layout
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

    // hide the empty view if visible and notify the recycler adapter
    @Override
    public void onMoviesLoaded(List<Movie> movies) {
        if (mEmptyView.getVisibility() == View.VISIBLE)
            mEmptyView.setVisibility(View.GONE);
        mAdapter.addMovies(movies);
    }

    // ooops , we have no movies
    @Override
    public void onNoMovies() {
        onError(getString(R.string.generic_empty_movie_list));
    }

    // reset adapter to default
    @Override
    public void clearRecentMovies() {
        mAdapter.clear();
    }

    // show any error in form of toasts
    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // oops , we have an exception in the url
    @Override
    public void onRequestError(Throwable throwable) {
        onError(throwable.getLocalizedMessage());
    }

    // we don't have internet connection and no movies in cache
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


    /**
     * method used to handle the action on filter and refresh
     * the menu item filter has to states active and default , user
     * can switch between them if not active date picker will be shown
     * otherwise it will clear the current filter.
     * the refresh item will be loaded to resend the request to api
     * and reset the current local db
     *
     * @param item menu item
     * @return
     */
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

    /**
     * method used to show date picker dialog to show the filter start date to get movies
     * the api takes start release date and the end release date will be today, for ux concerns
     * filter will show only dates till yesterday to allow minimum of 1 day filter criteria
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog startDateDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            mPresenter.getMoviesFiltered(Utils.getFilterDate(newDate));
            mFilterMenuItem.setIcon(R.drawable.ic_filter_active);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        long oneDayBefore = 24 * 60 * 60 * 1000;
        startDateDialog.getDatePicker().setMaxDate(new Date().getTime()-oneDayBefore);
        startDateDialog.show();
    }

    // when user click on an item start shared element animation on the image view
    // ans start the details view
    private MoviesAdapter.OnMovieClickListener mMovieClickListener = (v, movie) -> {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.MOVIE, movie);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, v, "movieImageAnimation");
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    };


}
