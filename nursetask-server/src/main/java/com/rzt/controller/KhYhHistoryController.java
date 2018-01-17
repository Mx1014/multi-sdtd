/**    
 * 文件名：KHYHHISTORYController
 * 版本信息：    
 * 日期：2017/11/30 18:31:34    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.KhTask;
import com.rzt.entity.KhYhHistory;
import com.rzt.entity.XsSbYh;
import com.rzt.service.KhYhHistoryService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;

/**
 * 类名称：KHYHHISTORYController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/30 18:31:34 
 * 修改人：张虎成    
 * 修改时间：2017/11/30 18:31:34    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("kyYhHistory")
public class KhYhHistoryController extends
		CurdController<KhYhHistory, KhYhHistoryService> {

	@ApiOperation(notes = "施工情况",value = "施工情况")
	@PostMapping("/saveYh")
	@ResponseBody
	public WebApiResponse saveYh(XsSbYh yh, String fxtime, String startTowerName, String endTowerName, String pictureId){
		return this.service.saveYh(yh,fxtime,startTowerName,endTowerName,pictureId);
	}

	@ApiOperation(notes = "隐患台账保存坐标",value = "隐患台账保存坐标")
	@PostMapping("/saveCoordinate")
	@ResponseBody
	public WebApiResponse saveCoordinate(String yhId,String lat,String lon,String radius){
		return this.service.saveCoordinate(yhId,lat,lon,radius);
	}

	@ApiOperation(notes = "地图撒坐标点",value = "地图撒坐标点")
	@GetMapping("/listCoordinate")
	@ResponseBody
	public WebApiResponse listCoordinate(String yhjb,String yhlb){
		return this.service.listCoordinate(yhjb,yhlb);
	}

	@ApiOperation(notes = "地图查看隐患信息",value = "地图查看隐患信息")
	@GetMapping("/listYhById")
	@ResponseBody
	public WebApiResponse listYhById(String yhId){
		return this.service.listYhById(yhId);
	}

	@ApiOperation(value = "隐患导入接口",notes = "隐患导入接口")
	@PostMapping("ImportYh")
	public WebApiResponse ImportYh(){
/* MultipartFile file */
		return service.ImportYh();
	}
	@ApiOperation(value = "隐患导出接口",notes = "隐患导出接口")
	@GetMapping("exportYhHistory")
	public WebApiResponse exportYhHistory(HttpServletResponse response){
     /* MultipartFile file */
		return service.exportYhHistory(response);
	}

	@ApiOperation(value = "修改隐患信息",notes = "修改隐患信息")
	@GetMapping("updateYhHistory")
	public WebApiResponse updateYhHistory(KhYhHistory yh){
		return service.updateYhHistory(yh);
	}
}