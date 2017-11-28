/**    
 * 文件名：ExamScoreController
 * 版本信息：    
 * 日期：2017/11/09 09:38:12    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.controller.CurdController;
import com.rzt.entity.ExamScore;
import com.rzt.service.ExamScoreService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名称：ExamScoreController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/09 09:38:12 
 * 修改人：张虎成    
 * 修改时间：2017/11/09 09:38:12    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("ExamScore")
public class ExamScoreController extends
		CurdController<ExamScore,ExamScoreService> {
    
    
	
}