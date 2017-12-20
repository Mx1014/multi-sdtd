/**    
 * 文件名：PICTUREYHController
 * 版本信息：    
 * 日期：2017/12/19 15:31:04    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.PICTUREYH;
import com.rzt.service.PICTUREYHService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**      
 * 类名称：PICTUREYHController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/19 15:31:04 
 * 修改人：张虎成    
 * 修改时间：2017/12/19 15:31:04    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("PICTUREYH")
public class PICTUREYHController extends
		CurdController<PICTUREYH,PICTUREYHService> {


	protected static Logger LOGGER = LoggerFactory.getLogger(PICTUREYHController.class);

	@ApiOperation(
			value = "单个文件上传",
			notes = "上传文件信息及参数。taskId  userId processId processName lat lon，这些是必须的"
	)
	@PostMapping("fileUpload")
	public Map<String, Object> fileUpload(MultipartFile multipartFile, PICTUREYH pictureyh) {
		return service.fileUpload( multipartFile,  pictureyh);
	}

	@ApiOperation(
			value = "根据Id删除照片",
			notes = "根据Id删除照片，同时删除文件及数据"
	)
	@DeleteMapping("deleteImgsById")
	public Map<String, Object> deleteImgsById(Long id) {

		return service.deleteImgsById( id);

	}

	@ApiOperation(
			value = "获取任务照片",
			notes = "根据taskId获取某条任务的所有照片"
	)
	@GetMapping("getImgsBytaskId")
	public Map<String, Object> getImgsBytaskId(Long taskId) {
		return service.getImgsBytaskId( taskId);
	}

	@ApiOperation(
			value = "获取某一步骤的照片",
			notes = "根据taskId,processId获取某一步骤的照片"
	)
	@GetMapping("getImgsBytaskIdAndProcessId")
	public Map<String, Object> getImgsBytaskIdAndProcessId(String taskId,String processId) {

		return service.getImgsBytaskIdAndProcessId( taskId, processId);

	}

	@ApiOperation(
			value = "根据id获取照片",
			notes = "根据id获取照片"
	)
	@GetMapping("getImgById")
	public Map<String, Object> getImgById(Long id) {

		return service.getImgById(id);

	}

}