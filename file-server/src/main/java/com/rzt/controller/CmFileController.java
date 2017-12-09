/**    
 * 文件名：CmFileController
 * 版本信息：    
 * 日期：2017/12/08 11:06:32    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.CmFile;
import com.rzt.service.CmFileService;
import com.rzt.utils.StorageUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
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

	@ApiOperation(value = "文件档案、头像等公共资源文件的上传",notes = "")
	@PostMapping("fileUpload")
	public Map<String,Object> fileUpload(MultipartFile file,Integer flag,Long fkId) {

		HashMap<String, Object> result = new HashMap<>();
		CmFile cmFile = new CmFile();
		try {
			Map<String, Object> map = StorageUtils.storageFiles(file);
			if("true".equals(map.get("success").toString())){

				String filePath = map.get("filePath").toString();
				String fileName = map.get("fileName").toString();
				cmFile.setId(null);
				cmFile.setCreateTime(new Date(System.currentTimeMillis()));
				cmFile.setFileName(fileName);
				cmFile.setFilePath(filePath);
				cmFile.setFileType(flag);
				cmFile.setFkId(fkId);


				service.add(cmFile);
				result.put("success",true);
				result.put("filePath",filePath);

			}

		} catch (IOException e) {
			LOGGER.error("文件上传失败",e);
			result.put("success",false);
		}
		return result;
	}
}