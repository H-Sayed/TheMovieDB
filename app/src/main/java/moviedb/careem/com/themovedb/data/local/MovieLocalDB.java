package moviedb.careem.com.themovedb.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import moviedb.careem.com.themovedb.mvp.model.Movie;
import moviedb.careem.com.themovedb.utilities.Utils;

/**
 * Created by Hassan Sayed on 2/11/2018.
 * SQLITE helper to save the movies to be viewed offline
 * local db is just a demonstration of local storage
 * DB will save maximum of 60 records
 * If the saved movies saved with day before today it will be cleared
 * or if the user hit the refresh button in home screen it will force delete
 */

public class MovieLocalDB extends SQLiteOpenHelper {


    private static final int MAX_RECORDS = 60;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Movie.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String LONG_TYPE = " LONG";
    private static final String COMMA_SEP = ",";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MoviePersistenceContract.MovieEntry.TABLE_NAME + " (" +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_ENTRY_ID + INTEGER_TYPE + " PRIMARY KEY autoincrement not null," +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_RATING + FLOAT_TYPE + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_IMAGE + TEXT_TYPE + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_CREATED_AT + LONG_TYPE + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_ADULT + INTEGER_TYPE +
                    " )";

    private static final String SELECT_QUERY = "select * from " + MoviePersistenceContract.MovieEntry.TABLE_NAME;
    private static final String DELETE_QUERY = "delete  from " + MoviePersistenceContract.MovieEntry.TABLE_NAME;


    @Inject
    public MovieLocalDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * method used to insert bulk of movies returned from the remote repository
     * @param movies  list of movies
     * @param addTime time to be saved as the update date for every record
     */
    public void addMovies(List<Movie> movies, long addTime) {
        if (!isAllowedQuery())
            return;
        SQLiteDatabase db = this.getWritableDatabase();
        for (Movie movie : movies) {
            ContentValues contentValues = getMovieContent(movie, addTime);
            db.insert(MoviePersistenceContract.MovieEntry.TABLE_NAME, null, contentValues);
        }
        db.close();
    }


    /**
     * method used to prepare content values from the movie bean
     * to be inserted in movies table
     * @param movie movie to be inserted
     * @param time  update time
     * @return ContentValue with movie properties
     */
    private ContentValues getMovieContent(Movie movie, long time) {
        ContentValues values = new ContentValues();
        values.put(MoviePersistenceContract.MovieEntry.COLUMN_NAME_TITLE, movie.getTitle());
        values.put(MoviePersistenceContract.MovieEntry.COLUMN_NAME_DESCRIPTION, movie.getOverview());
        values.put(MoviePersistenceContract.MovieEntry.COLUMN_NAME_RATING, movie.getVoteAverage());
        values.put(MoviePersistenceContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, movie.getReleaseDate());
        values.put(MoviePersistenceContract.MovieEntry.COLUMN_NAME_ADULT, movie.isAdult() ? 1 : 0);
        values.put(MoviePersistenceContract.MovieEntry.COLUMN_NAME_IMAGE, movie.getPosterPath());
        values.put(MoviePersistenceContract.MovieEntry.COLUMN_NAME_CREATED_AT, time);
        return values;
    }

    /**
     * method used to clear all the movies table records
     */
    public void clearMovies() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE_QUERY);
        db.close();
    }

    /**
     * method used to return the saved movie in db to be loaded offline
     * maximum query size is 60 as the maximum records supported
     * @return list of movies
     */
    public List<Movie> getMovies() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Movie> savedMovies = new ArrayList<>();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Movie movie = new Movie();
                        movie.setTitle(cursor.getString(cursor.getColumnIndex(MoviePersistenceContract.MovieEntry.COLUMN_NAME_TITLE)));
                        movie.setOverview(cursor.getString(cursor.getColumnIndex(MoviePersistenceContract.MovieEntry.COLUMN_NAME_DESCRIPTION)));
                        movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviePersistenceContract.MovieEntry.COLUMN_NAME_RELEASE_DATE)));
                        movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviePersistenceContract.MovieEntry.COLUMN_NAME_IMAGE)));
                        movie.setAdult(cursor.getInt(cursor.getColumnIndex(MoviePersistenceContract.MovieEntry.COLUMN_NAME_ADULT)) == 1);
                        movie.setVoteAverage(cursor.getFloat(cursor.getColumnIndex(MoviePersistenceContract.MovieEntry.COLUMN_NAME_RATING)));
                        savedMovies.add(movie);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

        }

        return savedMovies;
    }

    /**
     * method used to check if the database last transaction is expired
     * then the content will be cleared to add new records otherwise
     * it will check for the number of saved records if reached the maximum of not
     * @return if insertion is allowed
     */
    public boolean isAllowedQuery() {
        boolean allowedQuery;
        int recordCount = 0;
        boolean shouldClear = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if (cursor != null && cursor.getCount() > 0) {
            recordCount = cursor.getCount();
            if (cursor.moveToLast()) {
                long updateDate = cursor.getLong(cursor.getColumnIndex(MoviePersistenceContract.MovieEntry.COLUMN_NAME_CREATED_AT));
                shouldClear = Utils.isDirtyCache(updateDate);
            }
            cursor.close();
        }
        if (shouldClear) {
            db.execSQL(DELETE_QUERY);
            recordCount = 0;
        }
        db.close();
        allowedQuery = recordCount < MAX_RECORDS;
        return allowedQuery;
    }


}
