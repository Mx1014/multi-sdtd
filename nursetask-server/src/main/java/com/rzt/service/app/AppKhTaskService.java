package com.rzt.service.app;

import com.rzt.entity.KhTask;
import com.rzt.entity.KhTaskWpqr;
import com.rzt.repository.AppKhTaskRepository;
import com.rzt.repository.KhTaskWpqrRepository;
import com.rzt.service.CurdService;
import com.rzt.service.KhTaskWpqrService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.Constances;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/12/17.
 */
@Service
public class AppKhTaskService extends CurdService<KhTask, AppKhTaskRepository> {

    @Autowired
    private KhTaskWpqrService wpqrService;

    public Page<Map<String, Object>> appListkhTask(int dbyb, Pageable pageable, String userId) {
        String result = " k.id as taskId,k.plan_start_time as startTime,k.task_name as taskName,k.status as status,u.realname as name,k.zxys_num as num";
        StringBuffer buffer = new StringBuffer();
        if (dbyb == 1) {
            buffer.append("where (status like '未开始' or status like '进行中')");
        } else if (dbyb == 2) {
            buffer.append(" where status like '已完成'");
        }
        String sql = "select " + result + " from kh_task k left join rztsysuser u on u.id = k.user_id " + buffer.toString() + " and user_id = ?";
        return this.execSqlPage(pageable, sql, userId);
    }

    public WebApiResponse appListkhTaskById(String taskId) {
        try {
            String result = "K.TASK_NAME AS TASKNAME,H.YHMS AS MS,H.YHJB AS JB,K.PLAN_START_TIME AS STARTTIME,K.PLAN_END_TIME AS ENDTIME,K.STATUS AS STATUS ";
            String sql = "SELECT " + result + " FROM KH_TASK k LEFT JOIN KH_YH_HISTORY H on k.yh_id = h.id WHERE K.ID=?";
            return WebApiResponse.success(this.execSql(sql, Long.parseLong(taskId)));
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse appListUserInfoById(String userId, String taskId) {
        try {
            String result = "u.realname as name,d.DEPTNAME as class,u.phone as phone";
            String sql = "select " + result + " from rztsysuser u left join RZTSYSDEPARTMENT d on u.classname = d.id where u.id=? ";
            return WebApiResponse.success(this.execSql(sql, userId));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse appSavePhoto(String userId, String taskId) {
        try {
            return WebApiResponse.success("身份已经确认");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse appSaveWpzt(KhTaskWpqr task) {
        try {

            //缺少保存物品图片
            //缺少目前施工进度
            String result = "y.YHMS as ms,y.YHZRDW as DW,Y.YHZRDWLXR AS PEOPLE,Y.YHZRDWDH AS PHONE";
            String sql = "SELECT " + result + "  FROM KH_TASK K LEFT JOIN KH_YH_HISTORY Y ON y.id = k.YH_ID WHERE k.ID = ? ";
            return WebApiResponse.success(this.execSql(sql, task.getTaskId()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }


    public WebApiResponse appExchange(String taskId) {
        try {
            //保存现场照片
            return WebApiResponse.success("");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse getYbCount(String userId) {
        try {
            //保存现场照片 this.reposiotry.getybCount(userId)
            return WebApiResponse.success("");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse getDbCount(String userId) {
        try {
            String sql = "select a.count as db,b.count as yb from (select count(*) as count from KH_TASK WHERE (STATUS LIKE '未开始' OR  status like '进行中') AND USER_ID = ? ) a , (select count(*) as count from KH_TASK WHERE STATUS LIKE '已完成' AND USER_ID=?) b";
            List<Map<String, Object>> list = this.execSql(sql, userId, userId);
            //保存现场照片    this.reposiotry.getdbCount(userId)
            return WebApiResponse.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse appListjbr(String userId, long taskId) {
        try {
            String sql = "select k.yh_id as yhId,s.group_flag as flag from kh_task k left join kh_site s on s.id = k.site_id where k.id=?";
            Map<String, Object> map = this.execSqlSingleResult(sql, taskId);
            String yhId = map.get("YHID").toString();
            String jbrsql = "select u.id as \"value\",u.realname as \"text\",s.capatain as capatain,s.group_flag as flag from rztsysuser u left join kh_site s on s.user_id = u.id where s.yh_id=?";
            List<Map<String, Object>> list = this.execSql(jbrsql, yhId);
            List<Map<String, Object>> list1 = new ArrayList<>();
            for (Map map1 : list) {
                if (!map.get("FLAG").toString().equals(map1.get("FLAG").toString())) {
                    if (map1.get("CAPATAIN").toString().equals("1"))
                        list1.add(map1);
                }
            }
            return WebApiResponse.success(list1);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    //获取中心点坐标  现获取看护点的坐标  如果不存在，就用隐患的坐标
    public List<Map<String, Object>> getPoint(long taskId) {
        String sql = "select c.longitude as jd,c.latitude as wd from kh_cycle c left join kh_site s on s.yh_id = c.id left join kh_task k on k.site_id = s.id where k.id = ?";
        List<Map<String, Object>> list = this.execSql(sql, taskId);
        if (list.isEmpty()) {
            sql = "select y.jd as jd,y.wd as wd from kh_yh_history y left join kh_task k on y.id = k.yh_id where k.id=?";
            list = this.execSql(sql, taskId);
        }
        for (Map map : list) {
            map.put("ROUND", 100);
            map.put("URL", "http://192.168.1.122:7011//nurseTask/AppKhTask/appListjbr");
        }
       /* Point point = null;
        for (Map map : list) {
            point = new Point(Double.parseDouble(map.get("WD").toString()), Double.parseDouble(map.get("JD").toString()));
        }*/
        return list;
    }

    public WebApiResponse listPhone(long taskId) {
        try {
            String sql = "SELECT Y.YHZRDWLXR AS NAME,Y.YHZRDWDH AS PHONE FROM KH_YH_HISTORY Y LEFT JOIN KH_TASK K ON K.YH_ID = Y.ID WHERE  K.ID=? ";
            return WebApiResponse.success(this.execSql(sql,taskId));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }
}
