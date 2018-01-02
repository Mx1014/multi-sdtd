/**    
 * 文件名：CmFileController
 * 版本信息：    
 * 日期：2017/12/08 11:06:32    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.CmFile;
import com.rzt.service.CmFileService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 类名称：CmFileController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/08 11:06:32 
 * 修改人：张虎成    
 * 修改时间：2017/12/08 11:06:32    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("CmFile")
public class CmFileController extends
		CurdController<CmFile,CmFileService> {
	protected static Logger LOGGER = LoggerFactory.getLogger(CmFileController.class);

	@ApiOperation(value = "文件档案、头像等公共资源文件的上传",notes = "文件档案、头像等公共资源文件的上传")
	@PostMapping("fileUpload")
	public Map<String,Object> fileUpload(MultipartFile file,CmFile cmFile) {

		return service.fileUpload( file, cmFile);

	}

	@ApiOperation(value = "根据fkid获取照片",notes = "根据fkid获取照片")
	@GetMapping("getImgByFkId")
	public Map<String,Object> getImgByFkId(String fkid){
		return service.getImgByFkId(Long.valueOf(fkid));
	}

	@ApiOperation(value = "根据id获取照片",notes = "根据id获取照片")
	@GetMapping("getImgById")
	public Map<String,Object> getImgById(String id){
		return service.getImgById(Long.valueOf(id));
	}

}