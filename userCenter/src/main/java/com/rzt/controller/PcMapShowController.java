package com.rzt.controller;

import com.alibaba.fastjson.JSON;
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
    public Object menInMap(String tdOrg, String workType, String userId, Date startDate, String currentUserId, Integer loginStatus, String lineId, String hasTask) {
        try {
            Date date = new Date();
            if (startDate == null) {
                startDate = date;
            }
            String needDateString = DateUtil.dateFormatToDay(startDate);
            long timeSecond = date.getTime();
            //准备要返回的list
            List<Map> menInMap = new ArrayList<>();
            HashOperations<String, String, Map> hashOperations = redisTemplate.opsForHash();
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            //0 根据人查 有人就直接结束
            String dept = pcMapShowService.dataAccessByUserId(currentUserId).toString();
            if (userId != null) {
                String[] split = userId.split(",");
                Set<String> keys = new HashSet<String>(Arrays.asList(split));

                menInMap = hashOperations.multiGet("menInMap", keys);
                //-->去除list中为null的元素
                menInMap.removeAll(Collections.singleton(null));
            } else if (lineId != null) {
                List<Map<String, Object>> menAboutLine = cmcoordinateService.getMenAboutLines(lineId, dept);
                Set<String> keys = new HashSet<String>();
                for (Map<String, Object> user : menAboutLine) {
                    String id = user.get("USERID").toString();
                    keys.add(id);
                }
                menInMap = hashOperations.multiGet("menInMap", keys);
                //-->去除list中为null的元素
                menInMap.removeAll(Collections.singleton(null));
            } else {
                String deptId = "";
                if (!StringUtils.isEmpty(tdOrg)) {
                    deptId = tdOrg;
                } else {
                    deptId = dept;
                }
                //1.初始数据权限
                if ("err".equals(deptId)) {

                } else if ("all".equals(deptId)) {
                    menInMap = hashOperations.values("menInMap");
                } else {
                    //1.1 根据部门筛选
                    List<Map<String, Object>> userList = pcMapShowService.deptMenFromRedis(deptId);
                    Set<String> keys = new HashSet();
                    //单位 外协 组织 班组 都走这里
                    for (Map<String, Object> user : userList) {
                        String id = user.get("ID").toString();
                        keys.add(id);
                    }
                    menInMap = hashOperations.multiGet("menInMap", keys);
                    //-->去除list中为null的元素
                    menInMap.removeAll(Collections.singleton(null));
                }
            }
            //2.根据工作类型来分
            if (!StringUtils.isEmpty(hasTask) && hasTask.equals("0")) {
                chouYiXia2(workType, loginStatus, needDateString, hasTask, menInMap, valueOperations);
            } else {
                chouYiXia(workType, loginStatus, needDateString, hasTask, menInMap, valueOperations);
            }
            return WebApiResponse.success(menInMap);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("查询失败" + e.getStackTrace());
        }
    }

    private void chouYiXia(String workTypes, Integer loginStatus, String needDateString, String hasTask, List<Map> menInMap, ValueOperations<String, Object> valueOperations) {
        JSONObject allMen = new JSONObject();
        if (workTypes == null || workTypes.contains("1")) {
            JSONObject khMenAll = JSONObject.parseObject(valueOperations.get("khMenAll:" + needDateString).toString());
            allMen.putAll(khMenAll);
        }
        if (workTypes == null || workTypes.contains("2")) {
            JSONObject xsMenAll = JSONObject.parseObject(valueOperations.get("xsMenAll:" + needDateString).toString());
            allMen.putAll(xsMenAll);
        }
        if (workTypes == null || workTypes.contains("3")) {
            JSONObject jcMenAll = JSONObject.parseObject(valueOperations.get("jcMenAll:" + needDateString).toString());
            allMen.putAll(jcMenAll);
        }
//        Object offLineJudge = valueOperations.get("offLineJudge");
        //默认90分钟
        /*int offLineJudgeNum = 5400000;
        //如果redis有配置 则用redis的数据
        if(offLineJudge != null) {
            offLineJudgeNum = Integer.parseInt(offLineJudge.toString()) * 60 * 1000;
        }*/
        ArrayList<String> userIds = new ArrayList<>();
        for (Map<String, Object> map : menInMap) {
            String userid = map.get("userid").toString();
            userIds.add(userid);
        }

        List<Map<String, Object>> userLoginStatusList = cmcoordinateService.execSql("select id,LOGINSTATUS from RZTSYSUSER where USERDELETE=1");
        HashMap<String, String> userLoginStatusMap = new HashMap<>();
        for (Map<String, Object> map : userLoginStatusList) {
            String id = map.get("ID").toString();
            String loginstatus = map.get("LOGINSTATUS").toString();
            userLoginStatusMap.put(id, loginstatus);
        }
        Iterator<Map> iterator = menInMap.iterator();
        while (iterator.hasNext()) {
            Map men = iterator.next();
            String userid = men.get("userid").toString();
            //离线 不在这儿判断了
            /*Long createtime = Long.parseLong(men.get("createtime").toString());
            if (timeSecond - createtime > offLineJudgeNum) {
                //大于九十分钟 离线
                //显示在线
                if (loginStatus == 1) {
                    iterator.remove();
                    continue;
                }
                men.put("loginStatus", 0);
            } else {
                //小于十分钟 在线
                //显示离线
                if (loginStatus == 0) {
                    iterator.remove();
                    continue;
                }
                men.put("loginStatus", 1);
            }*/
            String loginStatus1 = userLoginStatusMap.get(userid);
            if (loginStatus1 != null) {
                if (loginStatus1.equals("0")) {
                    //大于九十分钟 离线
                    //显示在线
                    if (loginStatus == 1) {
                        iterator.remove();
                        continue;
                    }
                    men.put("loginStatus", 0);
                } else if (loginStatus1.equals("1")) {
                    //小于十分钟 在线
                    //显示离线
                    if (loginStatus == 0) {
                        iterator.remove();
                        continue;
                    }
                    men.put("loginStatus", 1);
                }
            } else {
                //注意这个地方
                iterator.remove();
                continue;
            }
            if (!allMen.containsKey(userid)) {
                //注意这个地方
                iterator.remove();
            } else {
                men.put("statuts", allMen.get(userid));
            }
        }
    }

    //地图上显示没有任务的人
    private void chouYiXia2(String workTypes, Integer loginStatus, String needDateString, String hasTask, List<Map> menInMap, ValueOperations<String, Object> valueOperations) {
        JSONObject allMen = new JSONObject();
        List<Map> menInMap2 = new ArrayList<>();
        if (workTypes == null || workTypes.contains("1")) {
            JSONObject khMenAll = JSONObject.parseObject(valueOperations.get("khMenAll:" + needDateString).toString());
            allMen.putAll(khMenAll);
            for (Map map:menInMap){
                if (map.get("gzlx").toString().equals("1")) {
                    menInMap2.add(map);
                }
            }
        }
        if (workTypes == null || workTypes.contains("2")) {
            JSONObject xsMenAll = JSONObject.parseObject(valueOperations.get("xsMenAll:" + needDateString).toString());
            allMen.putAll(xsMenAll);
            for (Map map:menInMap){
                if (map.get("gzlx").toString().equals("2")) {
                    menInMap2.add(map);
                }
            }
        }
        if (workTypes == null || workTypes.contains("3")) {
            JSONObject jcMenAll = JSONObject.parseObject(valueOperations.get("jcMenAll:" + needDateString).toString());
            allMen.putAll(jcMenAll);
            for (Map map:menInMap){
                if (map.get("gzlx").toString().equals("3")) {
                    menInMap2.add(map);
                }
            }
        }
        ArrayList<String> userIds = new ArrayList<>();
        for (Map<String, Object> map : menInMap) {
            String userid = map.get("userid").toString();
            userIds.add(userid);
        }

        List<Map<String, Object>> userLoginStatusList = cmcoordinateService.execSql("select id,LOGINSTATUS from RZTSYSUSER where USERDELETE=1");
        HashMap<String, String> userLoginStatusMap = new HashMap<>();
        for (Map<String, Object> map : userLoginStatusList) {
            String id = map.get("ID").toString();
            String loginstatus = map.get("LOGINSTATUS").toString();
            userLoginStatusMap.put(id, loginstatus);
        }
        Iterator<Map> iterator = menInMap2.iterator();
        while (iterator.hasNext()) {
            Map men = iterator.next();
            String userid = men.get("userid").toString();
            String loginStatus1 = userLoginStatusMap.get(userid);
            if (loginStatus1 != null) {
                if (loginStatus1.equals("0")) {
                    //大于九十分钟 离线
                    //显示在线
                    if (loginStatus == 1) {
                        iterator.remove();
                        continue;
                    }
                    men.put("loginStatus", 0);
                } else if (loginStatus1.equals("1")) {
                    //小于十分钟 在线
                    //显示离线
                    if (loginStatus == 0) {
                        iterator.remove();
                        continue;
                    }
                    men.put("loginStatus", 1);
                }
            } else {
                //注意这个地方
                iterator.remove();
                continue;
            }
            if (allMen.containsKey(userid)) {
                iterator.remove();
            }else {
                men.put("statuts", -1);
            }
        }
        menInMap.clear();
        menInMap.addAll(menInMap2);
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
    public Object menAboutLine(Long lineId, String currentUserId, Date startDate) {
        try {
            Map<String, Object> res = new HashMap<String, Object>();
            //拿到线路上的所有的杆塔
            List<Map<String, Object>> coordinateList = cmcoordinateService.lineCoordinateList(lineId);
            Date date = new Date();
            if (startDate == null) {
                startDate = date;
            }
            String needDateString = DateUtil.dateFormatToDay(startDate);
//            long timeSecond = date.getTime();
            String timeSecond = "";
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            //准备要返回的list
            List<Map> menInMap = new ArrayList<>();
            HashOperations<String, String, Map> hashOperations = redisTemplate.opsForHash();
            String deptId = pcMapShowService.dataAccessByUserId(currentUserId).toString();
            //显示改线路当天关联的人
            List<Map<String, Object>> menAboutLine = cmcoordinateService.getMenAboutLine(deptId, lineId);
            Set<String> keys = new HashSet();
            //单位 外协 组织 班组 都走这里
            for (Map<String, Object> user : menAboutLine) {
                String id = user.get("USERID").toString();
                keys.add(id);
            }
            menInMap = hashOperations.multiGet("menInMap", keys);
            //-->去除list中为null的元素
            menInMap.removeAll(Collections.singleton(null));
            chouYiXia(null, 2, needDateString, timeSecond, menInMap, valueOperations);
            res.put("menInMap", menInMap);
            res.put("coordinateList", coordinateList);
            return WebApiResponse.success(res);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("失败");
        }
    }


    @GetMapping("menAbooutMultiLine")
    public Object et4r1fdvmenAbooutMultiLine(String lineIds, String currentUserId, Date startDate, String yhTypes, String gzTypes, String deptId, String workType, String userId, String loginType) {
        if (StringUtils.isEmpty(lineIds)) {
            return WebApiResponse.erro("你都不传线路id,你想上天啊?");
        } else {
            String[] lineIdArr = lineIds.split(",");
            try {
                List resList = new ArrayList();
                /*for (String lineId:lineIdArr) {
                    Object o = menAboutLinesSon(Long.parseLong(lineId), currentUserId, startDate, yhTypes, gzTypes);
                    resList.add(o);
                }*/
                Object o = menAboutLinesAllSon(lineIdArr, currentUserId, startDate, yhTypes, gzTypes, deptId, workType, userId, loginType,null);
                resList.add(o);
                return WebApiResponse.success(resList);
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("报错了" + e.getMessage());
            }
        }
    }

    //地图上根据线路查询隐患、故障、人员、杆塔
    private Object menAboutLinesAllSon(String[] lineIdArr, String currentUserId, Date startDate, String yhTypes, String gzTypes, String deptId, String workType, String userId, String loginType,String yhlb) throws Exception {
        String s = "";
        String s1 = "";
        if (!StringUtils.isEmpty(deptId)) {
            String[] split = deptId.split(",");
            for (int i = 0; i < split.length; i++) {
                if (i > 0) {
                    s += " or YWORG_ID='" + split[i] + "' ";
                    s1 += " or td_org='" + split[i] + "' ";
                } else {
                    s += " and ( YWORG_ID='" + split[i] + "' ";
                    s1 += " and ( td_org='" + split[i] + "' ";
                }
            }
            s += ") ";
            s1 += ") ";
        }
        Map<String, Object> res = new HashMap<String, Object>();
        //拿到线路上的所有的杆塔
        List<Map<String, Object>> coordinateList = cmcoordinateService.lineCoordinateList(lineIdArr);
        Date date = new Date();
        if (startDate == null) {
            startDate = date;
        }
        String needDateString = DateUtil.dateFormatToDay(startDate);
        long timeSecond = date.getTime();
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        //准备要返回的list
        List<Map> menInMap = new ArrayList<>();
        HashOperations<String, String, Map> hashOperations = redisTemplate.opsForHash();
//        String deptId = pcMapShowService.dataAccessByUserId(currentUserId).toString();
        //显示改线路当天 关联的人
        List<Map<String, Object>> menAboutLine = cmcoordinateService.getMenAboutLineMain(lineIdArr, s1, loginType, workType, userId);
        Set<String> keys = new HashSet();
        //单位 外协 组织 班组 都走这里
        for (Map<String, Object> user : menAboutLine) {
            String id = user.get("USERID").toString();
            keys.add(id);
        }
        menInMap = hashOperations.multiGet("menInMap", keys);
        //-->去除list中为null的元素
        menInMap.removeAll(Collections.singleton(null));
        chouYiXia(null, 2, needDateString, null, menInMap, valueOperations);
        res.put("menInMap", menInMap);
        res.put("coordinateList", coordinateList);
        List<Map<String, Object>> guzhang = getGuzhang(gzTypes, lineIdArr, null);
        res.put("guzhang", guzhang);
        Object yinhuan = getYinhuan2(yhTypes, currentUserId, yhlb, lineIdArr, s);
        res.put("yinhuan", yinhuan);
        return res;
    }

    private Object getYinhuan2(String yhjb, String currentUserId, String yhlb, String[] lineIdArr, String deptId) throws Exception {
        List params = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where yhzt=0 ");
        if (yhjb != null && !yhjb.equals("")) {
            String[] split = yhjb.split(",");
            buffer.append(" and (");
            for (int i = 0; i < split.length; i++) {
                String s = "'" + split[i] + "'";
                buffer.append("yhjb1 = "+s+" or ");
                buffer.append("yhlb = "+s);
                if (i<split.length-1){
                    buffer.append(" or ");
                }
            }
            buffer.append(") ");
        }
        /*if (yhlb != null && !yhlb.equals("")) {
            buffer.append(" and yhlb like ?");
            params.add("%" + yhlb + "%");
        }*/
        int length = lineIdArr.length;
        buffer.append(" and ( ");
        for (int i = 0; i < length; i++) {
            String lineId = lineIdArr[i];
            if (lineId != null) {
                String[] split = deptId.split(",");
                if (i > 0) {
                    buffer.append(" or line_Id='" + lineId + "' ");
                } else {
                    buffer.append(" line_Id='" + lineId + "' ");
                }
            }
        }
        buffer.append(" ) ");
        buffer.append(deptId);
//            buffer.append(" and yhzt = 0 ");
        String sql = "SELECT DISTINCT(y.id) as yhid, y.* FROM ( SELECT  y.id as yh_id, y.* FROM KH_YH_HISTORY y WHERE YHLB LIKE '在施类' AND YHZT = 0 UNION ALL SELECT DISTINCT  (s.YH_ID), y.* FROM KH_YH_HISTORY y, KH_SITE s  WHERE s.YH_ID = y.ID AND s.STATUS = 1 AND y.yhzt = 0 and y.sfdj=1) y " + buffer.toString();
//        String sql = "select * from KH_YH_HISTORY y " + buffer.toString();
        List<Map<String, Object>> list = cmcoordinateService.execSql(sql, params.toArray());
        List<Object> list1 = new ArrayList<>();
        for (Map map : list) {
            if (map != null && map.size() > 0 && map.get("JD") != null) {
                sql = "select u.realname from kh_site s left join rztsysuser u on u.id =s.user_id where yh_id=? and s.status=1";
                List<Map<String, Object>> nameList = cmcoordinateService.execSql(sql, Long.parseLong(map.get("ID").toString()));
                String realname = "";
                map.put("USERNAME", "无");
                if (nameList.size() > 0) {
                    for (int i = 0; i < nameList.size(); i++) {
                        if (!realname.contains((nameList.get(i).get("REALNAME")).toString())) {
                            realname += nameList.get(i).get("REALNAME") + " ";
                        }
                    }
                    map.put("USERNAME", realname);
                }
                list1.add(map);
            }
        }
        return list1;
    }

    private List<Map<String, Object>> getGuzhang(String gzTypes, String[] lineIdArr, String types) {
        String s = "";
        if (StringUtils.isEmpty(types)) {
            s += " and GZ_REASON1 in ('施工碰线','异物短路','树竹放电') ";
        } else {
            String[] typeArr = types.split(",");
            s += " and GZ_REASON1 in (";
            for (String type : typeArr) {
                s += "'" + type + "',";
            }
            s = s.substring(0, s.length() - 1);
            s += ")";
        }
        String sql = "select t.*,tt.TOWER_NAME,ttt.LONGITUDE,ttt.LATITUDE from GUZHANG t join CM_LINE_TOWER tt on t.LINE_ID = tt.LINE_ID and t.GZ_TOWER is not NULL and tt.TOWER_NAME = substr(t.GZ_TOWER,0,instr(GZ_TOWER,'#',1,1)-1) " +
                s +
                "join cm_tower ttt on ttt.id = tt.TOWER_ID";
        List<Object> params = new ArrayList<Object>();
        StringBuffer sqlBuffer = new StringBuffer("");
        int length = lineIdArr.length;
        for (int i = 0; i < length; i++) {
            String hehe = lineIdArr[i];
            sqlBuffer.append(" (t.line_id = ?) ");
            if (i < length - 1) {
                sqlBuffer.append(" or ");
            }
            params.add(hehe);
//            params.add(split[1]);
        }
        sql += " and ( " + sqlBuffer.toString() + " )";
        return cmcoordinateService.execSql(sql, params.toArray());
    }


    public Object menAboutLinesSon(Long lineId, String currentUserId, Date startDate, String yhjb, String gzTypes) throws Exception {
        Map<String, Object> res = new HashMap<String, Object>();
        //拿到线路上的所有的杆塔
        List<Map<String, Object>> coordinateList = cmcoordinateService.lineCoordinateList(lineId);
        Date date = new Date();
        if (startDate == null) {
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
        List<Map<String, Object>> menAboutLine = cmcoordinateService.getMenAboutLine(deptId, lineId);
        Set<String> keys = new HashSet();
        //单位 外协 组织 班组 都走这里
        for (Map<String, Object> user : menAboutLine) {
            String id = user.get("USERID").toString();
            keys.add(id);
        }
        menInMap = hashOperations.multiGet("menInMap", keys);
        //-->去除list中为null的元素
        menInMap.removeAll(Collections.singleton(null));
        chouYiXia(null, 2, needDateString, null, menInMap, valueOperations);
        res.put("menInMap", menInMap);
        res.put("coordinateList", coordinateList);
        List<Map<String, Object>> guzhang = getGuzhang(gzTypes, currentUserId, lineId);
        res.put("guzhang", guzhang);
        Object yinhuan = getYinhuan(yhjb, currentUserId, null, lineId);
        res.put("yinhuan", yinhuan);
        return res;
    }


    /***
     * @Method getYinhuan
     * @Description
     * @return java.lang.Object
     * @date 2018/2/7 14:56
     * @author nwz
     */
    public Object getYinhuan(String yhjb, String currentUserId, String yhlb, Long lineId) throws Exception {
        Map<String, Object> jsonObject = pcMapShowService.userInfoFromRedis(currentUserId);
        List params = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        Integer roleType = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object tdId = jsonObject.get("DEPTID");
        Object companyid = jsonObject.get("COMPANYID");
        buffer.append(" where yhzt=0 ");
        if (roleType == 1 || roleType == 2) {
            buffer.append(" and y.YWORG_ID = '" + tdId + "'");
        }
        if (roleType == 3) {
            buffer.append(" and y.WXORG_ID ='" + companyid + "'");
        }
        if (yhjb != null && !yhjb.equals("")) {
            String[] split = yhjb.split(",");
            String result = "";
            for (int i = 0; i < split.length; i++) {
                String s = "'" + split[i] + "'";
                result += s + ",";
            }
            buffer.append(" and yhjb1 in (" + result.substring(0, result.lastIndexOf(",")) + ")");
        }
        if (yhlb != null && !yhlb.equals("")) {
            buffer.append(" and yhlb like ?");
            params.add("%" + yhlb + "%");
        }
        if (lineId != null) {
            buffer.append(" and y.line_id = ?");
            params.add(lineId);
        }
//            buffer.append(" and yhzt = 0 ");
        //String sql = "SELECT DISTINCT(y.id) as yhid, y.* FROM ( SELECT  y.id as yh_id, y.* FROM KH_YH_HISTORY y WHERE YHLB LIKE '在施类' AND YHZT = 0 UNION ALL SELECT DISTINCT  (s.YH_ID), y.* FROM KH_YH_HISTORY y, KH_SITE s  WHERE s.YH_ID = y.ID AND s.STATUS = 1 AND y.yhzt = 0) y " + buffer.toString();
        String sql = "select * from KH_YH_HISTORY y " + buffer.toString();
        List<Map<String, Object>> list = cmcoordinateService.execSql(sql, params.toArray());
        List<Object> list1 = new ArrayList<>();
        for (Map map : list) {
            if (map != null && map.size() > 0 && map.get("JD") != null) {
                sql = "select u.realname from kh_site s left join rztsysuser u on u.id =s.user_id where yh_id=?";
                List<Map<String, Object>> nameList = cmcoordinateService.execSql(sql, Long.parseLong(map.get("ID").toString()));
                String realname = "";
                map.put("USERNAME", "无");
                if (nameList.size() > 0) {
                    for (int i = 0; i < nameList.size(); i++) {
                        if (!realname.contains((nameList.get(i).get("REALNAME")).toString())) {
                            realname += nameList.get(i).get("REALNAME") + " ";
                        }
                    }
                    map.put("USERNAME", realname);
                }
                list1.add(map);
            }
        }
        return list1;
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
    public Object menPath(String userId, Date startTime, Date endTime) {
        try {
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            String prefixDate = DateUtil.dateFormatToDay(startTime);
            Set<Object> set;
            if (endTime == null) {
                endTime = DateUtil.dateNow();
            }
            set = zSetOperations.rangeByScore(prefixDate + ":" + userId, startTime.getTime(), endTime.getTime());
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
    public Object menPoint(String userId, Date startTime, Date endTime, Integer lOrR) {
        try {
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            String prefixDate = DateUtil.dateFormatToDay(startTime);
            Set<Object> set;
            if (endTime == null) {
                endTime = DateUtil.dateNow();
            }
            if (lOrR == 0) {
                set = zSetOperations.reverseRangeByScore(prefixDate + ":" + userId, startTime.getTime(), endTime.getTime(), 0, 1);
            } else {
                set = zSetOperations.rangeByScore(prefixDate + ":" + userId, startTime.getTime(), endTime.getTime(), 0, 1);
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
            List<Map<String, Object>> coordinateList = cmcoordinateService.lineCoordinateList(lineId);
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
            Map<String, Object> coordinate = cmcoordinateService.towerCoordinate(towerId);
            return WebApiResponse.success(coordinate);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    @GetMapping("menNameLike")
    public Object menNameLike(String userName, Integer workType) {
        try {
            userName = "%" + userName + "%";
            StringBuffer menNameLikesql = new StringBuffer("select id,classname,realname from rztsysuser where USERDELETE = 1 and realname like ?");
            ArrayList list = new ArrayList();
            list.add(userName);
            if (workType != null) {
                menNameLikesql.append(" and worktype = ?");
                list.add(workType);
            }
            List<Map<String, Object>> maps = cmcoordinateService.execSql(menNameLikesql.toString(), list.toArray());
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    /***
     * @Method menInLine
     * @Description 线路上的人
     * @return java.lang.Object
     * @date 2018/1/14 17:37
     * @author nwz
     */
    @GetMapping("menInLine")
    public Object menInLine(Long lineId, String currentUserId) {
        try {
            Map<String, Object> map = pcMapShowService.userInfoFromRedis(currentUserId);
            String tempTable = "";
            ArrayList list = new ArrayList();
            Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
            if (roletype == 0) {
                tempTable = "SELECT DISTINCT userid from (select CM_USER_ID userid from xs_zc_cycle where LINE_ID = ?\n" +
                        "union all\n" +
                        "select USER_ID userid from KH_SITE where LINE_ID = ?) t where t.userid is not null ";
                list.add(lineId);
                list.add(lineId);
            } else {
                tempTable = "SELECT DISTINCT userid from (select CM_USER_ID userid from xs_zc_cycle where LINE_ID = ? and TD_ORG = ?\n" +
                        "union all\n" +
                        "select USER_ID userid from KH_SITE where LINE_ID = ? and TDYW_ORGID = ?) t where t.userid is not null ";
                ;
                list.add(lineId);
                list.add(map.get("DEPTID"));
                list.add(lineId);
                list.add(map.get("DEPTID"));

            }
            StringBuffer menInLineSql = new StringBuffer("select t.USERID,tt.REALNAME,tt.LOGINSTATUS from (" + tempTable + ") t join RZTSYSUSER tt on t.userid = tt.ID");
            List<Map<String, Object>> userMaps = cmcoordinateService.execSql(menInLineSql.toString(), list.toArray());
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
            if (day == null) {
                day = new Date();
            }
            String sql = "SELECT cm_user_id,min(stauts) status from XS_ZC_TASK where PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) group by CM_USER_ID";
            List<Map<String, Object>> userList = cmcoordinateService.execSql(sql, day);
            Map<String, Object> map = new HashMap<String, Object>();
            for (Map<String, Object> user : userList) {
                String id = user.get("CM_USER_ID").toString();
                map.put(id, user.get("STATUS"));
            }
            valueOperations.set("xsMenAll:" + DateUtil.dateFormatToDay(day), map);
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
            for (Map<String, Object> map : maps) {
                String id = map.get("ID").toString();
                hashOperations.put("UserInformation", id, map);
            }
            return WebApiResponse.success("成功了");
        } catch (Exception e) {
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    @GetMapping("getGuZhang")
    public Object getGuZhang(String currentUserId, String types) {
        try {

            List<Map<String, Object>> maps = getGuzhang(types, currentUserId, null);
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro(e.getMessage());
        }

    }

    public List<Map<String, Object>> getGuzhang(String types, String currentUserId, Long lineId) throws Exception {
        Map<String, Object> map = pcMapShowService.userInfoFromRedis(currentUserId);
        String s = "";
        Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
        if (roletype != 0) {
            s = "and t.td_org = '" + map.get("DEPT") + "'";
        }
        if (StringUtils.isEmpty(types)) {
            s += " and GZ_REASON1 in ('施工碰线','异物短路','树竹放电') ";
        } else {
            String[] typeArr = types.split(",");
            s += " and GZ_REASON1 in (";
            for (String type : typeArr) {
                s += "'" + type + "',";
            }
            s = s.substring(0, s.length() - 1);
            s += ")";
        }
        String sql = "select t.*,tt.TOWER_NAME,ttt.LONGITUDE,ttt.LATITUDE from GUZHANG t join CM_LINE_TOWER tt on t.LINE_ID = tt.LINE_ID and t.GZ_TOWER is not NULL and tt.TOWER_NAME = substr(t.GZ_TOWER,0,instr(GZ_TOWER,'#',1,1)-1) " +
                s +
                "join cm_tower ttt on ttt.id = tt.TOWER_ID";
        if (lineId != null) {
            sql += " and t.line_id = " + lineId;
        }
        return cmcoordinateService.execSql(sql);
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
            for (Map<String, Object> dept : deptList) {
                String id = dept.get("ID").toString();
                List<Map<String, Object>> userList = cmcoordinateService.execSql(userListSql, id);
                hashOperations.put("menInDept", id, userList);
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
            if (day == null) {
                day = new Date();
            }
            String sql = "SELECT USER_ID,max(status) status from KH_TASK where PLAN_END_TIME >= trunc(?1) and user_id is not null AND STATUS!=3 and PLAN_START_TIME <= trunc(?1+1) group by USER_ID";
            List<Map<String, Object>> userList = cmcoordinateService.execSql(sql, day);
            Map<String, Object> map = new HashMap<String, Object>();
            for (Map<String, Object> user : userList) {
                String id = user.get("USER_ID").toString();
                map.put(id, user.get("STATUS"));
            }
            valueOperations.set("khMenAll:" + DateUtil.dateFormatToDay(day), map);
            return WebApiResponse.success("成功了");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    /***
     * @Method menCurrentDayJC
     * @Description 刷新flushMenInDept缓存
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("menCurrentDayJC")
    public Object menCurrentDayJC(Date day) {
        try {
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            if (day == null) {
                day = new Date();
            }
            String sql = "select USER_ID,min(STATUS) status from CHECK_LIVE_TASK where  PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME < trunc(?1+1) and user_id is not NULL group by USER_ID";
            List<Map<String, Object>> userList = cmcoordinateService.execSql(sql, day);
            Map<String, Object> map = new HashMap<String, Object>();
            for (Map<String, Object> user : userList) {
                String id = user.get("USER_ID").toString();
                map.put(id, user.get("STATUS"));
            }
            valueOperations.set("jcMenAll:" + DateUtil.dateFormatToDay(day), map);
            return WebApiResponse.success("成功了");
        } catch (Exception e) {
            e.printStackTrace();
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
                Map<String, Object> map = cmcoordinateService.execSqlSingleResult(sql, userId);
                Object status = map.get("STATUS");
                khMenAll.put(userId, status);
                valueOperations.set(key, khMenAll);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("flushRztSysdata")
    public Object flushRztSysdata() {
        String sql = " SELECT * FROM RZTSYSDATA ";
        List<Map<String, Object>> maps = cmcoordinateService.execSql(sql);
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

        for (Map map : maps) {
            Object roleid = map.get("ROLEID");
            if (roleid == null) {
                break;
            }
            hashOperations.put("RZTSYSDATA", roleid, map);
        }
        return WebApiResponse.success("yes");
    }

    //两个定时计划
    @Scheduled(cron = "0 25 0 ? * *")
    public void Scheduled() {
        Date day = new Date();
        menCurrentDayKh(day);
        menCurrentDayxs(day);
        menCurrentDayJC(day);
        flushMenInDept();
    }

    //两个定时计划
    @Scheduled(fixedRate = 1800 * 1000)
    public void Scheduled2() {
        Date day = new Date();
        menCurrentDayKh(day);
        menCurrentDayxs(day);
        menCurrentDayJC(day);
        flushMenInDept();
    }
}
