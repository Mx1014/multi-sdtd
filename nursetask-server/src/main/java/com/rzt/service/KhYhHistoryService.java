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
import com.rzt.utils.MapUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

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

    @Transactional
    public WebApiResponse saveYh(KhYhHistory yh,String fxtime,String startTowerName,String endTowerName,String pictureId) {
        try {
            yh.setYhfxsj(DateUtil.dateNow());
            //yh.setYhfxsj(DateUtil.parseDate(fxtime));
            yh.setId(0l);
            if (!yh.getStartTower().isEmpty()) {
                String startTower = "select longitude,latitude from cm_tower where id = ?";
                String endTower = "select longitude,latitude from cm_tower where id = ?";
                Map<String, Object> map = execSqlSingleResult(startTower, Integer.parseInt(yh.getStartTower()));
                Map<String, Object> map1 = execSqlSingleResult(endTower, Integer.parseInt(yh.getEndTower()));
                //经度
                double jd = (Double.parseDouble(map.get("LONGITUDE").toString()) + Double.parseDouble(map1.get("LONGITUDE").toString())) / 2;
                double wd = (Double.parseDouble(map.get("LATITUDE").toString()) + Double.parseDouble(map1.get("LATITUDE").toString())) / 2;
                double radius = MapUtil.GetDistance(Double.parseDouble(map.get("LONGITUDE").toString()), Double.parseDouble(map.get("LATITUDE").toString()), Double.parseDouble(map1.get("LONGITUDE").toString()), Double.parseDouble(map1.get("LATITUDE").toString())) / 2;
                yh.setRadius(radius+"");
                yh.setJd(jd+"");
                yh.setWd(wd+"");
            }
            yh.setYhjb("一般");
            yh.setSdgs(1);//手机导入
            yh.setSfdj(0);  //未定级
            yh.setYhzt(0);//隐患未消除
            yh.setCreateTime(DateUtil.dateNow());
            yh.setSection(startTowerName + "-" + endTowerName);
            if (null != pictureId || "" != pictureId){
                String[] split = pictureId.split(",");
                for (int i =0; i < split.length;i++){
                    this.reposiotry.updateYhPicture(Long.parseLong(split[i]),yh.getId(),yh.getXstaskId());
                }
            }
            this.add(yh);
            return WebApiResponse.success("数据保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据保存失败");
        }
    }

   /* public WebApiResponse listYhCount() {
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
    }*/
}