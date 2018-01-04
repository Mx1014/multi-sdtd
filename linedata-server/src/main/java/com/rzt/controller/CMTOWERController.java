/**    
 * 文件名：CMTOWERController
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.CMTOWER;
import com.rzt.service.CMTOWERService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名称：CMTOWERController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("CMTOWER")
public class CMTOWERController extends
		CurdController<CMTOWER,CMTOWERService> {
    
    public Map<String,Object> importTowers(MultipartFile file){
		HashMap<String, Object> result = new HashMap<>();
		try {
			service.importTowers(file);
			result.put("success",true);
		} catch (IOException e) {
			result.put("success",false);
			e.printStackTrace();
		}
		return result;
	}
	
}