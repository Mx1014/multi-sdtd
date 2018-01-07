package com.rzt.service;

import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 李成阳
 * 2018/1/7
 */
@Service
public class PictureService extends CurdService<CheckResult, CheckResultRepository>  {

    protected static Logger LOGGER = LoggerFactory.getLogger(PictureService.class);

    /***
     * 获取详情中默认展示的4条照片
     * @param taskId
     * @param taskType
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
            if("1".equals(taskType)){//巡视
                sql = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME,l.OPERATE_NAME,l.START_TOWER_ID" +
                        "   FROM xs_zc_task k LEFT JOIN XS_ZC_TASK_EXEC x ON k.ID = x.XS_ZC_TASK_ID" +
                        "    LEFT JOIN XS_ZC_TASK_EXEC_DETAIL l ON x.ID = l.XS_ZC_TASK_EXEC_ID RIGHT JOIN PICTURE_TOUR p ON l.ID = p.PROCESS_ID" ;
                sql += "   WHERE p.TASK_ID = ?"+list.size()+"  AND P.FILE_TYPE = 1  AND END_TOWER_ID = 0 ORDER BY  p.CREATE_TIME DESC ";
            }
            if("2".equals(taskType)){//看护
                sql = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME,y.LINE_NAME as OPERATE_NAME" +
                        " FROM KH_TASK k LEFT JOIN PICTURE_KH p ON k.ID = p.TASK_ID LEFT JOIN  KH_YH_HISTORY y ON k.YH_ID = y.ID";
                sql += "    WHERE k.ID =  ?"+list.size()+" AND FILE_TYPE = 1 ORDER BY p.CREATE_TIME DESC";
            }

            try {
                maps  = this.execSqlPage(new PageRequest(0, 4), sql, list.toArray());

            }catch (Exception e){
                LOGGER.error("查询杆塔图片失败"+e.getMessage());
                return WebApiResponse.erro("查询杆塔图片失败"+e.getMessage());
            }

        }
        return WebApiResponse.success(maps);
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
             if("1".equals(taskType)){//巡视
                 list.add(taskId);
                 if(null != taskId && !"".equals(taskId)){
                     grouupList.add(taskId);
                 }

                 groupSql = "SELECT START_TOWER_ID" +
                         "   FROM xs_zc_task k LEFT JOIN XS_ZC_TASK_EXEC x ON k.ID = x.XS_ZC_TASK_ID" +
                         "    LEFT JOIN XS_ZC_TASK_EXEC_DETAIL l ON x.ID = l.XS_ZC_TASK_EXEC_ID RIGHT JOIN PICTURE_TOUR p ON l.ID = p.PROCESS_ID" +
                         "   WHERE p.TASK_ID = ?"+grouupList.size()+" AND FILE_TYPE = 1 AND END_TOWER_ID = 0 GROUP BY START_TOWER_ID";
                 List<Map<String, Object>> maps1  = this.execSql(groupSql, grouupList.toArray());


                 for (Map<String, Object> stringObjectMap : maps1) {
                     Object start_tower_id = stringObjectMap.get("START_TOWER_ID");
                     System.out.println("start_tower_id"+start_tower_id);
                     if(null != start_tower_id){
                         ArrayList<String> strings = new ArrayList<>();
                         Object[] objects = list.toArray();
                         objects[1] = start_tower_id.toString();
                         System.out.println(list.size());
                         System.out.println(objects);
                         String sql = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME,l.OPERATE_NAME,l.START_TOWER_ID" +
                                 "   FROM xs_zc_task k LEFT JOIN XS_ZC_TASK_EXEC x ON k.ID = x.XS_ZC_TASK_ID" +
                                 "    LEFT JOIN XS_ZC_TASK_EXEC_DETAIL l ON x.ID = l.XS_ZC_TASK_EXEC_ID RIGHT JOIN PICTURE_TOUR p ON l.ID = p.PROCESS_ID"
                                 +   "   WHERE p.TASK_ID = ?1  AND P.FILE_TYPE = 1 AND END_TOWER_ID = 0  AND START_TOWER_ID = ?2 ORDER BY  p.CREATE_TIME DESC ";
                         System.out.println(sql);
                         List<Map<String, Object>> maps2 = this.execSql(sql ,objects);
                         System.out.println(maps2);
                         //所有的巡视图片组
                         group.add(maps2);


                     }
                 }
                 LOGGER.info("任务图片查询成功");
                 return  WebApiResponse.success(group);

             }
             if("2".equals(taskType)){//看护

                 String sql = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME,y.LINE_NAME as OPERATE_NAME,k.TASK_NAME" +
                         "FROM KH_TASK k LEFT JOIN PICTURE_KH p ON k.ID = p.TASK_ID LEFT JOIN  KH_YH_HISTORY y ON k.YH_ID = y.ID" +
                         " WHERE k.ID = ?"+list.size()+" AND FILE_TYPE = 1" +
                         " ORDER BY p.CREATE_TIME DESC;";
                 LOGGER.info("任务图片查询成功");
                 return WebApiResponse.success(this.execSql(sql, list.toArray()));

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
    public WebApiResponse getPhotos(String ids,String taskType) {
        String[] split = null;
        ArrayList<Object> list = new ArrayList<>();
        try {
            if(null != taskType && !"".equals(taskType)){
                if(null != ids && !"".equals(ids)){
                    split = ids.split(",");

                }
                if("1".equals(taskType)){//巡视
                    for (String s : split) {
                        if(null!=s && !"".equals(split)){
                            ArrayList<String> strings = new ArrayList<>();
                            strings.add(s);
                            String sql = "SELECT * FROM PICTURE_TOUR WHERE ID = ?"+strings.size();
                            List<Map<String, Object>> maps = this.execSql(sql, strings);
                            list.add(maps);
                        }
                    }
                }
                if("2".equals(taskType)){//看护
                    for (String s : split) {
                        if(null!=s && !"".equals(split)){
                            ArrayList<String> strings = new ArrayList<>();
                            strings.add(s);
                            String sql = "SELECT * FROM PICTURE_KH WHERE ID = ?"+strings.size();
                            List<Map<String, Object>> maps = this.execSql(sql, strings);
                            list.add(maps);
                        }
                    }
                }

            }
        }catch (Exception e){
            LOGGER.error("获取问题照片失败"+e.getMessage());
            return WebApiResponse.erro("获取问题照片失败"+e.getMessage());
        }

        return WebApiResponse.success(list);
    }
}
