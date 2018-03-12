package com.rzt.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
}
