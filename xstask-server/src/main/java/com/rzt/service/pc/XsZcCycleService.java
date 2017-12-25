/**    
 * 文件名：XsZcCycleService           
 * 版本信息：    
 * 日期：2017/12/07 07:50:10    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service.pc;

import com.rzt.entity.app.XSZCTASK;
import com.rzt.entity.pc.XsZcCycle;
import com.rzt.entity.pc.XsZcCycleLineTower;
import com.rzt.entity.sch.XsTaskSCh;
import com.rzt.repository.pc.XsZcCycleRepository;
import com.rzt.service.CurdService;
import com.rzt.service.app.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**      
 * 类名称：XsZcCycleService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 07:50:10 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 07:50:10    
 * 修改备注：    
 **-0* @version
 */
@Service
public class XsZcCycleService extends CurdService<XsZcCycle,XsZcCycleRepository> {

    @Autowired
    private XSZCTASKService xszctaskService;
    @Autowired
    private XsZcCycleLineTowerService xsZcCycleLineTowerService;
    /***
    * @Method addCycle
    * @Description 新增周期
    * @param xsZcCycle
    * @return java.lang.Object
    * @date 2017/12/8 9:18
    * @author nwz
    */

//

    @Modifying
    @Transactional
    public Object addCycle(XsZcCycle xsZcCycle) {
        try {
            //添加周期
            xsZcCycle.setId();
            xsZcCycle.setCreateTime(new Date());
            this.add(xsZcCycle);
            //添加周期表关联的线路杆塔
            Long xsZcCycleId = xsZcCycle.getId();
            Long lineId = xsZcCycle.getLineId();
            Integer xsStartSort = xsZcCycle.getXsStartSort();
            Integer xsEndSort = xsZcCycle.getXsEndSort();
            String sql = "select * from cm_line_tower where line_id = ?1 and sort between ?2 and ?3";
            List<Map<String, Object>> lineTowerList = this.execSql(sql,lineId,xsStartSort,xsEndSort);
            for (Map<String,Object> lineTower: lineTowerList) {
                long lineTowerId = Long.parseLong(lineTower.get("ID").toString());
                XsZcCycleLineTower xsZcCycleLineTower = new XsZcCycleLineTower();
                xsZcCycleLineTower.setId();
                xsZcCycleLineTower.setCmLineTowerId(lineTowerId);
                xsZcCycleLineTower.setXsZcCycleId(xsZcCycleId);
                xsZcCycleLineTowerService.add(xsZcCycleLineTower);
            }
            return WebApiResponse.success("数据保存成功!");
        } catch (Exception var3) {
            return WebApiResponse.erro("出错了" + var3.getMessage());
        }
    }

    @Modifying
    @Transactional
    public Object addPlan(XSZCTASK xszctask) {
        try {
            //拿到周期相关的信息
            Long xsZcCycleId = xszctask.getXsZcCycleId();
            String sql = "select task_name,section,plan_xs_num,cycle,total_task_num from xs_zc_cycle where id = ?1";
            List<Map<String, Object>> xsZcCycles = this.execSql(sql, xsZcCycleId);
            if(xsZcCycles.isEmpty()) {
                return WebApiResponse.erro("没有这个周期");
            } else {
                Map<String, Object> xsZcCycle = xsZcCycles.get(0);
                String taskName = xsZcCycle.get("TASK_NAME").toString();
                Integer planXsNum = Integer.parseInt(xsZcCycle.get("PLAN_XS_NUM").toString());

                xszctask.setId();
                xszctask.setTaskName(taskName);
                xszctask.setPlanXsNum(planXsNum);
                xszctask.setTaskNumInCycle(0);
                xszctask.setRealXsNum(0);
                xszctask.setTaskNumInCycle(0);
                xszctask.setStauts(0);
                xszctask.setPdTime(DateUtil.dateNow());

                xszctaskService.add(xszctask);
                this.reposiotry.updateTotalTaskNum(xsZcCycleId);
            }


            return WebApiResponse.success("数据保存成功!");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据保存失败" + var3.getMessage());
        }
    }


    /***
    * @Method cycleList
    * @Description 巡视任务周期列表
    * @param [pageable, xsTaskSch]
    * @return java.lang.Object
    * @date 2017/12/14 10:34
    * @author nwz
    */
    public Object cycleList(Pageable pageable, XsTaskSCh xsTaskSch) throws Exception {
        StringBuffer sqlBuffer = new StringBuffer();
        ArrayList arrList = new ArrayList();
        sqlBuffer.append("SELECT id,plan_xs_num xspl,plan_start_time,plan_end_time,v_level \"vLevel\",task_name \"taskName\",section,cycle,tdyw_org \"tdywOrg\",in_use \"inUse\",total_task_num \"totalTaskNum\",create_time \"createTime\" FROM xs_zc_cycle where 1 = 1 and is_delete = 0");
        String startDate = xsTaskSch.getStartDate();
        String endDate = xsTaskSch.getEndDate();
        if(startDate != null && endDate != null) {
            sqlBuffer.append("and create_time between ? and ?");
            arrList.add(DateUtil.stringToDate(startDate));
            arrList.add(DateUtil.stringToDate(endDate));
        }
        Integer status = xsTaskSch.getStatus();
        if(status != null) {
            sqlBuffer.append("and in_use = ?");
            arrList.add(status);

        }
        Integer ispf = xsTaskSch.getIspf();
        if(ispf == 1) {
            sqlBuffer.append("and total_task_num = 0");
        }


        Page<Map<String, Object>> maps = this.execSqlPage(pageable,sqlBuffer.toString(), arrList.toArray());
        return maps;
    }

    /**
    * @Method logicalDelete
    * @Description 逻辑删除
    * @param [ids]
    * @return java.lang.Object
    * @date 2017/12/14 14:09
    * @author nwz
    */
    public void logicalDelete(Long[] ids) {
        List<Long> longs = Arrays.asList(ids);
        this.reposiotry.logicalDelete(longs);
    }
    /**
    * @Method listPlan
    * @Description 任务列表
    * @param [pageable, xsTaskSch]
    * @return java.lang.Object
    * @date 2017/12/14 14:17
    * @author nwz
    */
    public Object listPlan(Pageable pageable, XsTaskSCh xsTaskSch) {
        StringBuffer sqlBuffer = new StringBuffer();
        ArrayList arrList = new ArrayList();
        sqlBuffer.append("SELECT * FROM xs_zc_task where 1 = 1");
        String startDate = xsTaskSch.getStartDate();
        String endDate = xsTaskSch.getEndDate();
        if (startDate != null && endDate != null) {
            sqlBuffer.append("and plan_start_time between ? and ?");
            arrList.add(DateUtil.stringToDate(startDate));
            arrList.add(DateUtil.stringToDate(endDate));
        }
        Integer status = xsTaskSch.getStatus();
        if (status != null) {
            sqlBuffer.append("and status = ?");
            arrList.add(status);

        }
        String userId = xsTaskSch.getUserId();
        if (userId != null) {
            sqlBuffer.append("and cm_user_id = ?");
            arrList.add(userId);
        }
        Page<Map<String, Object>> maps = this.execSqlPage(pageable, sqlBuffer.toString(), arrList.toArray());
        return maps;
    }

    /***
    * @Method getCycle
    * @Description 查看周期
    * @param [id]
    * @return java.lang.Object
    * @date 2017/12/15 16:56
    * @author nwz
    */
    public Object getCycle(Long id) throws Exception{
        String sql = "select * from xs_zc_cycle where id = ?";
        Map<String, Object> cycle = this.execSqlSingleResult(sql, id);
        return cycle;
    }

    public void updateCycle(Long id, Integer cycle, Integer inUse, Integer planXsNum, String planStartTime, String planEndTime) {
        this.reposiotry.updateCycle(id,cycle,inUse,planXsNum,planStartTime,planEndTime);
    }
}