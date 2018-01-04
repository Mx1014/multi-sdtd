/**
 *
 */
package com.rzt.utils;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private static String partterns[] = new String[5];

    static {
        partterns[0] = "yyyy-MM-dd HH:mm:ss";
    }


    /**
     * @author LYTG
     * @since 2016-7-2 上午11:47:14
     */
    private  DateUtil(){ }


    public static Date dateNow()  {
        try {
            return DateUtils.parseDate(stringNow(),partterns);
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

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parse = sdf.parse("2017-12-30 00:00:00");
        System.out.println(parse);
    }

}
