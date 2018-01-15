package com.rzt.controller;

import com.rzt.entity.Monitorcheckej;
import com.rzt.service.tourPublicService;
import com.rzt.util.WebApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("GJKH")
public class tourPublicController extends CurdController<Monitorcheckej, tourPublicService> {

    //未到杆塔半径5米内
    @RequestMapping("xsTourScope")
    public WebApiResponse xsTourScope(Long taskid, String orgid, String userid) {
        return this.service.xsTourScope(taskid, orgid, userid);
    }

    //巡视未按标准速率拍照
    @RequestMapping("takePhoto")
    public void takePhoto(Long taskid, String orgid, String userid){
        try {
            service.takePhoto(taskid,orgid,userid);
        }catch (Exception e){
            e.getMessage();
        }
    }


    //巡视下线
    @GetMapping("KHXX")
    public WebApiResponse KHXX(String userId,Integer taskType){
        try {
            this.service.KHXX(userId,taskType);
            return WebApiResponse.success("success");
        }catch (Exception e){
            return WebApiResponse.erro("fail"+e.getMessage());
        }
    }

    //巡视上线
    @GetMapping("KHSX")
    public WebApiResponse KHSX(String userId,Integer taskType){
        try {
            this.service.KHSX(userId,taskType);
            return WebApiResponse.success("success");
        }catch (Exception e){
            return WebApiResponse.erro("fail"+e.getMessage());
        }
    }
}
