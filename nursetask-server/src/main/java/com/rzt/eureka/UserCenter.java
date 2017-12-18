package com.rzt.eureka;

import com.rzt.util.WebApiResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
/**
 * Created by admin on 2017/12/17.
 */

@FeignClient("USERCENTER")
public interface UserCenter {

    @GetMapping(value = "/userCenter/RztSysMenu/treeQuery")
    List treeQuery(@RequestParam("id") String id);

    @GetMapping(value = "/userCenter/RztSysUser/userQuery")
    WebApiResponse userQuery(@RequestParam("classname") String classname,@RequestParam("realname") String realname);

    @GetMapping(value = "/userCenter/RztSysDepartment/queryOrgName")
    WebApiResponse queryOrgName();
  //  http://localhost:8098/userCenter/RztSysDepartment/findDeptListByPid
    @GetMapping(value = "/userCenter/Rztsyscompany/queryCompanyname")
    WebApiResponse queryCompanyname();
}