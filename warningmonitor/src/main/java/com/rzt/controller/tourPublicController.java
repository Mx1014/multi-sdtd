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

    //未到杆塔半径5米内(无法到位)
    @GetMapping("xsTourScope")
    public WebApiResponse xsTourScope(Long taskid, String userid) {
        return this.service.xsTourScope(taskid, userid);
    }

    //巡视未按标准速率拍照
    @GetMapping("takePhoto")
    public WebApiResponse takePhoto(Long taskid, String userid){
        try {
            service.takePhoto(taskid,userid);
            return WebApiResponse.success("success");
        }catch (Exception e){
            return WebApiResponse.erro("巡视未按标准拍照添加失败");
        }
    }

    //看护不到位
    @GetMapping("khWFDW")
    public void khWFDW(Long taskid, String userid){
        this.service.khWFDW(taskid,userid);

    }

    /**
     * 看护脱岗
     * @param userId
     * @param taskId
     */
    @GetMapping("khtg")
    public void khtg(String userId,Long taskId){
        try{
            service.KHTG(userId,taskId);
        }catch (Exception e){
            e.getMessage();
        }
    }

    @GetMapping("delKey")
    public void delKey(String userId,Long taskId,Integer taskType){
        try{
            service.delKey(userId,taskId,taskType);
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

    @GetMapping("khtgang")
    public WebApiResponse khtgang(Long taskId){
        try{

           return WebApiResponse.success(service.khtgang(taskId));
        }catch (Exception e){
           return WebApiResponse.erro("fail:"+e.getMessage());
        }

    }


}
