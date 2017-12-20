/**    
 * 文件名：CMPOSITIONController
 * 版本信息：    
 * 日期：2017/12/17 17:15:06    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.CMPOSITION;
import com.rzt.service.CMPOSITIONService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

/**
 * 类名称：CMPOSITIONController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/17 17:15:06 
 * 修改人：张虎成    
 * 修改时间：2017/12/17 17:15:06    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("CMPOSITION")
public class CMPOSITIONController extends
		CurdController<CMPOSITION,CMPOSITIONService> {

	@ApiOperation(value = "坐标测试",notes = "坐标测试")
	@GetMapping("savePosition")
	public WebApiResponse savePosition(String lon,String lat,String createTime) throws ParseException {
		CMPOSITION cmposition = new CMPOSITION();
		cmposition.setId(null);
		cmposition.setLon(lon);
		cmposition.setLat(lat);
		cmposition.setCreateTime(DateUtil.parse(createTime));
		service.add(cmposition);
		return WebApiResponse.success("上传成功");
	}
	
}