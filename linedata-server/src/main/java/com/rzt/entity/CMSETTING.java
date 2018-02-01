package com.rzt.entity;

import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="CM_SETTING")
public class CMSETTING {
    @Id
    private Long id;
    @Column(name = "SETTING_KEY")
    private String settingKey;
    @Column(name = "SETTING_VALUE")
    private String settingValue;
    @Column(name = "SETTING_TYPE")
    private String settingType;
    @Column(name = "SETTING_MODEL")
    private String settingModel;
    @Transient
    @Column(name = "SETTING_DESC")
    private String settingDesc;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        if(id==null||id==0){
            this.id = SnowflakeIdWorker.getInstance(9,1).nextId();
        }else{
            this.id = id;
        }
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getSettingType() {
        return settingType;
    }

    public void setSettingType(String settingType) {
        this.settingType = settingType;
    }

    public String getSettingModel() {
        return settingModel;
    }

    public void setSettingModel(String settingModel) {
        this.settingModel = settingModel;
    }

    public String getSettingDesc() {
        return settingDesc;
    }

    public void setSettingDesc(String settingDesc) {
        this.settingDesc = settingDesc;
    }
}
