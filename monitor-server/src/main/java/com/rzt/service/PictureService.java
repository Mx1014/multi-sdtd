package com.rzt.service;

import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class PictureService extends CurdService<CheckResult, CheckResultRepository>  {

    protected static Logger LOGGER = LoggerFactory.getLogger(PictureService.class);

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
    public WebApiResponse getPictureAndLines(String taskId, String taskType) {
        String groupSql ="";
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> grouupList = new ArrayList<>();
        ArrayList<List> group = new ArrayList<>();
        List<Map<String, Object>> maps = null;
     try {
         if(null != taskId && !"".equals(taskId)) {
             list.add(taskId);

         }

         if(null != taskType && !"".equals(taskType)){
             if("1".equals(taskType)){//巡视   AND END_TOWER_ID = 0
                 list.add(taskId);
                 if(null != taskId && !"".equals(taskId)){
                     grouupList.add(taskId);
                 }

                 groupSql = "SELECT START_TOWER_ID" +
                         "   FROM xs_zc_task k LEFT JOIN XS_ZC_TASK_EXEC x ON k.ID = x.XS_ZC_TASK_ID" +
                         "    LEFT JOIN XS_ZC_TASK_EXEC_DETAIL l ON x.ID = l.XS_ZC_TASK_EXEC_ID RIGHT JOIN PICTURE_TOUR p ON l.ID = p.PROCESS_ID" +
                         "   WHERE p.TASK_ID = ?"+grouupList.size()+" AND FILE_TYPE = 1 AND START_TOWER_ID IS NOT NULL   GROUP BY START_TOWER_ID";
                 List<Map<String, Object>> maps1  = this.execSql(groupSql, grouupList.toArray());


                 for (Map<String, Object> stringObjectMap : maps1) {
                     Object start_tower_id = stringObjectMap.get("START_TOWER_ID");
                     if(null != start_tower_id){
                         ArrayList<String> strings = new ArrayList<>();
                         Object[] objects = list.toArray();
                         objects[1] = start_tower_id.toString();
                         //  当有垃圾数据时  可能不显示杆塔信息  因为任务连接不上每轮任务详情     任务只有主任务 没有每一轮的任务
                         String sql = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME,l.OPERATE_NAME,l.START_TOWER_ID" +
                                 "   FROM xs_zc_task k LEFT JOIN XS_ZC_TASK_EXEC x ON k.ID = x.XS_ZC_TASK_ID" +
                                 "    LEFT JOIN XS_ZC_TASK_EXEC_DETAIL l ON x.ID = l.XS_ZC_TASK_EXEC_ID RIGHT JOIN PICTURE_TOUR p ON l.ID = p.PROCESS_ID"
                                 +   "   WHERE p.TASK_ID = ?1  AND P.FILE_TYPE = 1 AND START_TOWER_ID = ?2 ORDER BY  p.CREATE_TIME DESC ";
                         List<Map<String, Object>> maps2 = this.execSql(sql ,objects);
                         //所有的巡视图片组   AND END_TOWER_ID = 0
                         group.add(maps2);
                     }
                 }
                 LOGGER.info("任务图片查询成功");
                 return  WebApiResponse.success(group);
             }
             if("2".equals(taskType)){//看护

                 String sql = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME,y.LINE_NAME as OPERATE_NAME,k.TASK_NAME " +
                         " FROM KH_TASK k LEFT JOIN PICTURE_KH p ON k.ID = p.TASK_ID LEFT JOIN  KH_YH_HISTORY y ON  k.ID = y.TASK_ID" +
                         "  WHERE k.ID = ?"+list.size()+" AND FILE_TYPE = 1" +
                         "   ORDER BY p.CREATE_TIME DESC";

                 List<Map<String, Object>> maps1 = this.execSql(sql, list.toArray());
                 ArrayList<Object> objects = new ArrayList<>();
                 objects.add(maps1);
                 LOGGER.info("任务图片查询成功");
                 return WebApiResponse.success(objects);

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
}
