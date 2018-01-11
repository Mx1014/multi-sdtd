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
import java.util.*;

/**
 * 类名称：KHYHHISTORYService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/11/30 18:31:34
 * 修改人：张虎成
 * 修改时间：2017/11/30 18:31:34
 * 修改备注：
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
    public WebApiResponse saveYh(KhYhHistory yh, String fxtime, String startTowerName, String endTowerName, String pictureId) {
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
                yh.setRadius("100.0");
                yh.setJd(map.get("LONGITUDE").toString());
                yh.setWd(map.get("LATITUDE").toString());
            }
            if (yh.getVtype().equals("0")) {
                yh.setVtype("35");
            } else if (yh.getVtype().equals("1")) {
                yh.setVtype("110");
            } else if (yh.getVtype().equals("2")) {
                yh.setVtype("220");
            } else if (yh.getVtype().equals("3")) {
                yh.setVtype("550");
            }
            yh.setYhjb("一般");
            yh.setSdgs(1);//手机导入
            yh.setSfdj(0);  //未定级
            yh.setYhzt(0);//隐患未消除
            yh.setCreateTime(DateUtil.dateNow());
            yh.setSection(startTowerName + "-" + endTowerName);
            if (null != pictureId && !pictureId.equals("")) {
                String[] split = pictureId.split(",");
                for (int i = 0; i < split.length; i++) {
                    this.reposiotry.updateYhPicture(Long.parseLong(split[i]), yh.getId(), yh.getXstaskId());
                }
            }
            this.add(yh);
            return WebApiResponse.success("数据保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据保存失败" + e.getMessage());
        }
    }

    public WebApiResponse saveCoordinate(String yhId, String lat, String lon, String radius) {
        try {
            if (!radius.contains(".0")) {
                radius = radius + ".0";
            }
            this.reposiotry.updateYh(Long.parseLong(yhId), lat, lon, radius);
            return WebApiResponse.success("保存成功");
        } catch (Exception e) {
            return WebApiResponse.erro("保存失败" + e.getMessage());
        }
    }

    public WebApiResponse listCoordinate(String yhjb, String yhlb) {
        try {
            StringBuffer buffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            buffer.append(" where 1=1 ");
            if (yhjb != null && yhjb.equals("")) {
                buffer.append(" and yhjb1 like");
                params.add("%" + yhjb + "%");
            }
            if (yhlb != null && yhlb.equals("")) {
                buffer.append(" and yhlb like");
                params.add("%" + yhlb + "%");
            }
            String sql = "select jd,wd from kh_yh_history " + buffer.toString();
            List<Map<String, Object>> list = this.execSql(sql, params.toArray());
            List<Object> list1 = new ArrayList<>();
            for (Map map : list) {
                // System.out.println(map.get("JD").toString());
                if (map != null && map.size() > 0 && !(map.get("JD").toString().equals("null"))) {
                    list1.add(map);
                }
            }
            return WebApiResponse.success(list1);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("获取失败" + e.getMessage());
        }
    }

    public WebApiResponse listYhById(String yhId) {
        try {
            String sql = "select * from kh_yh_history where id=?";
            return WebApiResponse.success(this.execSql(sql,Long.parseLong(yhId)));
        } catch (Exception e) {
            return WebApiResponse.erro("保存失败" + e.getMessage());
        }
    }
}