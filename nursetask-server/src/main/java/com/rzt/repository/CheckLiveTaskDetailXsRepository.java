/**
 * 文件名：CHECKLIVETASKDETAILXSRepository
 * 版本信息：
 * 日期：2017/12/25 13:26:27
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository;
import com.rzt.entity.CheckLiveTaskDetailXs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * 类名称：CHECKLIVETASKDETAILXSRepository
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/25 13:26:27
 * 修改人：张虎成
 * 修改时间：2017/12/25 13:26:27
 * 修改备注：
 * @version
 */
@Repository
public interface CheckLiveTaskDetailXsRepository extends JpaRepository<CheckLiveTaskDetailXs,String> {
    CheckLiveTaskDetailXs findById(Long id);
}
