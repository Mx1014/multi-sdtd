/**    
 * 文件名：CMLINEService           
 * 版本信息：    
 * 日期：2017/11/28 18:05:13    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.repository.CMLINERepository;
import com.rzt.entity.CMLINE;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**      
 * 类名称：CMLINEService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 18:05:13 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 18:05:13    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class CMLINEService extends CurdService<CMLINE,CMLINERepository> {


}