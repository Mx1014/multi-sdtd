package com.rzt.redisListener;

import org.springframework.stereotype.Component;

/**
 * Created by huyuening on 2018/1/5.
 */
@Component
public class DSRedis {

  /*  @Autowired
    JedisPool jedisPool;


    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //@Scheduled(cron = "0/1 * * * * ? ")
    public void addString(){
        Jedis jedis = jedisPool.getResource();
        try {

           //jedis.setex("a",5,"123456");
            jedis.select(1);
            String date = "2019-01-08 15:05:00";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date strtodate = formatter.parse(date);
                jedis.psetex(String.valueOf(UUID.randomUUID()), Long.parseLong(String.valueOf(strtodate.getTime() - DateUtil.dateNow().getTime())) / 1000, "测试");
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("------");
            }
            System.out.println("dsfsdfsafsdfsadfdsfsfdsafssdf-------------------------===========================");

        }catch (Exception e){
            System.out.println("添加失败"+e.getMessage());
        }finally {
            jedis.close();
        }
    }*/

}
