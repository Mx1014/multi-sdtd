package com.rzt.service.app;

import com.rzt.entity.KhTask;
import com.rzt.entity.KhTaskWpqr;
import com.rzt.eureka.UserCenterService;
import com.rzt.repository.AppKhTaskRepository;
import com.rzt.repository.AppKhUpdateRepository;
import com.rzt.service.CurdService;
import com.rzt.service.KhTaskWpqrService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import com.rzt.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import static java.lang.Integer.*;

/**
 * Created by admin on 2017/12/22.
 */
@Service
public class AppKhUpdateService extends CurdService<KhTask, AppKhUpdateRepository> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private KhTaskWpqrService wpqrService;
    @Autowired
    private UserCenterService userService;

    //修改实际开始时间
    public WebApiResponse updateRealTime(long taskId,String userId) {
        try {
            int num = this.reposiotry.findNum(taskId);
            if (num < 1) {
                //if (isdw != null && reason != null) {
                this.reposiotry.updateRealStartTime(taskId, DateUtil.dateNow());
                this.reposiotry.updateZxnum(1, taskId);//修改执行页数
                userService.updateKhInfoStatusInredis(userId);
            }
            removeSomeKey(taskId);
            return WebApiResponse.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("修改失败");
        }
    }

    //修改身份确认时间
    public WebApiResponse updateSfqrTime(long taskId) {
        try {
            int num = this.reposiotry.findNum(taskId);
            if (num < 2) {
                this.reposiotry.updateSFQRTime(DateUtil.dateNow(), taskId);
                this.reposiotry.updateZxnum(2, taskId);//修改执行页数
            }
            return WebApiResponse.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("修改失败");
        }
    }

    @Transactional
    public WebApiResponse updateWpqrTime(KhTaskWpqr task) {
        try {
            String sql1 = "select id,taskid,wp_zt from KH_TASK_WPQR where taskid = ?";
            Map<String, Object> map = this.execSqlSingleResult(sql1, task.getTaskId());
            this.reposiotry.updateWp(task.getWpzt(), task.getTaskId());
            return WebApiResponse.success("修改成功");
        } catch (Exception e) {
            task.setId();
            wpqrService.add(task);
            this.reposiotry.updateWpqrTime(task.getTaskId(), DateUtil.dateNow());
            this.reposiotry.updateZxnum(3, task.getTaskId());
            return WebApiResponse.success("添加成功");
        }
    }

    public WebApiResponse updateKhtx(long taskId) {
        try {
            int num = this.reposiotry.findNum(taskId);
            if (num < 4) {
                this.reposiotry.updateZxnum(4, taskId);//修改执行页数
            }
            return WebApiResponse.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("修改失败");
        }
    }

    public WebApiResponse updateDdxcTime(Long taskId, String isdw, String reason) {
        try {
            int num = this.reposiotry.findNum(taskId);
            if (num < 5) {
                this.reposiotry.updateDDTime(DateUtil.dateNow(), taskId, Integer.parseInt(isdw), reason);
                this.reposiotry.updateZxnum(5, taskId);//修改执行页数
            } else {
                this.reposiotry.updateIsDz(taskId, Integer.parseInt(isdw), reason);
            }
            return WebApiResponse.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("修改失败");
        }
    }

    public WebApiResponse updateClzt(String clzt, long taskId) {
        try {
            int num = this.reposiotry.findNum(taskId);
            this.reposiotry.updateClzt(clzt, taskId);
            this.reposiotry.updateZxnum(6, taskId);//修改执行页数
            return WebApiResponse.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("修改失败");
        }
    }

    public WebApiResponse updateEndTime(long taskId,String userId) {
        try {
            this.reposiotry.updateEndTime(DateUtil.dateNow(), taskId);
            userService.updateKhInfoStatusInredis(userId);
            return WebApiResponse.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("修改失败");
        }
    }

    public void removeSomeKey(Long id) {
        String s = "TWO+" + id + "+2+10*";
        RedisConnection connection = null;
        try {
            connection = redisTemplate.getConnectionFactory().getConnection();
            connection.select(1);
            Set<byte[]> keys = connection.keys(s.getBytes());
            byte[][] ts = keys.toArray(new byte[][]{});
            if (ts.length > 0) {
                connection.del(ts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
}
