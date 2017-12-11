package com.rzt.service.app;

import com.rzt.entity.app.XsZcTaskwpqr;
import com.rzt.repository.app.XsZcTaskwpqrRepository;
import com.rzt.service.CurdService;
import com.rzt.util.SnowflakeIdWorker;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.service.app
 * @Author: liuze
 * @date: 2017-12-7 19:43
 */
@Service
public class XsZcTaskwpqrService extends CurdService<XsZcTaskwpqr, XsZcTaskwpqrRepository> {
    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视
     * 实际开始时间 ,巡视开始时间 ,身份确认时间 更改时间
     *
     * @param id
     * @param xslx
     * @return
     */
    public int updateSfqrTime(Long id, int xslx) {
        int one = 1, two = 2, zero = 0;
        if (xslx == one || xslx == zero) {
            return this.reposiotry.bdXsSfqrTime(id);
        } else if (xslx == two) {
            return this.reposiotry.zxXsSfqrTime(id);
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
    public int reachSpot(int xslx, Long id) {
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
    public int articlesReminding(Long taskID, String rwZt, int xslx) {
        int zero = 0, one = 1, two = 2;
        if (!StringUtils.isEmpty(taskID)) {
            if (xslx == zero || xslx == one) {
                return this.reposiotry.bdXsArticlesUpdate(rwZt, taskID);
            } else if (xslx == two) {
                return this.reposiotry.zcXsArticlesUpdate(rwZt, taskID);
            }
        } else {
            if (xslx == zero || xslx == one) {
                return this.reposiotry.bdXsArticlesInsert(new SnowflakeIdWorker(12, 20).nextId(), taskID, rwZt);
            } else if (xslx == two) {
                return this.reposiotry.zcXsArticlesInsert(new SnowflakeIdWorker(13, 20).nextId(), taskID, rwZt);
            }
        }
        return zero;
    }
}