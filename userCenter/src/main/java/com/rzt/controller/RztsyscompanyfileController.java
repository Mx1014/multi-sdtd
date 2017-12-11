/**    
 * 文件名：RztsyscompanyfileController
 * 版本信息：    
 * 日期：2017/12/08 16:40:23    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.Rztsyscompanyfile;
import com.rzt.service.RztsyscompanyfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rzt.controller.CurdController;
/**      
 * 类名称：RztsyscompanyfileController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/08 16:40:23 
 * 修改人：张虎成    
 * 修改时间：2017/12/08 16:40:23    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("Rztsyscompanyfile")
public class RztsyscompanyfileController extends
		CurdController<Rztsyscompanyfile,RztsyscompanyfileService> {
    
    
	
}