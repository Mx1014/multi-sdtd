package com.rzt.controller;

import com.rzt.entity.Monitorcheckej;
import com.rzt.service.tourPublicService;
import com.rzt.util.WebApiResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Scope")
public class tourPublicController extends CurdController<Monitorcheckej, tourPublicService> {

    //未到杆塔半径5米内
    @RequestMapping("xsTourScope")
    public WebApiResponse xsTourScope(Long taskid, String orgid, String userid) {
        return this.service.xsTourScope(taskid, orgid, userid);
    }
}
