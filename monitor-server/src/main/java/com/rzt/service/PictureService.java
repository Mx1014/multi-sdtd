package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.netflix.discovery.converters.Auto;
import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class PictureService extends CurdService<CheckResult, CheckResultRepository>  {

    protected static Logger LOGGER = LoggerFactory.getLogger(PictureService.class);
    @Autowired
    private RedisTemplate<String, Object>  redisTemplate;

    /***
     * 获取详情中默认展示的4条照片
     * @param taskId
     * @param taskType
     *           END_TOWER_ID  这个字段为0是代表当前为杆塔   不为0时代表是通道
     * @return
     */
    public WebApiResponse getPictureAndLine(String taskId, String taskType) {
        String sql = "";
        ArrayList<String> list = new ArrayList<>();
        Page<Map<String, Object>> maps = null;
        if(null != taskId && !"".equals(taskId)){
            list.add(taskId);
        }
        if(null != taskType && !"".equals(taskType)){
            try {
            if("1".equals(taskType)){//巡视   AND END_TOWER_ID = 0
                sql = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME,l.OPERATE_NAME,l.START_TOWER_ID" +
                        "   FROM xs_zc_task k LEFT JOIN XS_ZC_TASK_EXEC x ON k.ID = x.XS_ZC_TASK_ID" +
                        "    LEFT JOIN XS_ZC_TASK_EXEC_DETAIL l ON x.ID = l.XS_ZC_TASK_EXEC_ID RIGHT JOIN PICTURE_TOUR p ON l.ID = p.PROCESS_ID" ;
                sql += "   WHERE p.TASK_ID = ?"+list.size()+"  AND P.FILE_TYPE = 1   ORDER BY  p.CREATE_TIME DESC ";
                return WebApiResponse.success(this.execSqlPage(new PageRequest(0, 3), sql, list.toArray()));
            }
            if("2".equals(taskType)){//看护
                sql = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME,y.LINE_NAME as OPERATE_NAME" +
                        " FROM KH_TASK k LEFT JOIN PICTURE_KH p ON k.ID = p.TASK_ID LEFT JOIN  KH_YH_HISTORY y ON  k.ID = y.TASK_ID";
                sql += "    WHERE k.ID =  ?"+list.size()+" AND FILE_TYPE = 1 ORDER BY p.CREATE_TIME DESC";
                ArrayList<Object> objects = new ArrayList<>();
                Page<Map<String, Object>> maps1 = this.execSqlPage(new PageRequest(0, 3), sql, list.toArray());

                return WebApiResponse.success(maps1);

            }



           }catch (Exception e){
                LOGGER.error("查询杆塔图片失败"+e.getMessage());
                return WebApiResponse.erro("查询杆塔图片失败"+e.getMessage());
            }

        }
        return WebApiResponse.erro("参数错误");
    }

    /**
     * 获取问题检查中的所有照片  以杆塔分组返回
     * @param taskId
     * @param taskType
     * @return
     */
    public WebApiResponse getPictureAndLines(String taskId, String taskType,String currentUserId) {
        String roletype = "";
        //查询当前审核人角色  拿到当前审核角色的上次审核时间 图片拍照时间
        Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", currentUserId);
        if(null != userInformation1 && !"".equals(userInformation1)) {
            JSONObject jsonObject1 = JSONObject.parseObject(userInformation1.toString());
             roletype = (String) jsonObject1.get("ROLETYPE");
        }
        if(null == taskId || "".equals(taskId)) {
           return WebApiResponse.erro("参数无效TaskId="+taskId);
        }
        if(null == taskType || "".equals(taskType)) {
            return WebApiResponse.erro("参数无效taskType="+taskType);
        }
        String groupSql ="";
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> grouupList = new ArrayList<>();
        ArrayList<List> group = new ArrayList<>();
        List<Map<String, Object>> maps = null;
     try {
         if(null != taskType && !"".equals(taskType)){
             String s = "";
             String picTime = "";
             //  查询上一次审核时的图片拍照时间

             String flag  =  "0";
             if(null != roletype  && !"".equals(roletype)){
                 if("0".equals(roletype)){
                     flag = "1";
                 }
             }




             String flagSql = "SELECT PIC_TIME FROM TIMED_TASK WHERE STATUS = 1 AND TASKID = '"+taskId+"' AND PIC_TIME = " +
                     "          (SELECT max(PIC_TIME) FROM TIMED_TASK WHERE STATUS = 1 AND TASKID = '"+taskId+"' AND THREEDAY = '"+flag+"')";
             List<Map<String, Object>> maps2 = this.execSql(flagSql, null);
             if(null != maps2 && maps2.size()>0){
                 Map<String, Object> map = maps2.get(0);
                 if(null != map ){
                      picTime = map.get("PIC_TIME").toString();
                 }
             }


             if(null != picTime && !"".equals(picTime)){
                 s +=  "  AND p.CREATE_TIME > to_date('"+picTime+"','YYYY-MM-dd HH24:mi:ss')";
             }
             if("1".equals(taskType)){//巡视   AND END_TOWER_ID = 0


                //巡视图片查询
                String sql = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME,l.OPERATE_NAME,l.START_TOWER_ID" +
                        "     FROM xs_zc_task k LEFT JOIN XS_ZC_TASK_EXEC x ON k.ID = x.XS_ZC_TASK_ID" +
                        "      LEFT JOIN XS_ZC_TASK_EXEC_DETAIL l ON x.ID = l.XS_ZC_TASK_EXEC_ID RIGHT JOIN PICTURE_TOUR p ON l.ID = p.PROCESS_ID" +
                        "       WHERE p.TASK_ID = '"+taskId+"'  AND P.FILE_TYPE = 1 "+s+" AND OPERATE_NAME IS NOT NULL ORDER BY  p.CREATE_TIME DESC";

                 List<Map<String, Object>> maps1 = this.execSql(sql, null);
                 HashMap<String, Object> stirngObjectHashMap = new HashMap<String, Object>();
                 LOGGER.info("任务图片查询成功");
                 return  WebApiResponse.success(maps1);
                 //当任务还没开启时的人员照片
                 //人员信息图片展示
                /*String sql1 = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME as OPERATE_NAME" +
                         "   FROM xs_zc_task k LEFT JOIN XS_ZC_TASK_EXEC x ON k.ID = x.XS_ZC_TASK_ID" +
                         "    LEFT JOIN XS_ZC_TASK_EXEC_DETAIL l ON x.ID = l.XS_ZC_TASK_EXEC_ID RIGHT JOIN PICTURE_TOUR p ON l.ID = p.PROCESS_ID" +
                         "   WHERE p.TASK_ID = "+taskId+" AND FILE_TYPE = 1 AND p.PROCESS_ID =1";

                 String sql2 = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME as OPERATE_NAME" +
                         "   FROM xs_zc_task k LEFT JOIN XS_ZC_TASK_EXEC x ON k.ID = x.XS_ZC_TASK_ID" +
                         "    LEFT JOIN XS_ZC_TASK_EXEC_DETAIL l ON x.ID = l.XS_ZC_TASK_EXEC_ID RIGHT JOIN PICTURE_TOUR p ON l.ID = p.PROCESS_ID" +
                         "   WHERE p.TASK_ID = "+taskId+" AND FILE_TYPE = 1 AND p.PROCESS_ID =2";

                 String sql3 = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME as OPERATE_NAME" +
                         "   FROM xs_zc_task k LEFT JOIN XS_ZC_TASK_EXEC x ON k.ID = x.XS_ZC_TASK_ID" +
                         "    LEFT JOIN XS_ZC_TASK_EXEC_DETAIL l ON x.ID = l.XS_ZC_TASK_EXEC_ID RIGHT JOIN PICTURE_TOUR p ON l.ID = p.PROCESS_ID" +
                         "   WHERE p.TASK_ID = "+taskId+" AND FILE_TYPE = 1 AND p.PROCESS_ID =3";*/
              /*   List<Map<String, Object>> mapsa = this.execSql(sql1 ,null);
                 List<Map<String, Object>> mapsb = this.execSql(sql2 ,null);
                 List<Map<String, Object>> mapsc = this.execSql(sql3 ,null);
                 group.add(mapsa);
                 group.add(mapsb);
                 group.add(mapsc);*/

             }
             if("2".equals(taskType)){//看护
                //看护图片的返回
                 String sql = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME as OPERATE_NAME" +
                         "  FROM PICTURE_KH p" +
                         "    WHERE p.TASK_ID = '"+taskId+"' AND FILE_TYPE = 1  "+s+"  AND p.PROCESS_ID NOT IN (1,2,3)" +
                         "     ORDER BY p.CREATE_TIME DESC";

                 List<Map<String, Object>> maps1 = this.execSql(sql, list.toArray());
                 HashMap<String, Object> stringObjectHashMap = new HashMap<>();
                 LOGGER.info("任务图片查询成功");
                 return WebApiResponse.success(maps1);

             }


         }
     }catch (Exception e){
         LOGGER.info("任务图片查询失败"+e.getMessage());
         return WebApiResponse.erro("任务图片查询失败"+e.getMessage());
     }
        return  WebApiResponse.erro("参数错误");
    }
    /**
     * 根据问题id 和任务类型  查看当前问题对用的照片
     * @param ids
     * @param taskType
     * @return
     */
    public WebApiResponse getPhotos(String taskId,String ids,String taskType) {
        if((null == taskType || "".equals(taskType)) ||  (null == taskId || "".equals(taskId)) || (null == ids || "".equals(ids))){
            return WebApiResponse.erro("参数错误");
        }



        String[] split = null;
        ArrayList<Object> list = new ArrayList<>();

        HashSet<String> groupId = new HashSet<>();//存储分组开始杆塔id
         try {


            if(null != taskType && !"".equals(taskType)){


                if("1".equals(taskType)){//巡视

                    if(null != ids && !"".equals(ids)){

                        //分组   AND END_TOWER_ID = 0
                        String[] split1 = ids.split(",");
                        for (String s : split1) {
                            ArrayList<String> group = new ArrayList<>();
                            if(null!= taskId && !"".equals(taskId)){
                                group.add(taskId);
                            }
                            group.add(s);
                            if(null != s && !"".equals(s)){
                                String groupSql  = "SELECT  START_TOWER_ID" +
                                        "  FROM xs_zc_task k LEFT JOIN XS_ZC_TASK_EXEC x ON k.ID = x.XS_ZC_TASK_ID" +
                                        "   LEFT JOIN XS_ZC_TASK_EXEC_DETAIL l ON x.ID = l.XS_ZC_TASK_EXEC_ID RIGHT JOIN PICTURE_TOUR p ON l.ID = p.PROCESS_ID" +
                                        "    WHERE p.TASK_ID = ?1  AND p.ID = ?2 AND FILE_TYPE = 1  " ;
                                List<Map<String, Object>> maps1 = this.execSql(groupSql, group.toArray());
                               if(maps1.size()>0 && null != maps1.get(0)){
                                       groupId.add(maps1.get(0).get("START_TOWER_ID").toString());

                               }
                            }

                        }

                    if(!groupId.isEmpty()){

                        ArrayList<String> strings = new ArrayList<>(groupId);
                        String[] split2 = ids.split(",");
                        for (String string : strings) {
                            ArrayList<Object> objects = new ArrayList<>();
                            for (String s : split2) {


                               if(null != taskId && !"".equals(taskId)){
                                   ArrayList<String> strings1 = new ArrayList<>();
                                   strings1.add(taskId);
                                   strings1.add(string);
                                   strings1.add(s);

                                   String sql = "SELECT p.ID,FILE_PATH,FILE_SMALL_PATH,k.TASK_NAME,p.CREATE_TIME,l.REASON,cy.LINE_ID,line.LINE_NAME as PROCESS_NAME,line.SECTION,l.OPERATE_NAME  " +
                                           "  FROM xs_zc_task k LEFT JOIN XS_ZC_TASK_EXEC x ON k.ID = x.XS_ZC_TASK_ID" +
                                           "    LEFT JOIN XS_ZC_TASK_EXEC_DETAIL l ON x.ID = l.XS_ZC_TASK_EXEC_ID RIGHT JOIN PICTURE_TOUR p ON l.ID = p.PROCESS_ID" +
                                           "      LEFT JOIN XS_ZC_CYCLE cy on cy.ID = k.XS_ZC_CYCLE_ID LEFT JOIN CM_LINE line ON line.ID = cy.LINE_ID" +
                                           "        WHERE p.TASK_ID = ?1   AND START_TOWER_ID = ?2  AND p.ID = ?3  AND P.FILE_TYPE = 1 ORDER BY  p.CREATE_TIME DESC ";
                                   List<Map<String, Object>> maps = this.execSql(sql,strings1.toArray());
                                   if(null != maps && maps.size()>0){
                                       objects.add(maps.get(0));
                                   }

                               }
                           }
                           if(null !=objects && objects.size()>0){
                               list.add(objects);
                           }

                        }
                    }
                    }
                }
                if("2".equals(taskType)){//看护
                    ArrayList<String> taskArr = new ArrayList<>();
                    ArrayList<Object> task = new ArrayList<>();
                     split = ids.split(",");
                    if(null != taskId && !"".equals(taskId)){
                        taskArr.add(taskId);


                            ArrayList<Object> objects = new ArrayList<>();
                            for (String s : split) {

                        if(null!=s && !"".equals(split)){
                            ArrayList<String> strings = new ArrayList<>();
                            strings.add(taskId);
                            strings.add(s);
                            String sql = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,y.LINE_NAME as PROCESS_NAME,k.TASK_NAME as OPERATE_NAME,k.TASK_NAME,y.LINE_ID " +
                                    "   FROM KH_TASK k LEFT JOIN PICTURE_KH p ON k.ID = p.TASK_ID LEFT JOIN  KH_YH_HISTORY y ON k.ID = y.TASK_ID" +
                                    "   WHERE k.ID = ?1 AND FILE_TYPE = 1  AND p.ID = ?2 " +
                                    "   ORDER BY p.CREATE_TIME DESC ";
                            List<Map<String, Object>> maps1 = this.execSql(sql, strings.toArray());
                            if(null != maps1 && maps1.size()>0){
                                objects.add(maps1.get(0));
                            }

                        }
                    if(null!= objects && objects.size()>0){
                        list.add(objects);
                    }

                        }
                        if(null !=list && list.size()>0){
                            task.add(list.get(0));
                        }

                    return WebApiResponse.success(task);
                }


                }
            }

       }catch (Exception e){
            LOGGER.error("获取问题照片失败"+e.getMessage());
            return WebApiResponse.erro("获取问题照片失败"+e.getMessage());
        }

        return WebApiResponse.success(list);
    }

    /**
     * 根据任务id 和图片id 查询当前问题
     * @param taskId  任务id
     * @param pId  图片id
     * @return
     */
    public WebApiResponse findProByTaskId(String taskId, String pId) {

      try {
          if((null != taskId && !"".equals(taskId)) && (null != pId && !"".equals(pId))){
              ArrayList<String> strings = new ArrayList<>();
              ArrayList<Object> pros = new ArrayList<>();

              strings.add(taskId);
              String sql = "SELECT r.QUESTION_INFO,r.QUESTION_TYPE,r.PHOTO_IDS" +
                      "           FROM CHECK_RESULT r LEFT JOIN CHECK_DETAIL d ON r.CHECK_DETAIL_ID = d.ID" +
                      "                WHERE d.QUESTION_TASK_ID = ?"+strings.size();
              List<Map<String, Object>> maps = this.execSql(sql, strings);
              for (Map<String, Object> map : maps) {
                  String info = map.get("QUESTION_INFO")==null?"":map.get("QUESTION_INFO").toString();
                  String type = map.get("QUESTION_TYPE")==null?"":map.get("QUESTION_TYPE").toString();
                  String ids =  map.get("PHOTO_IDS")==null?"":map.get("PHOTO_IDS").toString();
                  if(null != ids && !"".equals(ids)){
                      String[] split = ids.split(",");
                      for (String s : split) {
                          if(null != s && !"".equals(s)){
                              if(pId.equals(s)){
                                  HashMap<String, String> pro = new HashMap<>();
                                  pro.put("info",info);
                                  pro.put("type",type);
                                  pros.add(pro);
                              }
                          }
                      }
                  }
              }
            return WebApiResponse.success(pros);
          }
      }catch (Exception e){
          LOGGER.error("问题查询失败"+e.getMessage());
          return WebApiResponse.erro("问题查询失败"+e.getMessage());
      }
        return WebApiResponse.success("");
    }
    /**
     * 根据当前流程id  获取当前流程的照片
     * @param id  流程id
     * @return
     */
    public WebApiResponse findProByproId(String id,String taskType,String proId,String dtId) {

        ArrayList<String> list = new ArrayList<>();
        List<Map<String, Object>> maps = null;
        String sql  = "";
        if(null == id || "".equals(id)){
            return WebApiResponse.erro("参数错误");
        }
        if(null == taskType || "".equals(taskType)){
            return WebApiResponse.erro("参数错误");
        }
        list.add(id);
        try {
            //巡视
            if("1".equals(taskType)){
                //巡视流程id
                if(null == dtId || "".equals(dtId)){
                    return WebApiResponse.erro("参数错误");
                }
                 sql  = " SELECT * FROM PICTURE_TOUR p WHERE p.FILE_TYPE = 1 AND p.PROCESS_ID =  "+dtId;
                List<Map<String, Object>> maps1 = this.execSql(sql, null);
                return WebApiResponse.success(maps1);
            }
            if("2".equals(taskType)){//看护

                if(null == proId || "".equals(proId)){
                    return WebApiResponse.erro("参数错误");
                }
                list.add(proId);
                sql = "SELECT *" +
                        "   FROM PICTURE_KH WHERE TASK_ID = ?1 AND FILE_TYPE = 1 and PROCESS_ID = ?2";
            }

            maps = this.execSql(sql, list.toArray());
            LOGGER.info("流程图片查询成功");
        }catch (Exception e){
        LOGGER.error("参数错误"+e.getMessage());
        return WebApiResponse.erro("参数错误"+e.getMessage());
        }

        return WebApiResponse.success(maps);

    }


    public WebApiResponse findPicByTaskId(Long id ,String taskType ){

        ArrayList<Object> list = new ArrayList<>();
        List<Map<String, Object>> maps = null;
        if(null == id || 0 == id){
            return WebApiResponse.erro("参数错误");
        }

        try {
            list.add(id);
            String  sql = "SELECT * " +
                        "      FROM PICTURE_YH  WHERE  YH_ID  =?"+list.size();

             maps = this.execSql(sql, list.toArray());
        }catch (Exception e){
            LOGGER.error("隐患图片查询失败"+e.getMessage());
            return WebApiResponse.erro("隐患图片查询失败"+e.getMessage());
        }
        return WebApiResponse.success(maps);
    }


    public WebApiResponse findPicByPro(String taskId) {
        if(null == taskId || "".equals(taskId)){
            return WebApiResponse.erro("参数错误，taskid="+taskId);
        }
       try {
           ArrayList<String> strings = new ArrayList<>();
           strings.add(taskId);
           String sql = "SELECT r.PHOTO_IDS" +
                   "       FROM CHECK_DETAIL d LEFT JOIN  CHECK_RESULT r ON d.ID = r.CHECK_DETAIL_ID" +
                   "    WHERE d.QUESTION_TASK_ID = ?"+strings.size();
           List<Map<String, Object>> maps = this.execSql(sql, strings);
           String ids = "";
           for (Map<String, Object> map : maps) {
               ids += map.get("PHOTO_IDS") == null ? "": ","+map.get("PHOTO_IDS");
           }
           if(ids.length()>0){
               HashSet<String> set = new HashSet<>();
               String[] split = ids.split(",");
               for (String s : split) {
                   if(null != s && !"".equals(s)){
                       set.add(s);
                   }
               }

               return WebApiResponse.success(set);
           }
       }catch (Exception e){
            LOGGER.error("问题图片回显失败"+e.getMessage());
            return WebApiResponse.erro("问题图片回显失败"+e.getMessage());
       }

        return WebApiResponse.success("");
    }

}
