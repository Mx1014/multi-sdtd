/**
 * 文件名：RztSysOperateService
 * 版本信息：
 * 日期：2017/10/12 10:25:31
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.service.CurdService;
import com.rzt.entity.RztSysOperate;
import com.rzt.repository.RztSysOperateRepository;
import com.rzt.util.WebApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 类名称：RztSysOperateService
 * 类描述：InnoDB free: 537600 kB
 * 创建人：张虎成
 * 创建时间：2017/10/12 10:25:31
 * 修改人：张虎成
 * 修改时间：2017/10/12 10:25:31
 * 修改备注：
 */
@Service
public class RztSysOperateService extends CurdService<RztSysOperate, RztSysOperateRepository> {
    /**
     * 查询操作表 提示前端是否选中
     * String menuid
     *
     * @return
     */
    public List<Map<String, Object>> findAllRztSysOperate(String menuid) {
        String sql = " SELECT * FROM (SELECT OPERATEID " +
                "FROM RZTMENUPRIVILEGE WHERE MENUID=?1) a " +
                "RIGHT JOIN " +
                "(SELECT ID,OPERATENAME,OPERATENUM " +
                "FROM RZTSYSOPERATE WHERE MENUID=?2) b ON a.OPERATEID=b.ID ";
        return this.execSql(sql, menuid, menuid);
    }

    /**
     * 添加菜单列表是否有权限增删改
     *
     * @param menuid    菜单id
     * @param operateid 操作id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public WebApiResponse insertRztmenuprivilege(String menuid, String operateid) {
        String[] split = operateid.split(",");
        reposiotry.deleteRztmenuprivilegeByMenuid(menuid);
        for (int i = 0; i < split.length; i++) {
            try {
                this.reposiotry.insertRztmenuprivilege(menuid, split[i]);
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("数据添加失败");
            }
        }
        return WebApiResponse.success("数据添加成功");
    }

}