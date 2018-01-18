package com.rzt.service;

import com.rzt.entity.app.XSZCTASK;
import com.rzt.service.app.XSZCTASKService;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;
/***
* @Class ScheduledTaskService
* @Description 巡视任务中的定时任务
* @date 2018/1/7 12:39
* @author nwz
*/
@Component
public class ScheduledTaskService {
    @Autowired
    private XSZCTASKService xszctaskService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * @方法说明：每天的凌晨12点10分自动生成巡视任务
     *
     * @author: ningweize
     * @date: 2017年5月18日 上午10:49:23
     */
	@Scheduled(cron = "0 10 0 ? * *")
//	@Scheduled(cron="0/50 * *  * * ? ")
// 每50秒执行一次
    @Modifying
    @Transactional
    public void autoInsertTourTask() {
        //1.当天完成的或者当天还没有完成
        String condition1 = "SELECT\n" +
                "  t.XS_ZC_CYCLE_ID,\n" +
                "  t.PLAN_START_TIME plantime,\n" +
                "  t.id              taskid,\n" +
                "  tt.CM_USER_ID,\n" +
                "  tt.TD_ORG,\n" +
                "  tt.WX_ORG,\n" +
                "  tt.CLASS_ID,\n" +
                "  tt.PLAN_START_TIME,\n" +
                "  tt.GROUP_ID,\n" +
                "  tt.PLAN_END_TIME,\n" +
                "  tt.IS_KT,\n" +
                "  tt.PLAN_XS_NUM,\n" +
                "  tt.TASK_NAME,\n" +
                "  tt.TOTAL_TASK_NUM,\n" +
                "  tt.CYCLE\n" +
                "FROM (\n" +
                "       SELECT\n" +
                "         id,\n" +
                "         XS_ZC_CYCLE_ID,\n" +
                "         PLAN_START_TIME,\n" +
                "         PLAN_END_TIME\n" +
                "       FROM XS_ZC_TASK\n" +
                "       WHERE\n" +
                "         reborn = 0 AND\n" +
                "         IS_DELETE = 0) t\n" +
                "  JOIN xs_zc_cycle tt ON t.XS_ZC_CYCLE_ID = tt.id AND tt.is_delete = 0 AND\n" +
                "                         (trunc(t.PLAN_START_TIME + tt.CYCLE) = trunc(sysdate) OR\n" +
                "                          (t.PLAN_END_TIME BETWEEN trunc(sysdate - 1) AND trunc(SYSDATE)))";
        bornTask(condition1);
       /* //2.提前完成
        String condition2 = " SELECT\n" +
                "        t.id taskid,\n" +
                "                t.XS_ZC_CYCLE_ID,\n" +
                "                t.REAL_END_TIME plantime,\n" +
                "                tt.CM_USER_ID,\n" +
                "                tt.TD_ORG,\n" +
                "                tt.WX_ORG,\n" +
                "                tt.CLASS_ID,\n" +
                "                tt.PLAN_START_TIME,\n" +
                "                tt.GROUP_ID,\n" +
                "                tt.PLAN_END_TIME,\n" +
                "                tt.IS_KT,\n" +
                "                tt.PLAN_XS_NUM,\n" +
                "                tt.TASK_NAME,\n" +
                "                tt.TOTAL_TASK_NUM,\n" +
                "                tt.CYCLE\n" +
                "        FROM (\n" +
                "                SELECT id,XS_ZC_CYCLE_ID,REAL_END_TIME,PLAN_START_TIME\n" +
                "                FROM XS_ZC_TASK\n" +
                "                WHERE\n" +
                "                reborn = 0 AND\n" +
                "                IS_DELETE = 0 and (REAL_END_TIME < trunc(PLAN_END_TIME))) t\n" +
                "        JOIN xs_zc_cycle tt ON t.XS_ZC_CYCLE_ID = tt.id";
        bornTask(condition2);*/

    }

    private void bornTask(String condition1) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        List<Map<String, Object>> condition1List = xszctaskService.execSql(condition1);
        Long id = 1l;
        for (Map<String, Object> orginTask:condition1List) {
            try {
                id = Long.parseLong(orginTask.get("XS_ZC_CYCLE_ID").toString());
                XSZCTASK xsZcTask = new XSZCTASK();
                xsZcTask.setId();
                xsZcTask.setTdOrg(orginTask.get("TD_ORG") == null ? null : orginTask.get("TD_ORG").toString());
                xsZcTask.setWxOrg(orginTask.get("WX_ORG") == null ? null : orginTask.get("WX_ORG").toString());
                xsZcTask.setGroupId(orginTask.get("GROUP_ID") == null ? null : orginTask.get("GROUP_ID").toString());
                xsZcTask.setClassId(orginTask.get("CLASS_ID") == null ? null : orginTask.get("CLASS_ID").toString());
                xsZcTask.setXsZcCycleId(id);
                xsZcTask.setStauts(0);
                xsZcTask.setPlanXsNum(Integer.parseInt(orginTask.get("PLAN_XS_NUM").toString()));
                String plan_start_time = orginTask.get("PLAN_START_TIME").toString();
                String plan_end_time = orginTask.get("PLAN_END_TIME").toString();

                int cycle = Integer.parseInt(orginTask.get("CYCLE").toString());
                String s = DateUtil.dayStringByIndex((Date) orginTask.get("PLANTIME"),cycle);
                xsZcTask.setPlanStartTime(DateUtil.stringToDate1(s + " " + plan_start_time));
                Integer isKt = orginTask.get("IS_KT") == null ? null : Integer.parseInt(orginTask.get("IS_KT").toString());
                if (isKt == 0) {
                    String e = DateUtil.dayStringByIndex((Date) orginTask.get("PLANTIME"),cycle + cycle);
                    xsZcTask.setPlanEndTime(DateUtil.stringToDate1(e  + plan_end_time));
                } else {
                    String e = DateUtil.dayStringByIndex((Date) orginTask.get("PLANTIME"),(cycle + cycle-1));
                    xsZcTask.setPlanEndTime(DateUtil.stringToDate1(e + " " + plan_end_time));

                }
                xsZcTask.setWxOrg(orginTask.get("WX_ORG") == null ? null : orginTask.get("WX_ORG").toString());
                xsZcTask.setPdTime(DateUtil.dateNow());
                Integer total_task_num = Integer.parseInt(orginTask.get("TOTAL_TASK_NUM").toString()) + 1;
                xsZcTask.setTaskNumInCycle(total_task_num);
                xsZcTask.setTaskName(orginTask.get("TASK_NAME").toString());
                xsZcTask.setCmUserId(orginTask.get("CM_USER_ID").toString());
                try {
                    xszctaskService.add(xsZcTask);
                } catch (Exception e) {
                    hashOperations.put("faiiTask" + DateUtil.stringNow(), Long.toString(id), "新任务失败");
                    e.printStackTrace();
                    continue;
                }
                try {
                    this.xszctaskService.reposiotry.updateCycleTotalBornNum(id,total_task_num);
                } catch (Exception e) {
                    hashOperations.put("faiiTask" + DateUtil.stringNow().split(" ")[0], Long.toString(id), "周期失败");
                    e.printStackTrace();
                    continue;
                }
                Long taskid = Long.parseLong(orginTask.get("TASKID").toString());
                try {
                    this.xszctaskService.reposiotry.updateTaskReborn(taskid);
                } catch (Exception e) {
                    hashOperations.put("faiiTask" + DateUtil.stringNow().split(" ")[0], Long.toString(id), "旧任务失败");
                    e.printStackTrace();
                    continue;
                }
            } catch (Exception e) {
                hashOperations.put("faiiTask" + DateUtil.stringNow().split(" ")[0], Long.toString(id), "失败");
                e.printStackTrace();
                continue;
            }


        }
    }

    public static void main(String[] args) {
        String sql =  "SELECT\n" +
                "  t.XS_ZC_CYCLE_ID,\n" +
                "  t.PLAN_START_TIME plantime,\n" +
                "  t.id              taskid,\n" +
                "  tt.CM_USER_ID,\n" +
                "  tt.TD_ORG,\n" +
                "  tt.WX_ORG,\n" +
                "  tt.CLASS_ID,\n" +
                "  tt.PLAN_START_TIME,\n" +
                "  tt.GROUP_ID,\n" +
                "  tt.PLAN_END_TIME,\n" +
                "  tt.IS_KT,\n" +
                "  tt.PLAN_XS_NUM,\n" +
                "  tt.TASK_NAME,\n" +
                "  tt.TOTAL_TASK_NUM,\n" +
                "  tt.CYCLE\n" +
                "FROM (\n" +
                "       SELECT\n" +
                "         id,\n" +
                "         XS_ZC_CYCLE_ID,\n" +
                "         PLAN_START_TIME,\n" +
                "         PLAN_END_TIME\n" +
                "       FROM XS_ZC_TASK\n" +
                "       WHERE\n" +
                "         reborn = 0 AND\n" +
                "         IS_DELETE = 0) t\n" +
                "  JOIN xs_zc_cycle tt ON t.XS_ZC_CYCLE_ID = tt.id AND tt.is_delete = 0 AND\n" +
                "                         (trunc(t.PLAN_START_TIME + tt.CYCLE) = trunc(sysdate) OR\n" +
                "                          (t.PLAN_END_TIME BETWEEN trunc(sysdate - 1) AND trunc(SYSDATE)))";
        System.out.println(sql);
    }

//    @Scheduled(cron="0/50 * *  * * ? ")
    public void modifyUserProblem() {
//        String sql = "SELECT * from xs_zc_cycle";
        String sql = "SELECT * from xs_zc_task";
        List<Map<String, Object>> maps = xszctaskService.execSql(sql);
        for (Map<String,Object> map:maps) {
            Object cm_user_id = map.get("CM_USER_ID");
            if(cm_user_id != null) {
                String userSql = "select * from rztsysuser where id = ?";
                Object id = map.get("ID");
                try {
                    Map<String, Object> user = xszctaskService.execSqlSingleResult(userSql, cm_user_id);
//                    xszctaskService.reposiotry.updateCycle(id,user.get("DEPTID"),user.get("COMPANYID"),user.get("GROUPID"),user.get("CLASSNAME"));
                    xszctaskService.reposiotry.updateTask(id,user.get("DEPTID"),user.get("COMPANYID"),user.get("GROUPID"),user.get("CLASSNAME"));
                } catch (Exception e) {
                    System.err.println(id);
                    continue;
                }

            }

        }


    }

}
