package utils;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestUtils {
    public  static final long WAIT=10;

    public static String DAY_MONTH_YEAR = "dd/MM/yyyy";
    public static String MONTH_DAY_YEAR = "MM/dd/yyyy";
    public static String YEAR_MONTH_DAY = "yyyy/MM/dd";

    /**
     * @param numberOfDays
     *            get number of days to move after , before or stay in the current
     *            day from story
     * @param format
     *            get the date format from class to set the date type
     * @return the past,future or stay in current date
     */
    public static String getDateInThePastOrFuture(int numberOfDays, String format) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, numberOfDays);
        Date pastOrFeautureDate = cal.getTime();
        String dateInThePastOrFuture = getDateFormat(format).format(pastOrFeautureDate);
        return dateInThePastOrFuture;

    }

    public static DateFormat getDateFormat(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat;

    }

    public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public  void sleepTime(int time) {
        try {
            Thread.currentThread().sleep(time * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

     static JsonObject jsonObject = new JsonObject();

    public void setImageBase64(String key,String value){
        jsonObject.addProperty(key, value);
    }

    public String getImageBase64(String key){
      String valueReplace=jsonObject.get(key).toString();
     return valueReplace.substring(1,valueReplace.length()-1);
    }




    public Logger log() {
        return LogManager.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
    }

}
