/**
 * 文件名：CMINSTALL
 * 版本信息：
 * 日期：2017/12/11 15:58:59
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.entity;

import com.rzt.util.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 类名称：CMINSTALL
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/11 15:58:59
 * 修改人：张虎成
 * 修改时间：2017/12/11 15:58:59
 * 修改备注：
 */
@Entity
@Table(name = "CM_INSTALL")
public class CMINSTALL implements Serializable {
    //字段描述:
    @Id
    private Long id;
    //字段描述: 专业信息
    @Column(name = "ZYXX_NAME")
    private String zyxxName;
    //字段描述: 专业详情 0 巡视 1 看护 2 稽查
    @Column(name = "ZYLX")
    private String zylx;
    //字段描述: 设置时间范围照片等
    @Column(name = "SZ_NUM")
    private String szNum;
    //字段描述: 数值
    @Column(name = "KEY_NUM")
    private String keyNum;

    public void setId(Long id) {
        if (id == null || id == 0) {
            this.id = new SnowflakeIdWorker(0, 0).nextId();
        } else {
            this.id = id;
        }
    }

    public Long getId() {
        return this.id;
    }


    public void setZyxxName(String zyxxName) {
        this.zyxxName = zyxxName;
    }

    public String getZyxxName() {
        return this.zyxxName;
    }


    public void setZylx(String zylx) {
        this.zylx = zylx;
    }

    public String getZylx() {
        return this.zylx;
    }


    public void setSzNum(String szNum) {
        this.szNum = szNum;
    }

    public String getSzNum() {
        return this.szNum;
    }


    public void setKeyNum(String keyNum) {
        this.keyNum = keyNum;
    }

    public String getKeyNum() {
        return this.keyNum;
    }


}
