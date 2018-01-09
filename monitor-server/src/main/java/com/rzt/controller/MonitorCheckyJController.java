/**
 * 文件名：MONITORCHECKYJController
 * 版本信息：
 * 日期：2018/01/08 11:06:23
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;
import com.rzt.entity.Monitorcheckyj;
import com.rzt.service.Monitorcheckyjservice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 类名称：MONITORCHECKYJController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2018/01/08 11:06:23
 * 修改人：张虎成
 * 修改时间：2018/01/08 11:06:23
 * 修改备注：
 * @version
 */
@RestController
@RequestMapping("MONITORCHECKYJ")
public class MonitorCheckyJController extends
		CurdController<Monitorcheckyj,Monitorcheckyjservice> {



}