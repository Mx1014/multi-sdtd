package com.rzt.activiti.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rzt.util.WebApiResponse;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

/**
 * 工作流 操作方法基类
 */
public interface ActivitiService {

	List<ProcessDefinition> checkDeploy();

	List<ProcessInstance> checkStatus();

	/**
	 *
	 * @param userName    用户  判断节点使用
	 * @param page
	 * @param size
	 * @param values      可能多的参数
	 * @return
	 */
	WebApiResponse checkTask(String userName,Integer page,Integer size,Object... values);

	void deploy();

	void delpd(String deploymentId);

	ProcessInstance start(String key, Map map);

	Object checkTask(String taskId, String variaName);

	void complete(String taskId, Map<String, Object> map);

	InputStream checkInputStream(String id);

}
