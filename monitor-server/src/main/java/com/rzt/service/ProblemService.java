package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.TimedTask;
import com.rzt.repository.XSZCTASKRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

/**
 * 李成阳
 * 2018/1/14
 *  问题处理类
 */
@Service
public class ProblemService  extends CurdService<TimedTask,XSZCTASKRepository>{
    protected static Logger LOGGER = LoggerFactory.getLogger(ProblemService.class);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 问题审核一级页面列表展示
     * @param page 分页组件
     * @param size 分页组件
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param proType    隐患类型
     * @param tdORG      通道公司
     * @param lineName   线路名称
     * @param taskType   作业类型  巡视 看护 稽查
     * @param level      线路电压
     * @param userId     角色id
     *
     */
    public WebApiResponse findProblemAll(Integer page,Integer size,String startTime,String endTime,String proType,
                                           String tdORG,String lineName,String taskType,String wxORG,String level,String userId){
        String roletype = "";
        String deptid = "";
                ArrayList<String> list = new ArrayList<>();
        Page<Map<String, Object>> maps = null;
        String sql = "";
        //测试写死最大权限
        userId = "84cf516c077a4267842f5d20939d7887";
        try {
        if(null == userId || "".equals(userId)){//如果没有当前用户id 那么直接返回没有权限
            return WebApiResponse.success("当前用户没有权限查看");
        }
        Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", userId);
        if(null != userInformation1 && !"".equals(userInformation1)){
            JSONObject jsonObject1 = JSONObject.parseObject(userInformation1.toString());
             roletype = (String) jsonObject1.get("ROLETYPE");//用户权限信息  0 为1级单位  1为二级单位 2为单位 只展示当前单位的任务
             deptid = (String) jsonObject1.get("DEPTID");//当角色权限为2 | 3时需要只显示本单位的任务信息
        }

        if(null == roletype || "".equals(roletype)){
            return WebApiResponse.success("当前用户没有权限查看");
        }
        if(null == deptid || "".equals(deptid)){
                return WebApiResponse.success("获取当前用户单位信息失败");
        }

        if(null == page ){
            page = 0;
        }
        if(size==0){
            size = 10;
        }
            Pageable pageable = new PageRequest(page, size, null);


        switch (Integer.parseInt(roletype)){
            case 0 :{//顶级单位  北京局权限 查看所有隐患
                 sql =  "     SELECT y.ID,y.YHMS,y.YHFXSJ,y.TDWX_ORG,y.TDYW_ORG,u.REALNAME,y.YHLB,u.PHONE,y.TASK_ID as KHID,y.XSTASK_ID as XSID,y.SGQK,l.LINE_NAME,l.V_LEVEL,(l.V_LEVEL || l.LINE_NAME1 || l.SECTION) as LINENAME" +
                        "      FROM XS_SB_YH y LEFT JOIN CM_LINE_SECTION l ON l.LINE_ID = y.LINE_ID" +
                        "       LEFT JOIN RZTSYSUSER u ON y.TBRID = u.ID" +
                        "        WHERE y.YHZT = 0";
                break;
            }
            case 1 :{//地级单位 只能显示当前单位的隐患
                list.add(deptid);
                sql = "SELECT y.ID,y.YHMS,y.YHFXSJ,y.TDWX_ORG,y.TDYW_ORG,u.REALNAME,(l.V_LEVEL || l.LINE_NAME1 || l.SECTION) as LINENAME,y.YHLB,u.PHONE,y.TASK_ID as KHID,y.XSTASK_ID as XSID,y.SGQK,l.LINE_NAME,l.V_LEVEL" +
                        "      FROM XS_SB_YH y LEFT JOIN CM_LINE_SECTION l ON l.LINE_ID = y.LINE_ID" +
                        "       LEFT JOIN RZTSYSUSER u ON y.TBRID = u.ID" +
                        "        WHERE y.YHZT = 0 AND  y.YWORG_ID =  ?"+list.size();
                break;
            }
            case 2 :{//地级单位 只能显示当前单位的隐患
                sql = "SELECT y.ID,y.YHMS,y.YHFXSJ,y.TDWX_ORG,y.TDYW_ORG,u.REALNAME,(l.V_LEVEL || l.LINE_NAME1 || l.SECTION) as LINENAME,y.YHLB,u.PHONE,y.TASK_ID as KHID,y.XSTASK_ID as XSID,y.SGQK,l.LINE_NAME,l.V_LEVEL" +
                        "      FROM XS_SB_YH y LEFT JOIN CM_LINE_SECTION l ON l.LINE_ID = y.LINE_ID" +
                        "       LEFT JOIN RZTSYSUSER u ON y.TBRID = u.ID" +
                        "        WHERE y.YHZT = 0  AND  y.YWORG_ID =  ?"+list.size();
                break;
            }
        }

            if((null != startTime && !"".equals(startTime)) && (null != endTime && !"".equals(endTime))){
                list.add(startTime);
                list.add(endTime);
                sql += "   AND y.CREATE_TIME BETWEEN to_date(?"+(list.size()-1)+",'yyyy-MM-dd hh24:mi:ss')" +
                        "   AND to_date(?"+list.size()+",'yyyy-MM-dd hh24:mi:ss') ";
            }
            if(null != proType && !"".equals(proType)){

            }
            if(null != tdORG && !"".equals(tdORG)){
                list.add(proType);
                sql += "  AND y.YWORG_ID = ?"+list.size();
            }
            if(null != wxORG && !"".equals(wxORG)){
                list.add(wxORG);
                sql += "  AND y.WXORG_ID =  ?"+list.size();
            }

            if(null != lineName && !"".equals(lineName)){
                String name = "'%"+lineName+"%'";
                sql+="  AND l.LINE_NAME LIKE "+name;
            }
            if(null != taskType && !"".equals(taskType)){

            }
            if(null != level && !"".equals(level)){
                list.add(level);
                sql+= "  AND l.V_LEVEL =  ?"+list.size();
            }
        if(null != list && list.size()>0){
            maps = this.execSqlPage(pageable, sql, list.toArray());
        }else {
            maps = this.execSqlPage(pageable, sql, null);
        }

            LOGGER.info("隐患查询成功");
        }catch (Exception e){
            LOGGER.error("查询失败"+e.getMessage());
            return WebApiResponse.erro("查询失败"+e.getMessage());
        }



        return WebApiResponse.success(maps);
    }

    /**
     * 根据用户id查询当前用户的角色类型  前端准备按照权限显示筛选条件
     * @param userId
     * @return
     */
    public WebApiResponse findRoleType(String userId){
       try {
           Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", userId);
           if(null != userInformation1 && !"".equals(userInformation1)){
               JSONObject jsonObject1 = JSONObject.parseObject(userInformation1.toString());
               String roletype = (String) jsonObject1.get("ROLETYPE");//用户权限信息  0 为1级单位  1为二级单位 2为单位 只展示当前单位的任务
               return WebApiResponse.success(roletype);
           }
       }catch (Exception e){
            LOGGER.error("查询权限信息失败"+e.getMessage());
           return WebApiResponse.erro("查询权限信息失败"+e.getMessage());
       }
        return WebApiResponse.success("查询权限信息失败");
    }



}
