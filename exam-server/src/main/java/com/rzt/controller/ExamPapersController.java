/**    
 * 文件名：ExamPapersController
 * 版本信息：    
 * 日期：2017/11/09 09:38:12    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.ExamPapers;
import com.rzt.service.ExamPapersService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 类名称：ExamPapersController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/09 09:38:12 
 * 修改人：张虎成    
 * 修改时间：2017/11/09 09:38:12    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("ExamPapers")
public class ExamPapersController extends
		CurdController<ExamPapers,ExamPapersService> {

	protected static Logger LOGGER = LoggerFactory.getLogger(ExamPapersController.class);

	@ApiOperation(
			value = "导入试卷",
			notes = "依据excel模板导入试卷，有底色的选项为正确选项"
	)
	@RequestMapping(value = "/importPaper",method = RequestMethod.POST)
	public String importPaper(MultipartFile file){
		String result = "<script>parent.err()</script>";
		try {
			this.service.importPaper(file);
			result = "<script>parent.succ()</script>";
		} catch (Exception e) {
			LOGGER.error("试卷导入出错！",e);
		}
		return result;
	}



	@ApiOperation(
			value = "获取试卷的内容（该试卷的试题与选项）",
			notes = "依据paperId获取该试卷的试题与选项"
	)
	@RequestMapping(value = "/getExamTexts",method = RequestMethod.POST)
	public List<Map<String,Object>> getExamTexts(String paperId){
		return service.getExamTexts(paperId);
	}
	
}