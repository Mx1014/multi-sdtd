/**
 * 文件名：KhYhTower
 * 版本信息：
 * 日期：2018/03/14 02:22:31
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.entity;

import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 类名称：KhYhTower    
 * 类描述：${table.comment}
 * 创建人：张虎成   
 * 创建时间：2018/03/14 02:22:31 
 * 修改人：张虎成    
 * 修改时间：2018/03/14 02:22:31    
 * 修改备注：    
 * @version
 */
@Entity
@Table(name = "KH_YH_TOWER")
public class KhYhTower implements Serializable {
    //字段描述:
    @Id
    private Long id;
    //字段描述: 隐患id
    @Column(name = "YH_ID")
    private Long yhId;
    //字段描述: 杆塔id
    @Column(name = "TOWER_ID")
    private Long towerId;
    @Column(name = "RADIUS")
    private int radius;

    public void setYhId(Long yhId) {
        this.yhId = yhId;
    }

    public Long getYhId() {
        return this.yhId;
    }

    public void setTowerId(Long towerId) {
        this.towerId = towerId;
    }

    public Long getTowerId() {
        return this.towerId;
    }

    public void setId(Long id) {
        if (id == null || id == 0) {
            SnowflakeIdWorker instance = SnowflakeIdWorker.getInstance(8, 11);
            this.id = instance.nextId();
        } else {
            this.id = id;
        }
    }

    public Long getId() {
        return this.id;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}