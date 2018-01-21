package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.service.CmcoordinateService;
import com.rzt.service.PcMapShowService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import com.rzt.utils.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/***
* @Class PcMapShowController
* @Description
* @param
* @return
* @date 2017/12/25 13:57
* @author nwz
*/
@RestController
@RequestMapping("pcMapShow")
public class PcMapShowController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CmcoordinateService cmcoordinateService;
    @Autowired
    private PcMapShowService pcMapShowService;

    /***
     * @Method menInMap
     * @Description 地图上的人
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("menInMap")
    public Object menInMap(String tdOrg,Integer workType,String userId,Date startDate,String currentUserId,Integer loginStatus /*,@RequestParam(value = "userIds[]") String[] userIds*/) {
        try {
            Date date = new Date();
            if(startDate == null) {
                startDate = date;
            }
            String needDateString = DateUtil.dateFormatToDay(startDate);
            long timeSecond = date.getTime();
            //准备要返回的list
            List<Map> menInMap = new ArrayList<>();
            HashOperations<String, String, Map> hashOperations = redisTemplate.opsForHash();
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            //0 根据人查 有人就直接结束
            if(userId != null) {
                menInMap.add(hashOperations.get("menInMap",userId));
            } else {
                String deptId = "";
                if(!StringUtils.isEmpty(tdOrg)) {
                    deptId = tdOrg;
                } else {
                    deptId = pcMapShowService.dataAccessByUserId(currentUserId).toString();
                }
                //1.初始数据权限
                if("err".equals(deptId)) {

                } else if("all".equals(deptId)) {
                    menInMap = hashOperations.values("menInMap");
                } else {
                    //1.1 根据部门筛选
                    List<Map<String, Object>> userList = pcMapShowService.deptMenFromRedis(deptId);
                    Set<String> keys = new HashSet();
                    //单位 外协 组织 班组 都走这里
                    for (Map<String,Object> user: userList) {
                        String id = user.get("ID").toString();
                        keys.add(id);
                    }
                    menInMap = hashOperations.multiGet("menInMap",keys);
                    //-->去除list中为null的元素
                    menInMap.removeAll(Collections.singleton(null));
                }
            }
            //2.根据工作类型来分
            chouYiXia(workType, loginStatus, needDateString, timeSecond, menInMap, valueOperations);


            return WebApiResponse.success(menInMap);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("查询失败" + e.getStackTrace());
        }
    }

    private void chouYiXia(Integer workType, Integer loginStatus, String needDateString, long timeSecond, List<Map> menInMap, ValueOperations<String, Object> valueOperations) {
        JSONObject allMen = new JSONObject();
        if(workType == null || workType == 1)  {
            JSONObject khMenAll = JSONObject.parseObject(valueOperations.get("khMenAll:" + needDateString).toString());
            allMen.putAll(khMenAll);
        }
        if(workType == null || workType == 2)  {
            JSONObject xsMenAll = JSONObject.parseObject(valueOperations.get("xsMenAll:" + needDateString).toString());
            allMen.putAll(xsMenAll);
        }
        if(workType == null || workType == 3)  {

        }
        Iterator<Map> iterator = menInMap.iterator();
        while(iterator.hasNext()){
            Map men = iterator.next();
            String userid = men.get("userid").toString();

            Long createtime = Long.parseLong(men.get("createtime").toString());

            if(timeSecond - createtime > 600000) {
                //大于十分钟 离线
                //显示在线
                if(loginStatus == 1) {
                    iterator.remove();
                    continue;
                }
                men.put("loginStatus",0);
            } else {
                //小于十分钟 在线
                //显示离线
                if (loginStatus == 0) {
                    iterator.remove();
                    continue;
                }
                men.put("loginStatus",1);
            }

            if(!allMen.containsKey(userid)) {
                //注意这个地方
                iterator.remove();
            } else {
                men.put("statuts",allMen.get(userid));
            }
        }
    }


    /***
     * @Method menAboutLine
     * @Description 地图上的人
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("menAboutLine")
    public Object menAboutLine(Long lineId,String currentUserId,Date startDate) {
        try {
            Map<String,Object> res = new HashMap<String,Object>();
            //拿到线路上的所有的杆塔
            List<Map<String,Object>> coordinateList =  cmcoordinateService.lineCoordinateList(lineId);
            Date date = new Date();
            if(startDate == null) {
                startDate = date;
            }
            String needDateString = DateUtil.dateFormatToDay(startDate);
            long timeSecond = date.getTime();
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            //准备要返回的list
            List<Map> menInMap = new ArrayList<>();
            HashOperations<String, String, Map> hashOperations = redisTemplate.opsForHash();
            String deptId = pcMapShowService.dataAccessByUserId(currentUserId).toString();
            //显示改线路当天关联的人
            List<Map<String, Object>> menAboutLine = cmcoordinateService.getMenAboutLine(deptId, lineId, currentUserId, startDate);
            Set<String> keys = new HashSet();
            //单位 外协 组织 班组 都走这里
              for (Map<String,Object> user: menAboutLine) {
                String id = user.get("USERID").toString();
                keys.add(id);
            }
            menInMap = hashOperations.multiGet("menInMap",keys);
            //-->去除list中为null的元素
            menInMap.removeAll(Collections.singleton(null));
            chouYiXia(null, 2, needDateString, timeSecond, menInMap, valueOperations);
            res.put("menInMap",menInMap);
            res.put("coordinateList",coordinateList);
            return WebApiResponse.success(res);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("失败");
        }
    }


    /***
     * @Method menInfo
     * @Description 地图上的人
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("menInfo")
    public Object menInfo(String userId) {
        try {
            Map<String, Object> userInformation = pcMapShowService.userInfoFromRedis(userId);
            return WebApiResponse.success(userInformation);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("失败");
        }
    }


    /***
     * @Method menPath
     * @Description 地图上人的轨迹
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("menPath")
    public Object menPath(String userId,Date startTime,Date endTime) {
        try {
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            String prefixDate = DateUtil.dateFormatToDay(startTime);
            Set<Object> set;
            if(endTime == null) {
                endTime = DateUtil.dateNow();
            }
            set = zSetOperations.rangeByScore( prefixDate + ":" + userId, startTime.getTime(), endTime.getTime());
            return WebApiResponse.success(set);
        } catch (Exception e) {
            return WebApiResponse.erro("失败" + e.getMessage());
        }
    }

    /***
     * @Method t
     * @Description 地图上人的位置
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("menPoint")
    public Object menPoint(String userId,Date startTime,Date endTime,Integer lOrR) {
        try {
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            String prefixDate = DateUtil.dateFormatToDay(startTime);
            Set<Object> set;
            if(endTime == null) {
                endTime = DateUtil.dateNow();
            }
            if(lOrR == 0) {
                set = zSetOperations.reverseRangeByScore( prefixDate + ":" + userId, startTime.getTime(), endTime.getTime(),0,1);
            } else {
                set = zSetOperations.rangeByScore( prefixDate + ":" + userId, startTime.getTime(), endTime.getTime(),0,1);
            }
            return WebApiResponse.success(set);
        } catch (Exception e) {
            return WebApiResponse.erro("失败" + e.getMessage());
        }
    }


    /***
     * @Method lineCoordinateList
     * @Description 线路的位置
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("lineCoordinateList")
    public Object lineCoordinateList(Long lineId) {
        try {
            List<Map<String,Object>> coordinateList =  cmcoordinateService.lineCoordinateList(lineId);
            return WebApiResponse.success(coordinateList);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    /***
     * @Method towerCoordinate
     * @Description 杆塔的位置
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("towerCoordinate")
    public Object towerCoordinate(Long towerId) {
        try {
            Map<String,Object> coordinate =  cmcoordinateService.towerCoordinate(towerId);
            return WebApiResponse.success(coordinate);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    @GetMapping("menNameLike")
    public Object menNameLike(String userName,Integer workType) {
        try {
            userName = "%" + userName + "%";
            StringBuffer menNameLikesql = new StringBuffer("select id,classname,realname from rztsysuser where USERDELETE = 1 and realname like ?");
            ArrayList list = new ArrayList();
            list.add(userName);
            if(workType != null) {
                menNameLikesql.append(" and worktype = ?");
                list.add(workType);
            }
            List<Map<String, Object>> maps = cmcoordinateService.execSql(menNameLikesql.toString(),list.toArray());
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }
    /***
    * @Method menInLine
    * @Description 线路上的人
    * @param [lineId, currentUserId]
    * @return java.lang.Object
    * @date 2018/1/14 17:37
    * @author nwz
    */
    @GetMapping("menInLine")
    public Object menInLine(Long lineId,String currentUserId) {
        try {
            Map<String, Object> map = pcMapShowService.userInfoFromRedis(currentUserId);
            String tempTable = "";
            ArrayList list = new ArrayList();
            Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
            if(roletype == 0) {
                tempTable = "SELECT DISTINCT userid from (select CM_USER_ID userid from xs_zc_cycle where LINE_ID = ?\n" +
                        "union all\n" +
                        "select USER_ID userid from KH_SITE where LINE_ID = ?) t where t.userid is not null ";
                list.add(lineId);
                list.add(lineId);
            } else {
                tempTable = "SELECT DISTINCT userid from (select CM_USER_ID userid from xs_zc_cycle where LINE_ID = ? and TD_ORG = ?\n" +
                        "union all\n" +
                        "select USER_ID userid from KH_SITE where LINE_ID = ? and TDYW_ORGID = ?) t where t.userid is not null ";;
                list.add(lineId);
                list.add(map.get("DEPTID"));
                list.add(lineId);
                list.add(map.get("DEPTID"));

            }
            StringBuffer menInLineSql = new StringBuffer("select t.userid,tt.REALNAME from (" + tempTable + ") t join RZTSYSUSER tt on t.userid = tt.ID");
            List<Map<String, Object>> userMaps = cmcoordinateService.execSql(menInLineSql.toString(),list.toArray());
            return WebApiResponse.success(userMaps);
        } catch (Exception e) {
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }


    @InitBinder
    public void initBinder(ServletRequestDataBinder binder) {
        /*** 自动转换日期类型的字段格式
         */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));

    }

    //下面是开发中用到的一些方法

    /***
     * @Method menCurrentDayxs
     * @Description 刷新巡视任务的人
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("menCurrentDayxs")
    public Object menCurrentDayxs(Date day) {
        try {
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            if(day == null) {
                day = new Date();
            }
            String sql = "SELECT cm_user_id,min(stauts) status from XS_ZC_TASK where PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) group by CM_USER_ID";
            List<Map<String, Object>> userList = cmcoordinateService.execSql(sql,day);
            Map<String,Object> map = new HashMap<String, Object>();
            for (Map<String,Object> user: userList) {
                String id = user.get("CM_USER_ID").toString();
                map.put(id,user.get("STATUS"));
            }
            valueOperations.set("xsMenAll:" + DateUtil.dateFormatToDay(day),map);
            return WebApiResponse.success("成功了");
        } catch (Exception e) {
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    /***
     * @Method flushUserInformationRedis
     * @Description 刷新flushUserInformationRedis缓存
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("flushUserInformationRedis")
    public Object flushUserInfoRedis() {
        try {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            String sql = "SELECT * from USERINFO";
            List<Map<String, Object>> maps = cmcoordinateService.execSql(sql);
            for (Map<String,Object> map: maps) {
                String id = map.get("ID").toString();
                hashOperations.put("UserInformation",id,map);
            }
            return WebApiResponse.success("成功了");
        } catch (Exception e) {
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }


    /***
     * @Method flushMenInDept
     * @Description 刷新flushMenInDept缓存
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("flushMenInDept")
    public Object flushMenInDept() {
        try {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            String deptListSql = "SELECT id from RZTSYSDEPARTMENT";
            String userListSql = "select id,worktype from RZTSYSUSER where USERDELETE = 1 and (DEPTID = ?1 or COMPANYID = ?1 or GROUPID = ?1 or CLASSNAME = ?1 ) ";
            List<Map<String, Object>> deptList = cmcoordinateService.execSql(deptListSql);
            for (Map<String,Object> dept: deptList) {
                String id = dept.get("ID").toString();
                List<Map<String, Object>> userList = cmcoordinateService.execSql(userListSql, id);
                hashOperations.put("menInDept",id,userList);
            }
            return WebApiResponse.success("成功了");
        } catch (Exception e) {
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }



    /***
     * @Method menCurrentDayKh
     * @Description 刷新flushMenInDept缓存
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("menCurrentDayKh")
    public Object menCurrentDayKh(Date day) {
        try {
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            if(day == null) {
                day = new Date();
            }
            String sql = "SELECT USER_ID,min(status) status from KH_TASK where PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) group by USER_ID";
            List<Map<String, Object>> userList = cmcoordinateService.execSql(sql,day);
            Map<String,Object> map = new HashMap<String, Object>();
            for (Map<String,Object> user: userList) {
                String id = user.get("USER_ID").toString();
                map.put(id,user.get("STATUS"));
            }
            valueOperations.set("khMenAll:" + DateUtil.dateFormatToDay(day),map);
            return WebApiResponse.success("成功了");
        } catch (Exception e) {
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    /***
     * @Method menCurrentDayKh
     * @Description 刷新flushMenInDept缓存
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("updateKhInfoStatusInredis")
    public void updateKhInfoStatusInredis(String userId) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String key = "khMenAll:" + DateUtil.dateFormatToDay(new Date());
        Object khMenAllString = valueOperations.get(key);
        if (khMenAllString == null) {
        } else {
            JSONObject khMenAll = JSONObject.parseObject(khMenAllString.toString());
            String sql = "SELECT USER_ID,min(status) status from kh_task where PLAN_END_TIME >= trunc(sysdate) and  PLAN_START_TIME <= trunc(sysdate+1) and USER_ID = ? group by user_id";
            try {
                Map<String, Object> map = cmcoordinateService.execSqlSingleResult(sql,userId);
                Object status = map.get("STATUS");
                khMenAll.put(userId,status);
                valueOperations.set(key,khMenAll);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //两个定时计划
    @Scheduled(cron = "0 25 0 ? * *")
    public void Scheduled() {
        Date day = new Date();
        menCurrentDayKh(day);
        menCurrentDayxs(day);
    }

}
