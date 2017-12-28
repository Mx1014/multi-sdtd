/**    
 * 文件名：ExamTextService           
 * 版本信息：    
 * 日期：2017/11/09 09:38:12    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.entity.ExamText;
import com.rzt.repository.ExamTextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**      
 * 类名称：ExamTextService    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/09 09:38:12 
 * 修改人：张虎成    
 * 修改时间：2017/11/09 09:38:12    
 * 修改备注：    
 * @version        
 */
@Service
public class ExamTextService extends CurdService<ExamText,ExamTextRepository> {


    public List<Object> getPaperTexts() {
        return this.reposiotry.getPaperTexts();
    }
}