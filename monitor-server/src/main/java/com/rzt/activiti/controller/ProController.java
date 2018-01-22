package com.rzt.activiti.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.activiti.Eureka.nurseTaskService;
import com.rzt.entity.KhYhHistory;
import com.rzt.repository.YHrepository;
import com.rzt.activiti.service.impl.ProServiceImpl;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.RedisUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 隐患上报处理流程
 * 李成阳
 * 2018/1/15
 */
@RestController
@RequestMapping("/pro")
public class ProController {
    @Autowired
    private ProServiceImpl proService;
    @Autowired
    private nurseTaskService nurseTaskService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private YHrepository yHrepository;
    @Autowired
    private RedisUtil redisUtil;


    /**
     * 开启流程
     * @return
     */
    @GetMapping("/start")
    public WebApiResponse start(String key , String userName,String YHID,String flag,String info,String khid,String isKH){
        HashMap<Object, Object> map = new HashMap<>();
        map.put("userName",userName);
        map.put("YHID",YHID);
        map.put("flag",flag);
        map.put("info",info);
        map.put("khid",khid);
        ProcessInstance start = proService.start(key, map);
        System.out.println(start);
        return WebApiResponse.success("");
    }

    /**
     * 进入流程节点
     * 此处可以选择当前待办是否进入  下一步流程
     * @param taskId   当前任务id
     * @param YHID         当前上报隐患id
     * @param flag          选择节点使用的标志
     * @return
     */
    @GetMapping("/complete")
    public WebApiResponse complete(String taskId,String YHID,String flag,String khid){
        Map<String, Object> map = new HashMap<>();
        map.put("YHID",YHID);
        map.put("flag",flag);
        map.put("khid",khid);
        proService.complete(taskId,map);

        return WebApiResponse.success("");
    }

    /**
     * 详细页中的处理按钮   针对监控中心
     * 当发送处理请求 准备派出稽查人员  并且判断是否生成临时看护任务
     * 生成稽查任务时需要带有 当前任务id   当稽查人员查看完毕时根据任务id判断节点方向
     * @param taskId   当前任务id
     * @param YHID      隐患id
     * @param flag      节点判断标记
     * @param isKH      是否派出看护任务
     * @param info      描述
     * @param proId      当前流程实例id
     * @return
     */
    @GetMapping("/proClick")
    public WebApiResponse chuLi(String taskId,String YHID,String flag,String isKH,String info,String proId,String userName){
      try {

          userName = redisUtil.findRoleIdByUserId(userName);
          //将稽查和看护任务派发     完成后拿到看护任务的id  进入下一个节点 稽查节点
          String khid = "";
          //需要派发稽查任务
          Map<String, Object> map = new HashMap<>();
          if (null != isKH && "1".equals(isKH)){
              //需要派发看护任务

              String sql = "";
              if(null != YHID && !"".equals(YHID)){
                  //Object data = nurseTaskService.saveLsCycle(YHID).getData();
                  //System.out.println("++++++++++++++++"+data);
                  //生成看护任务成功  添加看护id到流程
                  map.put("khid",khid);
              }
              System.out.println("看护任务派发---------------------------------------");
          }


          map.put("YHID",YHID);
          map.put("flag",flag);
          map.put("info",info);
          proService.complete(taskId,map);

          //获取节点前进后的id   用流程实例id做条件
          String id = proService.findIdByProId(proId);
          //测试稽查
          if("sdid".equals(userName) || "jkid".equals(userName)){
              proService.complete(id,map);
          }

          if(null == id || "".equals(id)){
              return WebApiResponse.erro("当前节点任务不存在");
          }
         /* String tdByUserId = redisUtil.findTDByUserId(userName);
          System.out.println(tdByUserId+"============================登录人通道单位======");*/
          System.out.println("节点任务id"+id+"-------------------------------------------");
          //调用稽查接口   派发稽查任务
      }catch (Exception e){
          System.out.println("------------");
          System.out.println(e.getMessage());
      }
        return WebApiResponse.success("进入下一节点");

    }





    /**
     * 稽查任务回调   回调时需要传递当前任务id  和flag  隐患id
     * @param taskId
     * @param YHID
     * @param flag
     * @return
     */
    @GetMapping("/jchd")
    public WebApiResponse jicha(String taskId,String YHID,String flag,String isKH){
        //稽查任务回调   回调时需要传递当前任务id  和flag  隐患id





     /*   Map<String, Object> map = new HashMap<>();
        map.put("YHID",YHID);
        map.put("flag",flag);
        proService.complete(taskId,map);*/
        return null;
    }
    /**
     * 查看所有待办任务
     * @param userId   传入当前节点名
     * @return
     */
    @GetMapping("/findTaskByUserName")
    public WebApiResponse toTask(String userId,Integer page,Integer size){
        String roleIdByUserId = redisUtil.findRoleIdByUserId(userId);
        return proService.checkTask(roleIdByUserId,page,size);

    }

    /**
     * 查看待办任务
     * @param userName 执行人
     * @param taskId   当前任务
     * @return
     */
    @GetMapping("/task")
    public WebApiResponse task(String userName,String taskId){
        return WebApiResponse.success(proService.checkTask(taskId,userName));

    }

    /**
     * 在每一个流程开始时都需要先部署当前的流程
     * 部署流程
     * @return
     */
    @GetMapping("/dep")
    public WebApiResponse dep(){
        proService.deploy();
        return WebApiResponse.success("");
    }



    @GetMapping("/history")
    public WebApiResponse gethi(String assignee,Integer page,Integer size){
        String roleIdByUserId = redisUtil.findRoleIdByUserId(assignee);
        System.out.println(roleIdByUserId+"====================用户id换 节点名");
        return proService.historyActInstanceList(assignee, page, size);
    }



    /**
     * 完善隐患信息
     * @param YHID 隐患id
     * @param YHMS 隐患描述
     * @param YHTDQX 区县
     * @param YHTDXZJD 乡镇
     * @param YHTDC 村
     * @param GKCS  管控措施
     * @param XCP   宣传牌
     * @return
     */
    @GetMapping("/perfectYH")
    @Transactional
    public WebApiResponse perfectYH(String YHID,String YHMS,String YHTDQX,String YHTDXZJD,String YHTDC,String GKCS,String XCP){
        System.out.println(GKCS);
        try {
            yHrepository.perfectYH(YHID,YHMS,YHTDQX,YHTDXZJD,YHTDC,GKCS,XCP);
            return WebApiResponse.success("");
        }catch (Exception e){
            return WebApiResponse.erro("更新隐患信息失败"+e.getMessage());
        }
    }
    @GetMapping("/tree")
    public WebApiResponse tree(){
        return proService.tree();
    }



}
