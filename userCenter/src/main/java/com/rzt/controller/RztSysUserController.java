/**    
 * 文件名：RztSysUserController
 * 版本信息：    
 * 日期：2017/10/10 17:28:27    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.controller.CurdController;
import com.rzt.entity.RztSysUser;
import com.rzt.entity.RztSysUserauth;
import com.rzt.service.RztSysUserService;
import com.rzt.service.RztSysUserauthService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类名称：RztSysUserController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/10/10 17:28:27 
 * 修改人：张虎成    
 * 修改时间：2017/10/10 17:28:27    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("RztSysUser")
public class RztSysUserController extends
		CurdController<RztSysUser,RztSysUserService> {
	@Autowired
	private RztSysUserauthService userauthService;

	@PostMapping("addUser/{password}")
	public WebApiResponse addUser(@RequestParam("test") MultipartFile file, @PathVariable String password, @ModelAttribute RztSysUser user) {
		String filePath = "";
		if (!file.isEmpty()) {
			// 获取文件名
			String fileName = file.getOriginalFilename();
			// 文件上传后的路径
			filePath = "E://test//" + fileName;
			File dest = new File(filePath);
			try {
				file.transferTo(dest);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		user.setAvatar(filePath);
		String flag = userauthService.findByUserName(user);
		if (!flag.equals("1")){
			return WebApiResponse.erro(flag);
		}
		user.setCreatetime(new Date());
		this.service.add(user);
		userauthService.addUserAuth(user,password);
		return WebApiResponse.success("添加成功！");
	}

	@DeleteMapping("deleteUser/{id}")
	public WebApiResponse deleteUser(@PathVariable String id){
		this.service.delete(id);
		userauthService.deleteAuthByUserId(id);
		return WebApiResponse.success("删除成功！");
	}

	@DeleteMapping("deleteBatchUser")
	public WebApiResponse deleteBatchUser(@RequestParam String ids){
		try {
			this.service.deleteSome(ids);
		} catch (Exception e) {
			e.printStackTrace();
			return WebApiResponse.success("批量删除失败！");
		}
		userauthService.deleteBatchAuthByUserId(ids);
		return WebApiResponse.success("批量删除成功！");
	}


	@PatchMapping("updateUser/{id}/{password}")
	public WebApiResponse updateUser(@PathVariable String id,@ModelAttribute RztSysUser user,
											 @PathVariable String password){
		try {
			this.service.update(user,id);
		} catch (Exception e) {
			e.printStackTrace();
			return WebApiResponse.success("更新失败！");
		}
		userauthService.updateUserAuth(id,password);
		return WebApiResponse.success("更新成功!");
	}

	@GetMapping("findAllUser/{page}/{size}")
	public List<Map<String,Object>> findAllUser(@PathVariable int page, @PathVariable int size, @RequestParam(required=false)
		String name){
		List<Map<String,Object>> pageList = this.service.findUserList(page,size);
		return pageList;
	}

	@GetMapping("findUserById/{id}")
	@ResponseBody
	public RztSysUser findAllUser(@PathVariable String id){
		RztSysUser user = this.service.findOne(id);
		return user;
	}

	/**
	 *
	 * @param flag 0 app登录 1 pc登录
	 * @param loginType
	 * @param account
	 * @param password
	 * @param request
	 * @return
	 */
	@GetMapping("login/{flag}/{loginType}")
	public WebApiResponse login(@PathVariable int flag,@PathVariable int loginType,@RequestParam String account,
										@RequestParam String password, HttpServletRequest request){

		RztSysUserauth userauth = userauthService.findByIdentifierAndIdentityTypeAndPassword(flag,account,password);
		RztSysUser sysUser = null;
		if (userauth != null){
			sysUser = this.service.findOne(userauth.getUserid());
			sysUser.setLoginstatus(1);
			try {
				this.service.update(sysUser,sysUser.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			userauthService.updateLoginIp(request.getRemoteAddr(),sysUser.getId(),flag);
		}
		else{
			return WebApiResponse.erro("用户名或密码不正确！");
		}
		request.getSession().setAttribute("user",sysUser);

		return WebApiResponse.success(sysUser);
	}

	@GetMapping("login/{flag}")
	public WebApiResponse login(@PathVariable int flag,@RequestParam String account,
											@RequestParam String password,@RequestParam String deptid){
		RztSysUser sysUser = this.service.findByUsernameAndDeptid(account,deptid);


		if (sysUser != null){
			RztSysUserauth userauth = userauthService.findByIdentifierAndIdentityTypeAndPassword(flag,account,password);
			if (userauth != null)
				sysUser.setLoginstatus(1);
			else
				return WebApiResponse.erro("用户名或密码不正确！");
			try {
				this.service.update(sysUser,sysUser.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			return WebApiResponse.erro("用户名或单位不正确！");
		}

		return WebApiResponse.success(sysUser);
	}

	@GetMapping("logOut")
	public WebApiResponse logOut(HttpServletRequest request){
		RztSysUser user = (RztSysUser) request.getSession().getAttribute("user");
		user.setLoginstatus(0);
		try {
			this.service.update(user,user.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.getSession().removeAttribute("user");

		return WebApiResponse.success("退出成功！");
	}

}