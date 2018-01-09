package com.rzt.controller;

import com.rzt.entity.Monitorcheckyj;
import com.rzt.service.Monitorcheckyjservice;
import com.rzt.service.tourPublicService;
import com.rzt.util.WebApiResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("SCOPE")
public class tourPublicController extends
        CurdController<Monitorcheckyj, tourPublicService> {
    /**
     * 巡视人员未到杆塔半径5米范围内
     *
     * @param taskid      任务ID
     * @param orgid       班组id
     * @param userid      人员ID
     * @param warningtype 告警类型
     * @return
     */
    @RequestMapping("xsTourScope")
    public WebApiResponse xsTourScope(Long taskid, Integer warningtype, String orgid, String userid) {
        return this.service.xsTourScope(taskid, warningtype, orgid, userid);
    }
}
