/**
 * 文件名：RztsyscompanyRepository
 * 版本信息：
 * 日期：2017/12/08 16:40:23
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.rzt.entity.Rztsyscompany;
import org.springframework.transaction.annotation.Transactional;

/**
 * 类名称：RztsyscompanyRepository
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/08 16:40:23
 * 修改人：张虎成
 * 修改时间：2017/12/08 16:40:23
 * 修改备注：
 */
@Repository
public interface RztsyscompanyRepository extends JpaRepository<Rztsyscompany, String> {
    @Modifying
    @Query(value = "INSERT INTO RZTSYSCOMPANY (ID,COMPANYNAME,CREATETIME,ORGID,ORGNAME) VALUES (?1,?2,sysdate,?3,?4)", nativeQuery = true)
    int addRztsyscompany(String id, String cmpanyname, String orgid, String orgname);

    @Modifying
    @Query(value = "INSERT INTO RZTSYSCOMPANYFILE (ID, COMPANYID, FILENAME, FILETYPE,FILEPATH) VALUES (?1,?2,?3,?4,?5)", nativeQuery = true)
    int addpanyFile(String fid, String id, String filename, String filetype, String filepath);

    @Modifying
    @Query(value = "UPDATE RZTSYSCOMPANY SET COMPANYNAME=?1 ,ORGID=?2 ,ORGNAME=?3,UPDATETIME=sysdate WHERE ID=?4", nativeQuery = true)
    int updateRztsyscompany(String cmpanyname, String orgid, String orgname, String id);

    @Modifying
    @Query(value = "DELETE FROM RZTSYSCOMPANYFILE WHERE COMPANYID=?1 ", nativeQuery = true)
    int deletePanyFile(String id);

    @Modifying
    @Query(value = "DELETE FROM RZTSYSCOMPANYFILE WHERE COMPANYID=?1 AND FILETYPE =?2", nativeQuery = true)
    int deleteUpadtePanyFile(String id, String filetype);

    @Modifying
    @Query(value = " delete from RZTSYSCOMPANY where id=?1", nativeQuery = true)
    int deleteRztsyscompany(String id);

}
