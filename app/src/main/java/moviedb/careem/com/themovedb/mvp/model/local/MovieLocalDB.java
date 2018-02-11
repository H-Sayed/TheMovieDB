package moviedb.careem.com.themovedb.mvp.model.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import moviedb.careem.com.themovedb.mvp.model.Movie;

/**
 * Created by Hassan Sayed on 2/11/2018.
 */

public class MovieLocalDB extends SQLiteOpenHelper {

    public static final int MAX_RECORDS = 60;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Movie.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String COMMA_SEP = ",";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MoviePersistenceContract.MovieEntry.TABLE_NAME + " (" +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_ENTRY_ID + INTEGER_TYPE + " PRIMARY KEY autoincrement not null," +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_RATING + FLOAT_TYPE + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COLUMN_NAME_IMAGE + TEXT_TYPE + COMMA_SEP +
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

    public void addMovies(List<Movie> movies) {
        if (getRecordCount() == MAX_RECORDS)
            return;
        SQLiteDatabase db = this.getWritableDatabase();
        for (Movie movie : movies) {
            ContentValues contentValues = getMovieContent(movie);
            db.insert(MoviePersistenceContract.MovieEntry.TABLE_NAME, null, contentValues);
        }
        db.close();
    }

    private ContentValues getMovieContent(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(MoviePersistenceContract.MovieEntry.COLUMN_NAME_TITLE, movie.getTitle());
        values.put(MoviePersistenceContract.MovieEntry.COLUMN_NAME_DESCRIPTION, movie.getOverview());
        values.put(MoviePersistenceContract.MovieEntry.COLUMN_NAME_RATING, movie.getVoteAverage());
        values.put(MoviePersistenceContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, movie.getReleaseDate());
        values.put(MoviePersistenceContract.MovieEntry.COLUMN_NAME_ADULT, movie.isAdult() ? 1 : 0);
        values.put(MoviePersistenceContract.MovieEntry.COLUMN_NAME_IMAGE, movie.getPosterPath());
        return values;
    }

    public void clearMovies() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE_QUERY);
        db.close();
    }

    public List<Movie> getMovies() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Movie> savedMovies = new ArrayList<>();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Movie cake = new Movie();
                        cake.setTitle(cursor.getString(cursor.getColumnIndex(MoviePersistenceContract.MovieEntry.COLUMN_NAME_TITLE)));
                        cake.setOverview(cursor.getString(cursor.getColumnIndex(MoviePersistenceContract.MovieEntry.COLUMN_NAME_DESCRIPTION)));
                        cake.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviePersistenceContract.MovieEntry.COLUMN_NAME_RELEASE_DATE)));
                        cake.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviePersistenceContract.MovieEntry.COLUMN_NAME_IMAGE)));
                        cake.setAdult(cursor.getInt(cursor.getColumnIndex(MoviePersistenceContract.MovieEntry.COLUMN_NAME_ADULT)) == 1);
                        cake.setVoteAverage(cursor.getFloat(cursor.getColumnIndex(MoviePersistenceContract.MovieEntry.COLUMN_NAME_RATING)));
                        savedMovies.add(cake);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

        }

        return savedMovies;
    }

    public int getRecordCount() {
        int recordCount = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if (cursor != null) {
            recordCount = cursor.getCount();
            cursor.close();
        }
        db.close();
        return recordCount;
    }


}
