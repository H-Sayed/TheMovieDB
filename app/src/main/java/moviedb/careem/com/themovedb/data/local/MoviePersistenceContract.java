package moviedb.careem.com.themovedb.data.local;

import android.provider.BaseColumns;

/**
 * Created by Hassan Sayed on 2/11/2018.
 * Contract class for the movies table
 */

class MoviePersistenceContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private MoviePersistenceContract() {}

    /* Inner class that defines the table contents */
    static abstract class MovieEntry implements BaseColumns {
        static final String TABLE_NAME = "movies";
        static final String COLUMN_NAME_ENTRY_ID = "entryid";
        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_DESCRIPTION = "overview";
        static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        static final String COLUMN_NAME_RATING = "rating";
        static final String COLUMN_NAME_ADULT = "adult";
        static final String COLUMN_NAME_IMAGE = "image";
        static final String COLUMN_NAME_CREATED_AT = "created_at";
    }
}
