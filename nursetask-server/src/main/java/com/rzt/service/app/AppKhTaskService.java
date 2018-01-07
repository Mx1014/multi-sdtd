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

import java.util.*;

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
            buffer.append("where (status = 0 or status = 1)  and user_id = ? order by k.plan_start_time");
        } else if (dbyb == 2) {
            buffer.append(" where (status = 2 or status = 3)  and user_id = ? order by k.real_end_time desc");
        }
        String sql = "select " + result + " from kh_task k left join rztsysuser u on u.id = k.user_id " + buffer.toString();
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

    public WebApiResponse appListWp(String userId, String taskId) {
        try {
            String sql = "select wp_zt from kh_task_wpqr where taskId=?";
            List<Map<String, Object>> map = this.execSql(sql, taskId);
            if (map.isEmpty()) {
                Map map1 = new HashMap<>();
                map1.put("WP_ZT", "0,0,0,0,0");
                map.add(map1);
            }
            return WebApiResponse.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse appListZl(String taskId) {
        try {
            String sql = "select cl_zt from kh_task_wpqr where taskId=?";
            Map<String, Object> map = this.execSqlSingleResult(sql, taskId);
            if (map.get("CL_ZT") == null) {
                map.put("CL_ZT", "0,0,0,0,0,0");
            }
            return WebApiResponse.success(map);
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


    public WebApiResponse appListCaptain(String taskId, String userId) {
        try {
            String sql = "select s.CAPATAIN,s.group_flag as flag FROM KH_SITE s,KH_TASK k where s.ID = k.SITE_ID AND  k.id=? and k.USER_ID =?";
            return WebApiResponse.success(this.execSql(sql, taskId, userId));
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
            String sql = "select a.count as db,b.count as yb from (select count(*) as count from KH_TASK WHERE (STATUS = 0 OR  status = 1) AND USER_ID = ? ) a , (select count(*) as count from KH_TASK WHERE (STATUS =2 or status=3) AND USER_ID=?) b";
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
            String sql = "SELECT k.yh_id as YHID,S.GROUP_FLAG AS FLAG from KH_TASK k,kh_site s where k.SITE_ID  = s.id and k.id=?";
            Map<String, Object> map = this.execSqlSingleResult(sql, taskId);
            String yhId = map.get("YHID").toString();
            String jbrsql = "select u.id as \"value\",u.realname as \"text\",s.capatain as capatain,s.group_flag as flag from rztsysuser u ,kh_site s where s.user_id = u.id and s.yh_id=? and status=1";
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
        String sql = "select c.radius as ROUND,c.longitude as jd,c.latitude as wd from kh_cycle c left join kh_site s on s.yh_id = c.yh_id left join kh_task k on k.site_id = s.id where k.id = ?";
        List<Map<String, Object>> list = this.execSql(sql, taskId);
        if (!list.isEmpty()) {
            for (Map map : list) {
                if (map.get("WD") == null || map.get("JD") == null) {
                    sql = "select y.radius as ROUND,y.jd as jd,y.wd as wd from kh_yh_history y left join kh_task k on y.id = k.yh_id where k.id=?";
                    list = this.execSql(sql, taskId);
                }
            }
        } else {
            sql = "select y.radius as ROUND,y.jd as jd,y.wd as wd from kh_yh_history y left join kh_task k on y.id = k.yh_id where k.id=?";
            list = this.execSql(sql, taskId);
        }
        for (Map map : list) {
            map.put("URL", "http://168.130.1.31:8097:/warningServer/warning/KHOffPost");
        }
       /* Point point = null;
        for (Map map : list) {
            point = new Point(Double.parseDouble(map.get("WD").toString()), Double.parseDouble(map.get("JD").toString()));
        }*/
        return list;
    }

    public WebApiResponse listPhone(long taskId) {
        try {
            String sql = "SELECT Y.YHZRDWLXR AS NAME,Y.YHZRDWDH AS PHONE FROM KH_YH_HISTORY Y ,KH_TASK K where K.YH_ID = Y.ID and  K.ID=? ";
            return WebApiResponse.success(this.execSql(sql, taskId));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }



    public WebApiResponse appCaptainTime(String userId, long taskId, String flag) {
        try {
            flag = flag.substring(0, flag.length() - 1) + 1;//0 + flag.substring(flag.length() - 1, flag.length())
            String sql = "SELECT k.REAL_END_TIME \n" +
                    "FROM KH_SITE s,KH_TASK k WHERE s.ID=k.SITE_ID and s.GROUP_FLAG=?";
            Map<String, Object> map = this.execSqlSingleResult(sql, flag);
            if (map.get("REAL_END_TIME") == null) {
                throw new Exception();
            }
            return WebApiResponse.success(map);
        } catch (Exception e) {
            return WebApiResponse.erro("队长未交班");
        }
    }

    public WebApiResponse appListTaskDone(String userId, long taskId) {
        try {
            String sql = "SELECT k.status status,k.TASK_NAME taskname,y.YHMS ms,y.YHJB jb,k.PLAN_START_TIME starttime,k.PLAN_END_TIME endtime,u.REALNAME name,u.PHONE phone,d.DEPTNAME\n" +
                    "from KH_TASK k,KH_YH_HISTORY y,RZTSYSUSER u,RZTSYSDEPARTMENT d\n" +
                    "where k.YH_ID=y.id and k.USER_ID = u.id and d.ID = u.CLASSNAME\n" +
                    "and k.id = ?";
            return WebApiResponse.success(this.execSql(sql, taskId));
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse appListPicture(int step, long taskId) {
        try {
            String sql = "SELECT FILE_PATH as path,PROCESS_NAME as step\n" +
                    "FROM PICTURE_KH WHERE PROCESS_ID=? and TASK_ID =? ";
            return WebApiResponse.success(this.execSql(sql, step, taskId));
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取失败");
        }
    }


    public WebApiResponse appCompareEndTime(long taskId) {
        try {
            String sql = "select plan_end_time as time from kh_task where id=?";
            Map<String, Object> map = this.execSqlSingleResult(sql, taskId);
            int time = compareDate(DateUtil.parseDate(map.get("TIME").toString()), new Date());
            if (time != 1) {
                return WebApiResponse.success("可以交班");
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return WebApiResponse.erro("不可以交班");
        }
    }

    public int compareDate(Date dt1, Date dt2) {
        if (dt1.getTime() > dt2.getTime()) {
            return 1;
        } else if (dt1.getTime() < dt2.getTime()) {
            return -1;
        } else {//相等
            return 0;
        }
    }
}
