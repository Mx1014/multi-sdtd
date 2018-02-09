/**    
 * 文件名：PICTUREJCController
 * 版本信息：    
 * 日期：2017/12/19 15:31:04    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.PICTUREJC;
import com.rzt.service.PICTUREJCService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 类名称：PICTUREJCController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/19 15:31:04 
 * 修改人：张虎成    
 * 修改时间：2017/12/19 15:31:04    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("PICTUREJC")
@Api(description = "稽查图片")
public class PICTUREJCController extends
		CurdController<PICTUREJC,PICTUREJCService> {

	protected static Logger LOGGER = LoggerFactory.getLogger(PICTUREJCController.class);

	@ApiOperation(
			value = "单个照片文件上传",
			notes = "上传照片信息及参数。taskId  userId processId processName lat lon 这些是必须的，" +
					"文件类型（默认1图片）2录音3摄像,fileType 为空是传照片，不为空则传文件"
	)
	@PostMapping("fileUpload")
	public Map<String, Object> fileUpload(MultipartFile multipartFile, PICTUREJC picturejc) {
		Map<String, Object> result;
		if(picturejc.getFileType()==null){
			result = service.fileUpload(multipartFile, picturejc);
		}else{
			result = service.fileUploadByType( multipartFile,  picturejc);
		}
		return result;
	}

	@ApiOperation(
			value = "根据Id删除照片",
			notes = "根据Id删除照片，同时删除文件及数据"
	)
	@DeleteMapping("deleteImgsById")
	public Map<String, Object> deleteImgsById(String id) {
		return service.deleteImgsById(Long.valueOf(id));
	}

	@ApiOperation(
			value = "获取任务照片",
			notes = "根据taskId获取某条任务的所有照片"
	)
	@GetMapping("getImgsBytaskId")
	public Map<String, Object> getImgsBytaskId(String taskId) {
		return service.getImgsBytaskId(Long.valueOf(taskId));
	}

	@ApiOperation(
			value = "获取某一步骤的照片",
			notes = "根据taskId,processId获取某一步骤的照片"
	)
	@GetMapping("getImgsBytaskIdAndProcessId")
	public Map<String, Object> getImgsBytaskIdAndProcessId(String taskId,String processId,String processType) {

		return service.getImgsBytaskIdAndProcessId( taskId, processId,processType);

	}
	@ApiOperation(
			value = "根据id获取照片",
			notes = "根据id获取照片"
	)
	@GetMapping("getImgById")
	public Map<String, Object> getImgById(String id) {
		return service.findById(Long.valueOf(id));
	}

}