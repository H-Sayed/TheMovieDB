package moviedb.careem.com.themovedb.modules.moviesdetails;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import butterknife.BindView;
import moviedb.careem.com.themovedb.BuildConfig;
import moviedb.careem.com.themovedb.R;
import moviedb.careem.com.themovedb.base.BaseActivity;
import moviedb.careem.com.themovedb.mvp.model.Movie;

/**
 * Created by Hassan Sayed on 2/11/2018.
 */

public class DetailActivity extends BaseActivity {

    public static final String MOVIE = "movie";
    @BindView(R.id.moviePoster)
    protected ImageView mMoviePoster;
    @BindView(R.id.movieTitle)
    protected TextView mMovieTitle;
    @BindView(R.id.movieRating)
    protected TextView mMovieRating;
    @BindView(R.id.movieDate)
    protected TextView mMovieDate;
    @BindView(R.id.movieGenre)
    protected TextView mMovieGenre;
    @BindView(R.id.movieDesc)
    protected TextView mMovieDesc;


    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMoviePoster.setTransitionName("cakeImageAnimation");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setMovie();
    }

    private void setMovie() {
        Movie movie = getIntent().getExtras().getParcelable(MOVIE);
        Glide.with(mMoviePoster).setDefaultRequestOptions(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).load(BuildConfig.IMG_DIR + movie.getPosterPath())
                .into(mMoviePoster);
        setTitle(movie.getTitle());
        mMovieTitle.setText(movie.getTitle());
        mMovieRating.setText(String.valueOf(movie.getVoteAverage()));
        mMovieDate.setText(movie.getReleaseDate());
        mMovieGenre.setText(movie.isAdult() ? getString(R.string.genre_type_adults) : getString(R.string.genre_type_all));
        mMovieDesc.setText(movie.getOverview());
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_details;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
