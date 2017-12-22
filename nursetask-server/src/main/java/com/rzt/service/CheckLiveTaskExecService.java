package com.rzt.service;

import com.rzt.entity.CheckLiveTaskExec;
import com.rzt.repository.CheckLiveTaskExecRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CheckLiveTaskExecService extends CurdService<CheckLiveTaskExec, CheckLiveTaskExecRepository> {

    public long getCount(String userId) {
        return this.reposiotry.getCount(userId);
    }

    public CheckLiveTaskExec findExec(String execId) {
        long id = Long.parseLong(execId);

        return this.reposiotry.findExec(id);

    }
}
