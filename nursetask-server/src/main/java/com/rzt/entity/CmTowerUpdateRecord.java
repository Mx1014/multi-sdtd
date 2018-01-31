/**
 * 文件名：CmTowerUpdateRecord
 * 版本信息：
 * 日期：2018/01/31 10:53:52
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.entity;

import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名称：CmTowerUpdateRecord    
 * 类描述：${table.comment}
 * 创建人：张虎成   
 * 创建时间：2018/01/31 10:53:52 
 * 修改人：张虎成    
 * 修改时间：2018/01/31 10:53:52    
 * 修改备注：    
 * @version
 */
@Entity
@Table(name="CM_TOWER_UPDATE_RECORD")
public class CmTowerUpdateRecord implements Serializable {
    //字段描述:
    @Id
    private long id;
    //字段描述:
    @Column(name = "USER_ID")
    private String userId;
    //字段描述:
    @Column(name = "TOWER_ID")
    private Long towerId;
    //字段描述:
    @Column(name = "LINE_NAME")
    private String lineName;
    //字段描述:
    @Column(name = "CREATE_TIME")
    private Date createTime;
    //字段描述:
    @Column(name = "LON")
    private String lon;
    //字段描述:
    @Column(name = "LAT")
    private String lat;
    //字段描述:
    @Column(name = "DETAIL_ID")
    private Long detailId;


    public void setId(Long id) {
        if (id == null || id == 0) {
            SnowflakeIdWorker instance = SnowflakeIdWorker.getInstance(8, 27);
            this.id = instance.nextId();
        } else {
            this.id = id;
        }
    }

    @ExcelResources(title = "", order = 1)
    public long getId() {
        return this.id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @ExcelResources(title = "", order = 2)
    public String getUserId() {
        return this.userId;
    }

    public void setTowerId(Long towerId) {
        this.towerId = towerId;
    }

    @ExcelResources(title = "", order = 3)
    public Long getTowerId() {
        return this.towerId;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    @ExcelResources(title = "", order = 4)
    public String getLineName() {
        return this.lineName;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @ExcelResources(title = "", order = 5)
    public Date getCreateTime() {
        return this.createTime;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    @ExcelResources(title = "", order = 6)
    public String getLon() {
        return this.lon;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    @ExcelResources(title = "", order = 7)
    public String getLat() {
        return this.lat;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getDetailId() {
        return detailId;
    }
}