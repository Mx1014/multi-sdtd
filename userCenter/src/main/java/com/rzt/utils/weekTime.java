package com.rzt.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class weekTime {
    public static Map weekTime() {
        Map map = new HashMap();
        try {
            Calendar cal = Calendar.getInstance();
            String data = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(data));
            int d = 0;
            if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
                d = -6;
            } else {
                d = 2 - cal.get(Calendar.DAY_OF_WEEK);
            }
            cal.add(Calendar.DAY_OF_WEEK, d);
            //所在周开始日期
            map.put("Mon", new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(cal.getTime()));
            cal.add(Calendar.DAY_OF_WEEK, 6);
            //所在周结束日期
            map.put("Sun", new SimpleDateFormat("yyyy-MM-dd 23:59:59").format(cal.getTime()));
            return map;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取本月第一天
    public static String getFirstDayOfMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String format1 = format.format(calendar.getTime());
        return format1;
    }
    //获取本月最后一天
    public static String getLastDayOfMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String format2 = format.format(calendar.getTime());
        return format2;
    }

    //获取今年是哪一年
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }
    //获取本月是哪一月
    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

}
