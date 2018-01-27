/**    
 * 文件名：KHYHHISTORYController
 * 版本信息：    
 * 日期：2017/11/30 18:31:34    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.alibaba.fastjson.JSONObject;
import com.netflix.discovery.converters.Auto;
import com.rzt.entity.KhTask;
import com.rzt.entity.KhYhHistory;
import com.rzt.entity.XsSbYh;
import com.rzt.service.KhYhHistoryService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
	@Autowired
	private RedisTemplate<String,Object> redisTemplate;

	@ApiOperation(notes = "施工情况",value = "施工情况")
	@PostMapping("/saveYh")
	@ResponseBody
	public WebApiResponse saveYh(XsSbYh yh,String startTowerName, String endTowerName, String pictureId){
		return this.service.saveYh(yh,startTowerName,endTowerName,pictureId);
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

	@ApiOperation(value = "隐患导入接口", notes = "隐患导入接口")
	@PostMapping("ImportYh")
	public WebApiResponse ImportYh(MultipartFile file) {
		if (file.getName().contains("xls")){
			return service.ImportYh(file);
		}else{
			return service.ImportYh2(file);
		}
	}

	@ApiOperation(value = "隐患导入模板",notes = "隐患导入模板")
	@GetMapping("ImportYhExam")
	public void ImportYhExam(HttpServletResponse response){
		 service.ImportYhExam(response);
	}

	@ApiOperation(value = "隐患导出接口",notes = "隐患导出接口")
	@GetMapping("exportYhHistory")
	public WebApiResponse exportYhHistory(HttpServletResponse response,String currentUserId){
     /* MultipartFile file */
		HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
		JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", currentUserId).toString());
		return service.exportYhHistory(response,jsonObject,currentUserId);
	}

	@ApiOperation(value = "修改隐患信息",notes = "修改隐患信息")
	@PatchMapping("updateYhHistory")
	public WebApiResponse updateYhHistory(KhYhHistory yh,String startTowerName, String endTowerName){
		return service.updateYhHistory(yh,startTowerName,endTowerName);
	}

	@ApiOperation(value = "隐患重新定级",notes = "隐患重新定级")
	@PatchMapping("updateYhjb")
	public WebApiResponse updateYhjb(String yhjb){
		return service.updateYhjb(yhjb);
	}

	@ApiOperation(value = "区镇村三级联动",notes = "区镇村三级联动")
	@GetMapping("lineArea")
	public WebApiResponse lineArea(Integer id){
     /* MultipartFile file */
		return service.lineArea(id);

	}
	@GetMapping("/a123")
	public void a123(){
		this.service.find();
	}

	@ApiOperation(value = "隐患审核通过",notes = "隐患审核通过")
	@GetMapping("reviewYh")
	public WebApiResponse reviewYh(long yhId){
		return this.service.reviewYh(yhId);
	}

	@ApiOperation(value = "隐患台账删除",notes = "隐患台账删除")
	@DeleteMapping("deleteYhById")
	public WebApiResponse deleteYhById(long yhId){
		return this.service.deleteYhById(yhId);
	}

    @ApiOperation(value = "杆塔坐标采集",notes = "杆塔坐标采集")
    @GetMapping("updateTowerById")
    public WebApiResponse updateTowerById(long id,String lon,String lat){
        return this.service.updateTowerById(id,lon,lat);
    }

    @ApiOperation(value = "判断线路是否属于通州公司、门头沟公司",notes = "隐患台账删除")
    @GetMapping("findLineOrg")
    public WebApiResponse findLineOrg(long towerId){
        return this.service.findLineOrg(towerId);
    }

	@ApiOperation(value = "隐患台账图片展示",notes = "隐患台账图片展示")
	@GetMapping("findYhPicture")
	public WebApiResponse findYhPicture(long yhId){
		return this.service.findYhPicture(yhId);
	}
}