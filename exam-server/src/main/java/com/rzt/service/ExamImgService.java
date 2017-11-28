/**    
 * 文件名：ExamImgService           
 * 版本信息：    
 * 日期：2017/11/09 09:38:12    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.entity.ExamImg;
import com.rzt.repository.ExamImgRepository;
import com.rzt.service.CurdService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**      
 * 类名称：ExamImgService    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/09 09:38:12 
 * 修改人：张虎成    
 * 修改时间：2017/11/09 09:38:12    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class ExamImgService extends CurdService<ExamImg,ExamImgRepository> {


}