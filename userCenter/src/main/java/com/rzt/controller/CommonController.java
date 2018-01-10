package com.rzt.controller;

import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("COMMON")
public class CommonController extends CurdController<RztSysUser, CommonService> {
    /**
     * 稽查单位查询
     *
     * @param userId
     * @return
     */
    @GetMapping("checkDepartment")
    public List<Map<String, Object>> checkDepartment(String userId,Integer worktype) {
        return this.service.checkDepartment(userId,worktype);
    }

    /**
     * 查询稽查人员
     *
     * @param classId
     * @param userId
     * @return
     */
    @GetMapping("userJcCx")
    public WebApiResponse userJcCx(String classId, String userId,Integer worktype) {
        return this.service.userJcCx(classId, userId,worktype);
    }
}
