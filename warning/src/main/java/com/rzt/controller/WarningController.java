package com.rzt.controller;


import com.rzt.entity.OffPostUser;
import com.rzt.entity.OffPostUserTime;
import com.rzt.eureka.StaffLine;
import com.rzt.service.WarningOffPostUserService;
import com.rzt.service.WarningOffPostUserTimeService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("warning")
public class WarningController extends CurdController<OffPostUser,WarningOffPostUserService> {
	
	@Autowired
	private WarningOffPostUserService service;

	@Autowired
	private WarningOffPostUserTimeService timeService;

	@Autowired
	private StaffLine staffLine;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	/**
	 * 添加看护人员脱岗信息
	 *@Author hyn
	 *@Method KHOffPost
	 *@Params [offPostUser]
	 *@Date 2017/12/27 18:08
	 */
	@GetMapping("KHOffPost")
	public Object KHOffPost(OffPostUser offPostUser){
		try {
			if(offPostUser!=null){
				OffPostUser offUser = service.findByUserIdAndTaskId(offPostUser.getUserId(),offPostUser.getTaskId());
				if(offUser==null){
					if(offPostUser.getStatus()==1){
						//添加OffPostUser
						offPostUser.setCreateTime(new Date());
						service.addUser(offPostUser);
						//添加OffPostUserTime
						timeService.addOffUserTime(offPostUser.getUserId(),offPostUser.getTaskId());

					}
				}else{
					//判断该人员状态是否改变
					if (offUser.getStatus()!=offPostUser.getStatus()){
                        List<OffPostUserTime> list = timeService.findByUserIdAndDateisNull(offUser.getUserId(),offUser.getTaskId());
                        if(offPostUser.getStatus()==0){
                            //回岗
                            if(list.size()>0){
                                OffPostUserTime offPostUserTime = list.get(0);
                                //如果该条时间记录已经存在，则只更新回岗时间
                                //offPostUserTime.setEndTime(new Date());
                                timeService.updateOffUserEndTime(offPostUserTime);
                            }
                            String key  = offPostUser.getUserId()+"#"+offPostUser.getTaskId();
							removeTuoGangRedis(key);
							service.updateAlarmOffWorkStatus(offPostUser.getUserId(),offPostUser.getTaskId());
                        }else if(offPostUser.getStatus()==1){
                            //脱岗
                            if(list.size()==0){
                                timeService.addOffUserTime(offPostUser.getUserId(),offPostUser.getTaskId());
                            }
						}

						//更新脱岗人员状态
						offUser.setStatus(offPostUser.getStatus());
						service.updateOffUser(offUser);

					}
				}

			}
			return new WebApiResponse().success("添加成功");
		} catch (Exception e) {
			return new WebApiResponse().erro("添加失败"+e.getMessage());
		}
	}


	/**
	 * 定时查询 如果脱岗开始时间超过60分钟则正式记为脱岗
	 */
	@Scheduled(fixedRate = 60000)
	public void changeTG(){
		String sql = "SELECT * FROM WARNING_OFF_POST_USER_TIME WHERE END_TIME is NULL " +
				"AND (sysdate-START_TIME)*24*60*60>3600 AND TIME_STATUS=0 ";
		List<Map<String, Object>> maps = service.execSql(sql);
		for (Map<String, Object> map :maps){
			Long fk_task_id = Long.parseLong(map.get("FK_TASK_ID").toString());
			//判断该任务是不是在进行中，只有进行中才进行报警
			String sql1 = "SELECT * FROM KH_TASK WHERE ID=?1 AND STATUS=1";

            List<Map<String, Object>> maps1 = service.execSql(sql1, fk_task_id);
            if(maps1.size()>0){
                String fk_user_id = (String) map.get("FK_USER_ID");
                Long id = Long.parseLong(map.get("ID").toString());
                int i = service.updateTimeStatus(fk_task_id,fk_user_id,id);
                if(i>0){
                    try {
                        //staffLine.khtg(fk_user_id,fk_task_id); //由于怕feign调用服务出错，将告警代码直接移到这个项目中
                        service.KHTG(fk_user_id,fk_task_id); //看护告警代码
                        String value = map.get("START_TIME").toString();
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date da = format.parse(value);
                        service.tuoGangRedis(fk_user_id,fk_task_id,da);
                    } catch (Exception e) {
                    	e.printStackTrace();
                    }
                }
            }
		}
	}

	@GetMapping("khtg") //脱岗测试使用
	public void khtg(String fk_user_id,Long fk_task_id){
		Date da = new Date();
		service.tuoGangRedis(fk_user_id,fk_task_id,da);
	}
	@GetMapping("hg") //回岗测试使用
	public void hg(String userId,Long taskId){
		service.updateAlarmOffWorkStatus(userId,taskId);
		String key = userId+"#"+taskId;
		removeTuoGangRedis(key);
	}
	//人员回岗后删除redis中的键
	//@GetMapping("removeTuogang")
	public void removeTuoGangRedis(String s) {
		RedisConnection connection = null;
		try {
			connection = redisTemplate.getConnectionFactory().getConnection();
			connection.select(5);
			byte[] bytes = "tuogang".getBytes();
			connection.hDel(bytes,s.getBytes());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}



}
