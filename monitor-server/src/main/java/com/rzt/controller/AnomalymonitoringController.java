/**    
 * 文件名：ANOMALYMONITORINGController
 * 版本信息：    
 * 日期：2017/12/31 16:25:17    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.Anomalymonitoring;
import com.rzt.service.AnomalymonitoringService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名称：ANOMALYMONITORINGController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/31 16:25:17 
 * 修改人：张虎成    
 * 修改时间：2017/12/31 16:25:17    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("ANOMALYMONITORING")
public class AnomalymonitoringController extends
		CurdController<Anomalymonitoring, AnomalymonitoringService> {
    
    
	
}