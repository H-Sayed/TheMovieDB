package moviedb.careem.com.themovedb.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import moviedb.careem.com.themovedb.mvp.model.Movie;

/**
 * Created by Hassan Sayed on 2/11/2018.
 */

public class RequestUtilities {

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public static String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getFilterDate(Calendar calendar) {
        return simpleDateFormat.format(calendar.getTime());
    }

    public static boolean isEmptyList(List<Movie> movies) {
        return movies == null || movies.isEmpty();
    }
}
