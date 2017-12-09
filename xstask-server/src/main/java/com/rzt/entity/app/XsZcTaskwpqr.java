package com.rzt.entity.app;

import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.entity.appentity
 * @Author: liuze
 * @date: 2017-12-7 19:33
 */
@Entity
@Table(name = "XS_ZC_TASKWPQR")
public class XsZcTaskwpqr extends BaseEntity implements Serializable {
    /**
     * 字段描述: ID
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String ID;
    /**
     * 字段描述: 任务ID
     */
    @Column(name = "TASKID")
    private String TASKID;
    /**
     * 字段描述：物品确认信息
     */
    @Column(name = "WP_ZT")
    private String WP_ZT;

    @ExcelResources(title = "ID", order = 1)
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    @ExcelResources(title = "任务ID", order = 2)
    public String getTASKID() {
        return TASKID;
    }

    public void setTASKID(String TASKID) {
        this.TASKID = TASKID;
    }

    @ExcelResources(title = "物品确认信息", order = 3)
    public String getWP_ZT() {
        return WP_ZT;
    }

    public void setWP_ZT(String WP_ZT) {
        this.WP_ZT = WP_ZT;
    }
}
