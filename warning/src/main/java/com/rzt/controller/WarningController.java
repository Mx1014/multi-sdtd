package com.rzt.controller;


import com.rzt.entity.OffPostUser;
import com.rzt.entity.OffPostUserTime;
import com.rzt.service.WarningOffPostUserService;
import com.rzt.service.WarningOffPostUserTimeService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("warning")
public class WarningController extends CurdController<OffPostUser,WarningOffPostUserService> {
	
	@Autowired
	private WarningOffPostUserService service;

	@Autowired
	private WarningOffPostUserTimeService timeService;
	
	/**
	 * 添加看护人员脱岗信息
	 *@Author hyn
	 *@Method KHOffPost
	 *@Params [offPostUser]
	 *@Date 2017/12/27 18:08
	 */
	@GetMapping("KHOffPost")
	public Object KHOffPost(OffPostUser offPostUser){
		try {
			if(offPostUser!=null){
				OffPostUser offUser = service.findByUserIdAndTaskId(offPostUser.getUserId(),offPostUser.getTaskId());
				if(offUser==null){
					//添加OffPostUser
					offPostUser.setCreateTime(new Date());
					service.addUser(offPostUser);
					//添加OffPostUserTime
					timeService.addOffUserTime(offPostUser.getUserId());
				}else{
					//判断该人员状态是否改变
					if (offUser.getStatus()!=offPostUser.getStatus()){
						OffPostUserTime offPostUserTime = timeService.findByUserIdAndDateisNull(offUser.getUserId());
						if(offPostUserTime!=null){
							//如果该条时间记录已经存在，则只更新回岗时间
							offPostUserTime.setEndTime(new Date());
							timeService.updateOffUserEndTime(offPostUserTime);
						}else{
							//如果该条时间记录不存在，则重新添加一条
							timeService.addOffUserTime(offPostUser.getUserId());
						}
						//更新脱岗人员状态
						offUser.setStatus(offPostUser.getStatus());
						service.updateOffUser(offUser);
					}
				}
			}
			return new WebApiResponse().success("添加成功");
		} catch (Exception e) {
			return new WebApiResponse().erro("添加失败"+e.getMessage());
		}
	}

}