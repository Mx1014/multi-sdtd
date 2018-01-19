package com.rzt.activiti.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rzt.util.WebApiResponse;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public interface ProService {

	List<ProcessDefinition> checkDeploy();

	List<ProcessInstance> checkStatus();

	WebApiResponse checkTask(String userName,Integer page,Integer size);

	void deploy();

	void delpd(String deploymentId);

	ProcessInstance start(String key, Map map);

	Object checkTask(String taskId, String variaName);

	void complete(String taskId, Map<String, Object> map);

	InputStream checkInputStream(String id);

	String findIdByProId(String proId);
}
