package moviedb.careem.com.themovedb.instrumentdb;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import moviedb.careem.com.themovedb.mvp.model.Movie;
import moviedb.careem.com.themovedb.data.local.MovieLocalDB;

import static org.junit.Assert.*;


/**
 * Created by Hassan Sayed on 2/13/2018.
 * instrumental test for the local db
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MovieLocalDbTest {

    private MovieLocalDB mDataSource;

    // load the storage
    @Before
    public void setUp() {
        mDataSource = new MovieLocalDB(InstrumentationRegistry.getTargetContext());
    }

    // test if the storage is loaded
    @Test
    public void testPreConditions() {
        assertNotNull(mDataSource);
    }

    // test clear movies table
    // expected get movies to return empty list
    @Test
    public void testDeleteAll() {
        mDataSource.clearMovies();
        List<Movie> rate = mDataSource.getMovies();
        assertEquals(rate.size(), 0);
    }


    // test add record to db
    // expected get movies method return non empty list
    @Test
    public void testAddRecords() throws Exception {
        List<Movie> movies = new ArrayList<>();
        Movie movie = new Movie();
        movie.setTitle("First Movie");
        movie.setOverview("Movie Overview");
        movie.setAdult(true);
        movie.setVoteAverage(7);
        movie.setReleaseDate("2018/02/13");
        movie.setPosterPath("dummy path");
        movies.add(movie);
        mDataSource.addMovies(movies, System.currentTimeMillis());
        movies.clear();
        movies = mDataSource.getMovies();
        assertNotNull(movies);
    }

    // test if insert query is allowed if we have no records
    // expected is true
    @Test
    public void testAllowedQuery() throws Exception {
        mDataSource.clearMovies();
        assertTrue(mDataSource.isAllowedQuery());
    }

    // test if insert query is allowed if date saved with the records are old
    // expected is true
    @Test
    public void notAllowedQueryForMaxRecord() throws Exception {
        List<Movie> movies = new ArrayList<>();
        long expiredDate = System.currentTimeMillis() - (25 * 60 * 60 * 1000);
        for (int i = 0; i < 60; i++) {
            Movie movie = new Movie();
            movie.setTitle("First Movie");
            movie.setOverview("Movie Overview");
            movie.setAdult(true);
            movie.setVoteAverage(7);
            movie.setReleaseDate("2018/02/13");
            movie.setPosterPath("dummy path");
            movies.add(movie);
        }
        mDataSource.addMovies(movies, expiredDate);
        assertTrue(mDataSource.isAllowedQuery());
    }



}
