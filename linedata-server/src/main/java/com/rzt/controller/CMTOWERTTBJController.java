/**    
 * 文件名：CMTOWERTTBJController
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.CMTOWERTTBJ;
import com.rzt.service.CMTOWERTTBJService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类名称：CMTOWERTTBJController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("CMTOWERTTBJ")
public class CMTOWERTTBJController extends
		CurdController<CMTOWERTTBJ,CMTOWERTTBJService> {

	@ApiOperation(value = "获取同塔并架线路信息接口",notes = "获取同塔并架线路信息接口,分页搜索")
	@GetMapping("getTtbjLine")
	public WebApiResponse getTtbjLine(@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "15") Integer size, String tdOrg, String kv, String lineId) {
		Pageable pageable = new PageRequest(page, size);
		List<String> list = new ArrayList<>();
		String sql = "select t.v_level_1, (select line_name from cm_line where id=t.line1_id) line1,t.section1," +
				"t.V_LEVEL_2,(select line_name from cm_line where id=t.line2_id) line2,t.section2 ," +
				"t.V_LEVEL_3,(select line_name from cm_line where id=t.line3_id) line3,t.section3 ," +
				"t.V_LEVEL_4,(select line_name from cm_line where id=t.line4_id) line4,t.section4 " +
				"from cm_tower_ttbj t where 1=1 ";
		if(tdOrg!=null&&!"".equals(tdOrg.trim())){
			List<Object> ids = service.getIdsByTdorg(tdOrg);
			String join = StringUtils.join(ids, ",");
			System.out.println(join);
			sql += " and (line1_id in ("+join+") or line2_id in ("+join+") or line3_id in ("+join+") or line4_id in （" +join+")) " ;
		}
		if(kv!=null&&!"".equals(kv.trim())){
			list.add(kv);
			sql += " and (v_level_1= ?"+list.size()+" or v_level_2= ?"+list.size()+" or v_level_3= ?"+list.size()+" or v_level_4= ?" + list.size() +")";
		}
		if(lineId!=null&&!"".equals(lineId.trim())){
			list.add(lineId);
			sql += " and (line1_id= ?"+list.size()+" or line2_id= ?"+list.size()+" or line3_id= ?"+list.size()+" or line4_id= ?" + list.size() +")";
		}
		Page<Map<String, Object>> maps = service.execSqlPage(pageable, sql,list.toArray());
		return WebApiResponse.success(maps);
	}
}