package com.rzt.service.app;

import com.rzt.eureka.CensusServer;
import com.rzt.repository.WarningOneKeyRepository;
import com.rzt.entity.WarningOneKey;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import com.rzt.service.CurdService;

import java.util.Date;

@Service
public class WarningOneKeyService extends CurdService<WarningOneKey, WarningOneKeyRepository> {

    @Autowired
    private CensusServer censusServer;

    public WebApiResponse saveWarning(WarningOneKey warn, String ids) {
        try {
            warn.setId(0L);
            if (ids != null && !ids.equals("")) {
                String[] split = ids.split(",");
                for (int i = 0; i < split.length; i++) {
                    this.reposiotry.updateWaring(warn.getId(), Long.parseLong(split[i]));
                }
            }
            this.reposiotry.insertWarn(warn.getId(), warn.getGjlx(), warn.getGjms(), warn.getLon(), warn.getLat(), warn.getUserId());
            censusServer.warningKey(warn.getId());
            return WebApiResponse.success("数据保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("error" + e.getMessage());
        }
    }
}