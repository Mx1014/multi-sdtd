package com.rzt.service.app;

import com.rzt.repository.WarningOneKeyRepository;
import com.rzt.entity.WarningOneKey;
import com.rzt.util.WebApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import com.rzt.service.CurdService;

@Service
public class WarningOneKeyService extends CurdService<WarningOneKey, WarningOneKeyRepository> {

    @Transactional
    public WebApiResponse saveWarning(WarningOneKey warn, String ids) {
        try {
            if (ids != null && !ids.equals("")) {
                String[] split = ids.split(",");
                for (int i = 0; i <= split.length; i++) {
                    this.reposiotry.updateWaring(warn.getId(),Long.parseLong(split[i]));
                }
            }
            warn.setId();
            this.add(warn);
            return WebApiResponse.success("数据保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("error" + e.getMessage());
        }
    }
}