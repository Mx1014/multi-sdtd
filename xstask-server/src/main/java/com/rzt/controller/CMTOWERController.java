/**    
 * 文件名：CMTOWERController
 * 版本信息：    
 * 日期：2017/11/28 18:05:13    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.CMTOWER;
import com.rzt.service.CMTOWERService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名称：CMTOWERController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 18:05:13 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 18:05:13    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("CMTOWER")
public class CMTOWERController extends
		CurdController<CMTOWER,CMTOWERService> {
    
    
	
}