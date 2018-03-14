/**    
 * 文件名：KhYhTowerController
 * 版本信息：    
 * 日期：2018/03/14 02:22:31    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.KhYhTower;
import com.rzt.service.KhYhTowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rzt.controller.CurdController;
/**      
 * 类名称：KhYhTowerController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2018/03/14 02:22:31 
 * 修改人：张虎成    
 * 修改时间：2018/03/14 02:22:31    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("KhYhTower")
public class KhYhTowerController extends
		CurdController<KhYhTower,KhYhTowerService> {
    
    
	
}