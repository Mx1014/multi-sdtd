/**    
 * 文件名：ExamTextRepository           
 * 版本信息：    
 * 日期：2017/11/09 09:38:12    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;

import com.rzt.entity.ExamText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名称：ExamTextRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/09 09:38:12 
 * 修改人：张虎成    
 * 修改时间：2017/11/09 09:38:12    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface ExamTextRepository extends JpaRepository<ExamText,String> {

  @Query(value = "select t.id as text_id,t.text_body,t.text_points,group_concat(o.id) as option_id,group_concat(o.option_name,',|分|割|') as options,GROUP_CONCAT(o.is_right) as anwers " +
         " from exam_papers p,paper_text pt,exam_text t,exam_options o" +
         " where p.id=pt.paper_id and t.id=pt.text_id and t.id=o.text_id  group by t.id",nativeQuery = true)
  List<Object> getPaperTexts();


}
