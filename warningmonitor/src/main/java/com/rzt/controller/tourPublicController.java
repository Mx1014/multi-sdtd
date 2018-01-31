package com.rzt.controller;

import com.rzt.entity.Monitorcheckej;
import com.rzt.service.tourPublicService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("GJKH")
public class tourPublicController extends CurdController<Monitorcheckej, tourPublicService> {

    //未到杆塔半径5米内(无法到位)
    @GetMapping("xsTourScope")
    public WebApiResponse xsTourScope(Long taskid, String userid,String reason) {
        return this.service.xsTourScope(taskid, userid,reason);
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

    //看护脱岗
    @GetMapping("khtgang")
    public WebApiResponse khtgang(Long taskId){
        try{
           return WebApiResponse.success(service.khtgang(taskId));
        }catch (Exception e){
           return WebApiResponse.erro("fail:"+e.getMessage());
        }

    }

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    //Long id
    @GetMapping("remoceKey12")
    public void removeKey(){
        String s = "TWO+*+2+8+*";
        RedisConnection connection = null;
        try {
            connection = redisTemplate.getConnectionFactory().getConnection();
            connection.select(1);
            Set<byte[]> keys = connection.keys(s.getBytes());
            byte[][] ts = keys.toArray(new byte[][]{});
            if(ts.length > 0) {
                connection.del(ts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

}
