/**
 * 文件名：KHCYCLE
 * 版本信息：
 * 日期：2017/12/25 21:36:18
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.entity;

import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名称：KHCYCLE
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/25 21:36:18
 * 修改人：张虎成
 * 修改时间：2017/12/25 21:36:18
 * 修改备注：
 * @version
 */
@Entity
@Table(name="KH_CYCLE")
public class KhCycle implements Serializable{
    //字段描述:
    @Id
    private Long id;
    //字段描述: 电压等级
    @Column(name = "VTYPE")
    private String vtype;
    //字段描述: 线路名称
    @Column(name = "LINE_NAME")
    private String lineName;
    //字段描述: 段落
    @Column(name = "SECTION")
    private String section;
    //字段描述: 0未派发 1已派发 2已停用
    @Column(name = "STATUS")
    private int status;
    //字段描述: 任务名称
    @Column(name = "TASK_NAME")
    private String taskName;
    //字段描述: 线路id
    @Column(name = "LINE_ID")
    private Long lineId;
    //字段描述: 通道运维单位
    @Column(name = "TDYW_ORG")
    private String tdywOrg;
    //字段描述: 消缺时间
    @Column(name = "XQ_TIME")
    private Date xqTime;
    //字段描述: 隐患id
    @Column(name = "YH_ID")
    private Long yhId;
    //字段描述: 外协单位
    @Column(name = "WX_ORG")
    private String wxOrg;
    //字段描述: 稽查人
    @Column(name = "JC_USER_ID")
    private String jcUserId;
    //字段描述: 0未派发1已派发2未稽查3已稽查
    @Column(name = "JC_STATUS")
    private int jcStatus;
    //字段描述: 创建时间
    @Column(name = "CREATE_TIME")
    private Date createTime;
    //字段描述: 创建时间
    @Column(name = "PF_TIME")
    private Date pfTime;


    public void setId(){
            this.id = new SnowflakeIdWorker(0,0).nextId();
    }
    public Long getId(){
        return this.id;
    }

    public void setVtype(String vtype){
        this.vtype = vtype;
    }
    public String getVtype(){
        return this.vtype;
    }

    public void setLineName(String lineName){
        this.lineName = lineName;
    }
    public String getLineName(){
        return this.lineName;
    }

    public void setSection(String section){
        this.section = section;
    }
    public String getSection(){
        return this.section;
    }

    public void setTaskName(String taskName){
        this.taskName = taskName;
    }
    public String getTaskName(){
        return this.taskName;
    }

    public void setTdywOrg(String tdywOrg){
        this.tdywOrg = tdywOrg;
    }
    public String getTdywOrg(){
        return this.tdywOrg;
    }

    public void setXqTime(Date xqTime){
        this.xqTime = xqTime;
    }
    public Date getXqTime(){
        return this.xqTime;
    }

    public void setWxOrg(String wxOrg){
        this.wxOrg = wxOrg;
    }
    public String getWxOrg(){
        return this.wxOrg;
    }

    public void setJcUserId(String jcUserId){
        this.jcUserId = jcUserId;
    }
    public String getJcUserId(){
        return this.jcUserId;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getYhId() {
        return yhId;
    }

    public void setYhId(Long yhId) {
        this.yhId = yhId;
    }

    public int getJcStatus() {
        return jcStatus;
    }

    public void setJcStatus(int jcStatus) {
        this.jcStatus = jcStatus;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setPfTime(Date pfTime) {
        this.pfTime = pfTime;
    }

    public Date getPfTime() {
        return pfTime;
    }
}
