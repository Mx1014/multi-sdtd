package com.rzt.controller.appController;

import com.rzt.controller.CurdController;
import com.rzt.entity.KhTask;
import com.rzt.service.KhTaskService;
import com.rzt.util.WebApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by admin on 2017/12/8.
 */
@RestController
@RequestMapping("KhAppTask")
public class AppKhTaskController extends
        CurdController<KhTask,KhTaskService> {

    @GetMapping("/appKhTask.do")
    @ResponseBody
    public WebApiResponse appKhTask(int dbyb, Pageable pageable,String userId){
        try {
            this.service.appKhTask(dbyb,pageable,userId);
            return WebApiResponse.success("");
        }catch (Exception e){
            return WebApiResponse.erro("数据获取失败");
        }
    }

    @GetMapping("/updateTaskTime.do")
    @ResponseBody
    public void updateTaskTime(String step, Date time, String id){

        if (step.equals("1")){
            //设置到达现场时间
            this.service.updateDDTime(time,Long.parseLong(id));
        }else if(step.equals("2")){
            //设置身份确认时间
            this.service.updateSFQRTime(time,Long.parseLong(id));
        }else if(step.equals("3")){
            //设置物品确认时间
            this.service.updateWPQRTime(time,Long.parseLong(id));
        }
        else if(step.equals("4")){
            //设置实际开始时间 修改看护任务状态
            this.service.updateRealStartTime(time,Long.parseLong(id));
        }else{
            //交接班,设置世界结束时间

        }

    }
}