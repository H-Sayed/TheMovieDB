package moviedb.careem.com.themovedb.modules.movieshome.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import moviedb.careem.com.themovedb.BuildConfig;
import moviedb.careem.com.themovedb.R;
import moviedb.careem.com.themovedb.mvp.model.Movie;

/**
 * Created by Hassan Sayed on 2/11/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private List<Movie> movieList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    public MoviesAdapter(LayoutInflater inflater) {
        this.mLayoutInflater = inflater;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cellView = mLayoutInflater.inflate(R.layout.movie_list_cell, parent, false);
        return new MoviesViewHolder(cellView);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        Movie movie = movieList.get(holder.getAdapterPosition());
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void addMovies(List<Movie> movies) {
        this.movieList.addAll(movies);
        notifyDataSetChanged();
    }

    public void clear() {
        this.movieList.clear();
        notifyDataSetChanged();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.moviePoster)
        ImageView mMoviePoster;
        private Movie mMovie;

        MoviesViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        void bind(Movie movie) {
            this.mMovie = movie;
            Glide.with(mMoviePoster).setDefaultRequestOptions(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.mdb_placeholder)).load(BuildConfig.IMG_DIR + movie.getPosterPath())
                    .into(mMoviePoster);
        }

        @Override
        public void onClick(View view) {
            if (mMovieClickListener != null) {
                mMovieClickListener.onClick(mMoviePoster,mMovie);
            }
        }
    }


    public void setOnMovieClickListener(OnMovieClickListener listener) {
        mMovieClickListener = listener;
    }

    private OnMovieClickListener mMovieClickListener;

    public interface OnMovieClickListener {

        void onClick(View v,Movie movie);
    }
}
