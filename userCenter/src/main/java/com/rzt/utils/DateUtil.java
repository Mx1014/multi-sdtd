/**
 *
 */
package com.rzt.utils;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/***
 * @Class DateUtil
 * @Description
 * @param
 * @return
 * @date 2017/12/6 17:42
 * @author nwz
 */
public class DateUtil {
    public static final String FORTER_DATE = "yyyy-MM-dd";   //默认日期格式
    private static Logger log = Logger.getLogger(DateUtil.class);
    private static final FastDateFormat SDF = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
    private static String partterns[] = new String[5];

    static {
        partterns[0] = "yyyy-MM-dd HH:mm:ss";
    }

    public static final Date getNowDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cdate = sdf.format(cal.getTime());
        return cal.getTime();
    }

    public static Date addDate(Date date, double hour) {
        Calendar cal = Calendar.getInstance();
        long hour1 = (long) (hour * 3600000);
        long l = date.getTime() + hour1;
        cal.setTimeInMillis(l);
        date = cal.getTime();
        // return format.format(date);
        return date;
    }

    public static String timeUtil(int i) {
        String date = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        Date m = c.getTime();
        String mon = df.format(m);
        if (i == 1) {
            date = mon + " 00:00";
        } else {
            date = mon + " 23:59";
        }
        //  task.setPlanEndTime(df.format(new Date()) + " 23:59");
        return date;
    }

    /**
     * @author LYTG
     * @since 2016-7-2 上午11:47:14
     */
    private DateUtil() {
    }


    public static Date dateNow() {
        try {
            return DateUtils.parseDate(stringNow(), partterns);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Date();
    }

    public static Date parseDate(String dateTime) {
        try {
            return formatter.parse(dateTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Date();
    }

    public static String stringNow() {
        return SDF.format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getCurrentDate() {
        DateTime dt = new DateTime();
        String date = dt.toString(FORTER_DATE);
        return date;
    }

    /**
     * 获取到天
     *
     * @return
     */
    public static String dateFormatToDay(Date date) {
        return formatter1.format(date);
    }

}
