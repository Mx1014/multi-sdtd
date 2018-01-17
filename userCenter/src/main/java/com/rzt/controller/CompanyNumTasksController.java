package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("CompanyNumTasks")
public class CompanyNumTasksController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    RedisTemplate<String, Object> template;

    @GetMapping("CompanyNumTask")
    public WebApiResponse CompanyNumTask(String userId) {
        HashOperations<String, Object, Object> hashOperations = template.opsForHash();
        JSONObject jsonObject = JSONObject.parseObject(String.valueOf(hashOperations.get("UserInformation", userId)));
        String deptid = jsonObject.get("DEPTID").toString();
        Integer type = Integer.parseInt(jsonObject.get("TYPE").toString());
        if (type == 0) {
            return CompanyNumTaskYj();
        } else if (type == 1 || type == 2) {
            return CompanyAllTasks(deptid);
        }
        return WebApiResponse.erro("NULL");
    }

    public WebApiResponse CompanyNumTaskYj() {
        List<Map<String, Object>> deptnameList = null;
        try {
            Map<String, Integer> map1 = new HashMap();
            Map<String, Integer> map2 = new HashMap();
            Map<String, Integer> map3 = new HashMap();
            String zcXsZs = " SELECT count(1) AS num,TD_ORG FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) GROUP BY TD_ORG ";
            List<Map<String, Object>> zcXsZsList = this.service.execSql(zcXsZs);
            String bdXsZs = " SELECT count(1) AS num,TD_ORG FROM XS_TXBD_TASK WHERE trunc(PLAN_START_TIME) = trunc(SYSDATE) GROUP BY TD_ORG ";
            List<Map<String, Object>> bdXsZsList = this.service.execSql(bdXsZs);
            String khZs = " SELECT nvl(a.num,0) as num,d.ID as TD_ORG FROM (SELECT COUNT(1) as num,TDYW_ORG FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) GROUP BY TDYW_ORG) a RIGHT JOIN RZTSYSDEPARTMENT d ON a.TDYW_ORG = d.DEPTNAME WHERE DEPTSORT IS NOT NULL ";
            List<Map<String, Object>> khZsList = this.service.execSql(khZs);
            String deptname = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL ORDER BY t.DEPTSORT ";
            deptnameList = this.service.execSql(deptname);
            for (int i = 0; i < zcXsZsList.size(); i++) {
                map1.put(zcXsZsList.get(i).get("TD_ORG").toString(), Integer.parseInt(zcXsZsList.get(i).get("NUM").toString()));
            }
            for (int i = 0; i < bdXsZsList.size(); i++) {
                map2.put(bdXsZsList.get(i).get("TD_ORG").toString(), Integer.parseInt(bdXsZsList.get(i).get("NUM").toString()));
            }
            for (int i = 0; i < khZsList.size(); i++) {
                map3.put(khZsList.get(i).get("TD_ORG").toString(), Integer.parseInt(khZsList.get(i).get("NUM").toString()));
            }
            for (Map map : deptnameList) {
                map.put("XSZS", 0);
                map.put("KHZS", 0);
                Integer zcXsS = 0;
                Integer zcxs = map1.get(map.get("ID"));
                if (!StringUtils.isEmpty(zcxs)) {
                    zcXsS = zcxs;
                }
                Integer bdXsS = 0;
                Integer bdxs = map2.get(map.get("ID"));
                if (!StringUtils.isEmpty(bdxs)) {
                    bdXsS = bdxs;
                }
                Integer khzsL = 0;
                Integer khzx = map3.get(map.get("ID"));
                if (!StringUtils.isEmpty(khzx)) {
                    khzsL = khzx;
                }
                map.put("XSZS", zcXsS + bdXsS);
                Integer khzs = khzsL;
                map.put("KHZS", khzs);
            }
            return WebApiResponse.success(deptnameList);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    public WebApiResponse CompanyAllTasks(String deptid) {
        if (StringUtils.isEmpty(deptid)) {
            return WebApiResponse.erro("Null");
        }
        try {
            Map<String, Integer> map1 = new HashMap();
            Map<String, Integer> map2 = new HashMap();
            Map<String, Integer> map3 = new HashMap();
            String className = " SELECT ID,DEPTNAME FROM (SELECT ID,DEPTNAME,LASTNODE FROM RZTSYSDEPARTMENT START WITH ID = ?1 CONNECT BY PRIOR ID = DEPTPID) WHERE LASTNODE = 0 ";
            List<Map<String, Object>> classNameList = this.service.execSql(className, deptid);
            String calssZcXsZs = " SELECT count(1) AS num,CLASS_ID FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND TD_ORG = ?1 GROUP BY CLASS_ID ";
            List<Map<String, Object>> calssZcXsZsList = this.service.execSql(calssZcXsZs, deptid);
            String calssBdXsZs = " SELECT count(1) AS num,CLASS_ID FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND TD_ORG = ?1 GROUP BY CLASS_ID ";
            List<Map<String, Object>> calssBdXsZsList = this.service.execSql(calssBdXsZs, deptid);
            String calssKhZs = " SELECT count(1) as num,CLASSNAME FROM (SELECT u.CLASSNAME,u.DEPTID FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID WHERE trunc(PLAN_START_TIME) = trunc(sysdate)) WHERE DEPTID = ?1 GROUP BY CLASSNAME ";
            List<Map<String, Object>> calssKhZsList = this.service.execSql(calssKhZs, deptid);
            for (int i = 0; i < calssZcXsZsList.size(); i++) {
                map1.put(calssZcXsZsList.get(i).get("CLASS_ID").toString(), Integer.parseInt(calssZcXsZsList.get(i).get("NUM").toString()));
            }
            for (int i = 0; i < calssBdXsZsList.size(); i++) {
                map2.put(calssBdXsZsList.get(i).get("CLASS_ID").toString(), Integer.parseInt(calssBdXsZsList.get(i).get("NUM").toString()));
            }
            for (int i = 0; i < calssKhZsList.size(); i++) {
                map3.put(calssKhZsList.get(i).get("CLASSNAME").toString(), Integer.parseInt(calssKhZsList.get(i).get("NUM").toString()));
            }
            for (Map map : classNameList) {
                map.put("XSZS", 0);
                map.put("KHZS", 0);
                Integer zcXsS = 0;
                Integer zcxs = map1.get(map.get("ID"));
                if (!StringUtils.isEmpty(zcxs)) {
                    zcXsS = zcxs;
                }
                Integer bdXsS = 0;
                Integer bdxs = map2.get(map.get("ID"));
                if (!StringUtils.isEmpty(bdxs)) {
                    bdXsS = bdxs;
                }
                Integer KhZs = 0;
                Integer KhZss = map3.get(map.get("ID"));
                if (!StringUtils.isEmpty(KhZss)) {
                    KhZs = KhZss;
                }
                map.put("XSZS", zcXsS + bdXsS);
                Integer khzs = KhZs;
                map.put("KHZS", khzs);
            }
            return WebApiResponse.success(classNameList);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    @GetMapping("deptZhu")
    public WebApiResponse deptZhu(String id) {
        try {
            Map map = new HashMap();
            //正常
            String xsZcWks = " SELECT count(1) AS num FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 0 AND TD_ORG = ?1 ";
            Map<String, Object> xsZcWksList = this.service.execSqlSingleResult(xsZcWks, id);
            String xsZcJxz = " SELECT count(1) AS num FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 1 AND TD_ORG = ?1 ";
            Map<String, Object> xsZcJxzList = this.service.execSqlSingleResult(xsZcJxz, id);
            String xsZcYwc = " SELECT count(1) AS num FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 2 AND TD_ORG = ?1 ";
            Map<String, Object> xsZcYwcList = this.service.execSqlSingleResult(xsZcYwc, id);
            //保电
            String bdZcWks = " SELECT count(1) AS num FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 0 AND TD_ORG = ?1 ";
            Map<String, Object> bdZcWksList = this.service.execSqlSingleResult(bdZcWks, id);
            String bdZcJxz = " SELECT count(1) AS num FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 1 AND TD_ORG = ?1 ";
            Map<String, Object> bdZcJxzList = this.service.execSqlSingleResult(bdZcJxz, id);
            String bdZcYwc = " SELECT count(1) AS num FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 2 AND TD_ORG = ?1 ";
            Map<String, Object> bdZcYwcList = this.service.execSqlSingleResult(bdZcYwc, id);
            //看护
            String khWks = " SELECT count(1) as num FROM (SELECT u.CLASSNAME,u.DEPTID FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 0) WHERE DEPTID = ?1 ";
            Map<String, Object> khWksList = this.service.execSqlSingleResult(khWks, id);
            String khJxz = " SELECT count(1) as num FROM (SELECT u.CLASSNAME,u.DEPTID FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 1) WHERE DEPTID = ?1 ";
            Map<String, Object> khJxzList = this.service.execSqlSingleResult(khJxz, id);
            String khYwc = " SELECT count(1) as num FROM (SELECT u.CLASSNAME,u.DEPTID FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 2) WHERE DEPTID = ?1 ";
            Map<String, Object> khYwcList = this.service.execSqlSingleResult(khYwc, id);
            map.put("XSWKS", Integer.parseInt(xsZcWksList.get("NUM").toString()) + Integer.parseInt(bdZcWksList.get("NUM").toString()));
            map.put("XSJXZ", Integer.parseInt(xsZcJxzList.get("NUM").toString()) + Integer.parseInt(bdZcJxzList.get("NUM").toString()));
            map.put("XSYWC", Integer.parseInt(xsZcYwcList.get("NUM").toString()) + Integer.parseInt(bdZcYwcList.get("NUM").toString()));
            map.put("KHWKS", khWksList.get("NUM"));
            map.put("KHJXZ", khJxzList.get("NUM"));
            map.put("KHYWC", khYwcList.get("NUM"));
            return WebApiResponse.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }
}
