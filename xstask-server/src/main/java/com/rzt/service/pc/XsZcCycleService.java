/**    
 * 文件名：XsZcCycleService           
 * 版本信息：    
 * 日期：2017/12/07 07:50:10    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service.pc;

import com.rzt.entity.pc.XsZcCycle;
import com.rzt.entity.pc.XsZcCycleLineTower;
import com.rzt.repository.pc.XsZcCycleRepository;
import com.rzt.service.CurdService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：XsZcCycleService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 07:50:10 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 07:50:10    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class XsZcCycleService extends CurdService<XsZcCycle,XsZcCycleRepository> {


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



}