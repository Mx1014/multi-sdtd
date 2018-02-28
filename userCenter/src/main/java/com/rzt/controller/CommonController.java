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
     * @param currentUserId
     * @return
     */
    @GetMapping("checkDepartment")
    public List<Map<String, Object>> checkDepartment(String currentUserId, Integer worktype) {
        return this.service.checkDepartment(currentUserId, worktype);
    }

    /**
     * 二级单位通道查询搜索框
     * @param currentUserId
     * @param worktype
     * @return
     */
    @GetMapping("checkDepartmentAll")
    public List<Map<String, Object>> checkDepartmentAll(String currentUserId, Integer worktype) {
        return this.service.checkDepartmentAll(currentUserId, worktype);
    }
    /**
     * 查询稽查人员
     *
     * @param classId
     * @param currentUserId
     * @return
     */
    @GetMapping("userJcCx")
    public WebApiResponse userJcCx(String classId, String currentUserId, Integer worktype) {
        return this.service.userJcCx(classId, currentUserId, worktype);
    }
}
