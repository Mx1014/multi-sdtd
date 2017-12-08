package com.rzt.service.app;

import com.rzt.entity.appentity.xsZcTaskwpqr;
import com.rzt.repository.app.xsZcTaskwpqrRepository;
import com.rzt.service.CurdService;
import com.rzt.util.UUIDTool;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.service.app
 * @Author: liuze
 * @date: 2017-12-7 19:43
 */
@Service
public class xsZcTaskwpqrService extends CurdService<xsZcTaskwpqr, xsZcTaskwpqrRepository> {
    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视
     * 实际开始时间 ,巡视开始时间 ,身份确认时间 更改时间
     *
     * @param id
     * @param xslx
     * @return
     */
    public int updateSfqrTime(String id, int xslx) {
        int one = 1;
        int two = 2;
        if (xslx == one) {
            return this.reposiotry.zxXsSfqrTime(id);
        } else if (two == two) {
            return this.reposiotry.bdXsSfqrTime(id);
        }
        return 0;
    }

    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视 id 任务ID
     * 到达现场更改时间
     *
     * @param xslx
     * @param id
     * @return
     */
    public int reachSpot(int xslx, String id) {
        int one = 1;
        int two = 2;
        if (xslx == one) {
            return this.reposiotry.zcXsDdxcTime(id);
        } else if (xslx == two) {
            return this.reposiotry.bdXsDdxcTime(id);
        }
        return 0;
    }

    /**
     * 物品提醒 先查询如果有就修改没有就存一条 返回int
     *
     * @param taskID 任务ID
     * @param rwZt   物品存档
     * @param xslx   0 特巡 1 保电 2 巡视
     * @return
     */
    public int articlesReminding(String taskID, String rwZt, int xslx, String id) {
        int zero = 0, one = 1, two = 2;
        if (!StringUtils.isEmpty(id)) {
            if (xslx == zero || xslx == one) {
                return this.reposiotry.bdXsArticlesUpdate(rwZt, id);
            } else if (xslx == two) {
                return this.reposiotry.zcXsArticlesUpdate(rwZt, id);
            }
        } else {
            if (xslx == zero || xslx == one) {
                return this.reposiotry.bdXsArticlesInsert(UUIDTool.getUUID(), taskID, rwZt);
            } else if (xslx == two) {
                return this.reposiotry.zcXsArticlesInsert(UUIDTool.getUUID(), taskID, rwZt);
            }
        }
        return zero;
    }
}