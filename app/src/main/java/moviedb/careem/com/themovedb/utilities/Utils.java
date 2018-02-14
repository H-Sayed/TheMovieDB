package moviedb.careem.com.themovedb.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import moviedb.careem.com.themovedb.mvp.model.Movie;

/**
 * Created by Hassan Sayed on 2/11/2018.
 */

public class Utils {

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    // method used to get formatted string for today date to be used in filter
    public static String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        return getFilterDate(calendar);
    }


    // method used to get formatted string for the minimum filter date
    public static String getFilterDate(Calendar calendar) {
        return simpleDateFormat.format(calendar.getTime());
    }

    // check if returned results is empty
    public static boolean isEmptyList(List<Movie> movies) {
        return movies == null || movies.isEmpty();
    }

    // method used to check if today date and last inserted date
    // in db has more then 1 day difference to clear the db
    public static boolean isDirtyCache(long recordDate) {
        Date dbDate = new Date(recordDate);
        Date nowDate = new Date(System.currentTimeMillis());
        long dif = nowDate.getTime() - dbDate.getTime();
        return TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS) >= 1;
    }
}
