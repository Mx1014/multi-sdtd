/**    
 * 文件名：KhCycleController
 * 版本信息：    
 * 日期：2017/11/28 14:43:44    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.KhCycle;
import com.rzt.service.KhCycleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rzt.controller.CurdController;
/**      
 * 类名称：KhCycleController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 14:43:44 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 14:43:44    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("KhCycle")
public class KhCycleController extends
		CurdController<KhCycle,KhCycleService> {
    
    
	
}