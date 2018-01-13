package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.service.CmcoordinateService;
import com.rzt.service.PcMapShowService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import com.rzt.utils.RedisUtil;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Object menInMap(String tdOrg,Integer workType,String userId,Date startDate,String currentUserId) {
        try {
            //准备要返回的list
            List<Object> menInMap = null;
            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            //0 根据人查 有人就直接结束
            if(userId != null) {
                return hashOperations.get("menInMap",userId);
            }
            String deptId = "";
            /*if(tdOrg != null) {
                deptId = tdOrg;
            } else {
                deptId = pcMapShowService.dataAccessByUserId(currentUserId).toString();
            }*/
            deptId = pcMapShowService.dataAccessByUserId(currentUserId).toString();
            //1.初始数据权限
            if("err".equals(deptId)) {

            } else if("all".equals(deptId)) {
                menInMap = hashOperations.values("menInMap");
                Object xsMenAll = valueOperations.get("xsMenAll");
                JSONObject allMen = JSONObject.parseObject(xsMenAll.toString());
                Iterator<Object> iterator = menInMap.iterator();
                while(iterator.hasNext()){
                    Object menObj = iterator.next();
                    JSONObject men = JSONObject.parseObject(menObj.toString());
                    String userid = men.get("userid").toString();
                    if(!allMen.containsKey(userid)) {
                        iterator.remove();   //注意这个地方
                    }
                }
            } else {
                //1.1 根据部门筛选
                List<Map<String, Object>> userList = pcMapShowService.deptMenFromRedis(deptId);
                Set<String> keys = new HashSet();
                for (Map<String,Object> user: userList) {
                    String id = user.get("ID").toString();
                    keys.add(id);
                }
                menInMap = hashOperations.multiGet("menInMap",keys);
//                menInMap.replaceAll(Collections.singleton(null));
            }

            return WebApiResponse.success(menInMap);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("查询失败" + e.getStackTrace());
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
            if(endTime == null) {
                endTime = DateUtil.dateNow();
            }
            Set<Object> set = zSetOperations.rangeByScore( prefixDate + "_" + userId, startTime.getTime(), endTime.getTime());
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

    @InitBinder
    public void initBinder(ServletRequestDataBinder binder) {
        /*** 自动转换日期类型的字段格式
         */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));

    }

    //下面是开发中用到的一些方法

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
     * @Method menCurrentDay
     * @Description 刷新flushMenInDept缓存
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("menCurrentDay")
    public Object menCurrentDay() {
        try {
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            String sql = "SELECT cm_user_id FROM XS_ZC_TASK WHERE (PLAN_END_TIME BETWEEN trunc(sysdate) AND trunc(sysdate + 1)) union select cm_user_id from XS_ZC_TASK WHERE ((PLAN_START_TIME BETWEEN trunc(sysdate) AND trunc(sysdate + 1)))";
            List<Map<String, Object>> userList = cmcoordinateService.execSql(sql);
            Map<String,Object> map = new HashMap<String, Object>();
            for (Map<String,Object> user: userList) {
                String id = user.get("CM_USER_ID").toString();
                map.put(id,id);
            }
            valueOperations.set("xsMenAll",map);
            return WebApiResponse.success("成功了");
        } catch (Exception e) {
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }



}
