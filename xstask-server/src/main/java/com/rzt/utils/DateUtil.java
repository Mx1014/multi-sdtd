/**
 *
 */
package com.rzt.utils;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

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
    private static Logger log = Logger.getLogger(DateUtil.class);
    private static final FastDateFormat SDF = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat fdf = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat fdf2 = new SimpleDateFormat("MM-dd");
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static String partterns[] = new String[5];
    private static String partterns1[] = new String[5];

    static {
        partterns[0] = "yyyy-MM-dd HH:mm:ss";
        partterns1[0] = "yyyy-MM-dd HH:mm";
    }


    /**
     * @author LYTG
     * @since 2016-7-2 上午11:47:14
     */
    private  DateUtil(){ }


    public static Date dateNow()  {
        try {
            String str = stringNow();
            return DateUtils.parseDate(str,partterns);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Date();
    }

    public static String stringNow(){
        return SDF.format(new Date());
    }

    /***
    * @Method stringToDate
    * @Description   字符串转日期格式
    * @param [time]
    * @return java.util.Date
    * @date 2017/12/25 13:19
    * @author nwz
    */
    public static Date stringToDate(String time)  {
        try {
            return DateUtils.parseDate(time,partterns);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Date();
    }

    public static Date stringToDate1(String time)  {
        try {
            return sdf2.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Date();
    }

    public static Date StringToDateForImport(String time)  {
        try {
            return fdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Date();
    }
    public static Date StringToDateForImport2(String time)  {
        try {
            return fdf2.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Date();
    }

    public static String dayStringByIndex (int index) {
        Date dNow = new Date();   //当前时间

        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dNow);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, index);  //设置为前一天
        Date dBefore = calendar.getTime();   //得到前一天的时间


        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd "); //设置时间格式
        String format = sdf.format(dBefore);
        return format;

    }
    public static String dayStringByIndex (Date date,int index) {

        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(date);//把参数时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, index);
        Date myDay = calendar.getTime();


        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd "); //设置时间格式
        String format = sdf.format(myDay);
        return format;

    }

    public static String dateToString (Date date) {
        String format = SDF.format(date);
        return format;

    }
}
