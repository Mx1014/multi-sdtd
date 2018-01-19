package com.rzt.activiti.service;

import com.rzt.util.WebApiResponse;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 李成阳
 * 2018/1/18
 * 缺陷上报实现类
 */
public interface DefectService {

    List<ProcessDefinition> checkDeploy();

    List<ProcessInstance> checkStatus();

    WebApiResponse checkTask(String userName, Integer page, Integer size);

    void deploy();

    void delpd(String deploymentId);

    ProcessInstance start(String key, Map map);

    Object checkTask(String taskId, String variaName);

    void complete(String taskId, Map<String, Object> map);

    InputStream checkInputStream(String id);
}
