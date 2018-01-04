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
    public static final String                FORTER_DATE        = "yyyy-MM-dd";                  //默认日期格式
    private static Logger log = Logger.getLogger(DateUtil.class);
    private static final FastDateFormat SDF = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String partterns[] = new String[5];

    static {
        partterns[0] = "yyyy-MM-dd HH:mm:ss";
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
     * @return
     */
    public static String getCurrentDate() {
        DateTime dt = new DateTime();
        String date = dt.toString(FORTER_DATE);
        return date;
    }
    public static Date getPlanStartTime(String startTime){
        try{
            Date planStartTime =  parseDate(startTime);
            String time =  SDF.format(planStartTime);
            return  DateUtils.parseDate(time,partterns);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new Date();

    }

    public static Date getPlanEndTime(String endTime){
        try{
            Date planEndTime =  parseDate(endTime);
            String time =  SDF.format(planEndTime);
            return  DateUtils.parseDate(time,partterns);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Date();

    }
    //返回指定格式的时间
    public static  String getDate(){
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String d = df.format(date);
        String s = "";
        s =  d.substring(0,4)+"年"+d.substring(5,7)+"月"+d.substring(8,10)+"日";
        return s;
    }

    public static Long addDate(String day, int hour){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        System.out.println("front:" + format.format(date)); //显示输入的日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);// 24小时制
        date = cal.getTime();
        System.out.println("after:" + format.format(date));  //显示更新后的日期
        cal = null;
       // return format.format(date);
        return date.getTime();

    }

    public static Long getBiggest(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String d = df.format(new Date());
        String s = d + " 23:59:59";
        return DateUtil.parseDate(s).getTime();
    }

    public static void main(String[] args) {
        System.out.println(getDatePoor(DateUtil.parseDate("2018-01-04 00:00:00"),new Date()));

    }
    public static long getDatePoor(Date endDate, Date nowDate) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return  hour;//day + "天" + hour + "小时" + min + "分钟";
    }
}
