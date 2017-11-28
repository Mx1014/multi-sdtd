/**    
 * 文件名：ExamTextController
 * 版本信息：    
 * 日期：2017/11/09 09:38:12    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.ExamText;
import com.rzt.service.ExamTextService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类名称：ExamTextController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/09 09:38:12 
 * 修改人：张虎成    
 * 修改时间：2017/11/09 09:38:12    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("ExamText")
public class ExamTextController extends
		CurdController<ExamText,ExamTextService> {


	@RequestMapping("/getPaperTexts")
	public List<Object> getPaperTexts(){
		List<Object> list = this.service.getPaperTexts();
		return list;
	}
	/*select p.paper_name,p.exam_time,t.text_body,t.text_points,group_concat(o.option_name,',|分|割|'),GROUP_CONCAT(o.is_right)
	from exam_papers p,paper_text pt,exam_text t,exam_options o
	where p.id=pt.paper_id and t.id=pt.text_id and t.id=o.text_id group by t.id;*/
}