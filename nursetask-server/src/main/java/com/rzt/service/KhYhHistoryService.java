/**
 * 文件名：KHYHHISTORYService
 * 版本信息：
 * 日期：2017/11/30 18:31:34
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.repository.KhYhHistoryRepository;
import com.rzt.entity.KhYhHistory;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 类名称：KHYHHISTORYService    
 * 类描述：${table.comment}
 * 创建人：张虎成   
 * 创建时间：2017/11/30 18:31:34 
 * 修改人：张虎成    
 * 修改时间：2017/11/30 18:31:34    
 * 修改备注：    
 * @version
 */
@Service
public class KhYhHistoryService extends CurdService<KhYhHistory, KhYhHistoryRepository> {


    public WebApiResponse list() {
        try {

            return WebApiResponse.success("");
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse listYhCount() {
        try {
            String date = DateUtil.getCurrentDate();
            String handlesql = "(SELECT COUNT(*) as count FROM KH_YH_HISTORY WHERE to_char(YHXQ_TIME) >=? and to_char(YHXQ_TIME) <=?)  a,";
            String addedsql = "(SELECT COUNT(*) as count FROM KH_YH_HISTORY WHERE to_char(create_time) >=? and to_char(create_time) <=?) b,";
            String updateSql = "(SELECT COUNT(*) as count FROM KH_YH_HISTORY WHERE to_char(update_time) >=? and to_char(update_time) <=?) c,";
            String allSql = "(select count(*) as count from kh_yh_history where yhzt=0 ) d";
            String sql = "select a.count as handle,b.count as addcount,c.count as updatecount,d.count as allcount from " +
                    handlesql + addedsql + updateSql + allSql;
            System.out.println(sql);
            return WebApiResponse.success(this.execSql(sql, date + " 00:00:00", date + " 23:59:59", date + " 00:00:00", date + " 23:59:59", date + " 00:00:00", date + " 23:59:59"));
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取失败");
        }
    }


    public WebApiResponse listSgCount() {
        try {
            String doingSql = "(select count(*) as count from kh_yh_history where sgqk = 1) a,";
            String doneSql = "(select count(*) as count from kh_yh_history where sgqk = 2) b,";
            String sql = "select a.count as doing,b.count as done from "+doingSql+doneSql;
            return WebApiResponse.success("");
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse listGkqk() {
        try {
            String doingSql = "select count(*) from  left join ";
            String doneSql = "(select count(*) as count from kh_yh_history where sgqk = 2) b,";
            String sql = "select a.count as doing,b.count as done from "+doingSql+doneSql;
            return WebApiResponse.success("");
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取失败");
        }
    }
}