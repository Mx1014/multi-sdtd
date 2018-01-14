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

    public WebApiResponse findProblemAll(Integer page,Integer size,String startTime,String endTime,String proType,
                                         String tdORG,String lineName,String taskType,String wxORG,String level,String userId){
        String roletype = "";
        ArrayList<String> list = new ArrayList<>();
        Page<Map<String, Object>> maps = null;
       /* if(null == userId || "".equals(userId)){//如果没有当前用户id 那么直接返回没有权限
            return WebApiResponse.success("当前用户没有权限查看");
        }
        Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", userId);
        if(null != userInformation1 && !"".equals(userInformation1)){
            JSONObject jsonObject1 = JSONObject.parseObject(userInformation1.toString());
             roletype = (String) jsonObject1.get("ROLETYPE");//用户权限信息  0 为1级单位  1为二级单位 2为单位 只展示当前单位的任务
        }
        if(null == roletype || "".equals(roletype)){
            return WebApiResponse.success("当前用户没有权限查看");
        }*/
        if(null == page ){
            page = 0;
        }
        if(size==0){
            size = 10;
        }

        try {
            Pageable pageable = new PageRequest(page, size, null);
            String sql = "SELECT y.ID,y.YHMS,y.YHFXSJ,y.TDWX_ORG,y.TDYW_ORG,u.REALNAME,(l.V_LEVEL || l.LINE_NAME1 || l.SECTION) as line_name,y.YHLB,u.PHONE" +
                    "      FROM KH_YH_HISTORY y LEFT JOIN CM_LINE_SECTION l ON l.LINE_ID = y.LINE_ID" +
                    "       LEFT JOIN RZTSYSUSER u ON y.TBRID = u.ID" +
                    "        WHERE y.YHZT = 0";
            if((null != startTime && !"".equals(startTime)) && (null != endTime && !"".equals(endTime))){
                list.add(startTime);
                list.add(endTime);
                sql += "   AND y.CREATE_TIME BETWEEN to_date('?"+(list.size()-1)+"','yyyy-MM-dd hh24:mi:ss')" +
                        "   AND to_date('?"+list.size()+"','yyyy-MM-dd hh24:mi:ss') ";
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
                list.add(lineName);
                sql+="  AND l.LINE_NAME LIKE '?"+list.size()+"' ";
            }
            if(null != taskType && !"".equals(taskType)){

            }
            if(null != level && !"".equals(level)){
                list.add(level);
                sql+= "  AND l.V_LEVEL =  ?"+list.size();
            }

            maps = this.execSqlPage(pageable, sql, null);
            LOGGER.info("隐患查询成功");
        }catch (Exception e){
            LOGGER.error("查询失败"+e.getMessage());
            return WebApiResponse.erro("查询失败"+e.getMessage());
        }



        return WebApiResponse.success(maps);
    }





}
