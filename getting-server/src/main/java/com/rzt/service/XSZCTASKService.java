package com.rzt.service;

import com.rzt.entity.XSZCTASK;
import com.rzt.repository.XSZCTASKRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 李成阳
 * 2018/1/2
 */
@Service
public class XSZCTASKService extends CurdService<XSZCTASK,XSZCTASKRepository>{
    protected static Logger LOGGER = LoggerFactory.getLogger(XSZCTASKService.class);
    @Autowired
    private XSZCTASKRepository repository;


    public WebApiResponse findXSTASK(String id){

        String sql = "SELECT xst.ID,xst.REAL_START_TIME createTime,xst.TASK_NAME taskName,users.REALNAME realName,users.PHONE phone," +
                "  dept.DEPTNAME dept,xst.PLAN_START_TIME startTime,xst.PLAN_END_TIME endTime" +
                "    from XS_ZC_TASK xst" +
                "      LEFT JOIN RZTSYSUSER users" +
                "          on users.ID = xst.CM_USER_ID" +
                "            LEFT JOIN RZTSYSDEPARTMENT dept" +
                "              on dept.ID = xst.TD_ORG";
        ArrayList<Object> objects = new ArrayList<>();
        List<Map<String, Object>> maps = null;
        try {
            if(null!=id && !"".equals(id)){
                objects.add(id);
                sql+="id=?"+objects.size();
            }
             maps = this.execSql(sql, objects.toArray());
        }catch (Exception e){
            LOGGER.error("查询巡查信息失败，"+e.getStackTrace());
            System.out.println(e.getStackTrace());
            return WebApiResponse.erro("查询巡查信息失败，"+e.getStackTrace());
        }
        LOGGER.info("查询巡查信息");
        return WebApiResponse.success(maps);
    }





}
