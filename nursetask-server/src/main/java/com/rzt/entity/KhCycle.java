/**
 * 文件名：KHCYCLE
 * 版本信息：
 * 日期：2017/12/25 21:36:18
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.entity;

import com.rzt.util.excelUtil.ExcelResources;
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
    //字段描述: 创建时间
    @Column(name = "CREATE_TIME")
    private Date createTime;
    //字段描述: 创建时间
    @Column(name = "PF_TIME")
    private Date pfTime;
    @Column(name = "LONGITUDE")
    private String longitude;
    @Column(name = "LATITUDE")
    private String latitude;
    //字段描述: 隐患点半径
    @Column(name="radius")
    private String radius;
    //字段描述: 运维单位id
    @Column(name="TDYW_ORGID")
    private String tdywOrgId;
    //字段描述: 外协单位id
    @Column(name="WX_ORGID")
    private String wxOrgId;


    @ExcelResources(title="被稽查的任务id",order=1)
    public void setId(){
            this.id = new SnowflakeIdWorker(1,0).nextId();
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

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longtitude) {
        this.longitude = longtitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getRadius() {
        return radius;
    }

    public String getTdywOrgId() {
        return tdywOrgId;
    }

    public void setTdywOrgId(String tdywOrgId) {
        this.tdywOrgId = tdywOrgId;
    }

    public String getWxOrgId() {
        return wxOrgId;
    }

    public void setWxOrgId(String wxOrgId) {
        this.wxOrgId = wxOrgId;
    }
}
