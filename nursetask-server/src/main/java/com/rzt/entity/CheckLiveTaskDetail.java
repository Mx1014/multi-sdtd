/**
 * 文件名：CHECKLIVETASKDETAILXS           
 * 版本信息：    
 * 日期：2017/12/25 11:14:58    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名称：CHECKLIVETASKDETAILXS    
 * 类描述：${table.comment}
 * 创建人：张虎成   
 * 创建时间：2017/12/25 11:14:58 
 * 修改人：张虎成    
 * 修改时间：2017/12/25 11:14:58    
 * 修改备注：    
 * @version
 */
@Entity
@DynamicUpdate
@Table(name="CHECK_LIVE_TASK_DETAIL")
public class CheckLiveTaskDetail implements Serializable{
    //字段描述:
    @Id
    private Long id;
    //字段描述: 稽查任务id（check_live_task表）
    @Column(name = "TASK_ID")
    private Long taskId;
    //字段描述: 巡视任务id
    @Column(name = "KH_TASK_ID")
    private Long khTaskId;
    //字段描述: 巡视任务类型（0 正常 1保电 2 特殊）
    @Column(name = "KH_TASK_TYPE")
    private Integer khTaskType;
    //字段描述: 计划开始时间
    @Column(name = "PLAN_START_TIME")
    private Date planStartTime;
    //字段描述: 计划结束时间
    @Column(name = "PLAN_END_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date planEndTime;
    //字段描述: 实际开始时间
    @Column(name = "REAL_START_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date realStartTime;
    //字段描述: 实际结束时间
    @Column(name = "REAL_END_TIME")
    private Date realEndTime;
    //字段描述: 执行情况 0未开始 1进行中 2已完成 3已超期
    @Column(name = "STATUS")
    private Integer status;
    //字段描述: 达到现场时间
    @Column(name = "DDXC_TIME")
    private Date ddxcTime;
    //字段描述: 周期内第多少次任务
    @Column(name = "DZWL")
    private Integer dzwl;
    //字段描述: 是否在岗(0是 1否)
    @Column(name = "SFZG")
    private Integer sfzg;
    //字段描述: 现场APP人员是否一直（0是 1否）
    @Column(name = "RYYZ")
    private Integer ryyz;
    //字段描述: 是否清楚段落电压等级等（0是 1否）
    @Column(name = "DYDJ")
    private Integer dydj;
    //字段描述: 是否掌握现场隐患信息（0是 1否）
    @Column(name = "YHXX")
    private Integer yhxx;
    //字段描述: 是否清楚隐患处置方案（0 是 1否）
    @Column(name = "CZFA")
    private Integer czfa;
    //字段描述: 是否存在其他问题（0 是 1否）
    @Column(name = "QTWT")
    private Integer qtwt;
    //字段描述: 创建时间
    @Column(name = "CREATE_TIME")
    private Date createTime;
    //字段描述: 通道运维单位
    @Column(name = "TDYW_ORGID")
    private String tdywOrgid;
    //字段描述: 通道外协单位id
    @Column(name = "TDWX_ORGID")
    private String tdwxOrgid;

    @Column(name = "SJT")
    private Integer sjt;

    @Column(name = "DXJX")
    private Integer dxjx;

    public void setId(Long id){
        if(id==null||id==0){
            SnowflakeIdWorker instance = SnowflakeIdWorker.getInstance(8, 3);
            this.id = instance.nextId();
        }else{
            this.id = id;
        }
    }

    @ExcelResources(title="",order=1)
    public Long getId(){
        return this.id;
    }

    public void setTaskId(Long taskId){
        this.taskId = taskId;
    }
    @ExcelResources(title="稽查任务id（check_live_task表）",order=3)
    public Long getTaskId(){
        return this.taskId;
    }

    public void setKhTaskId(Long khTaskId){
        this.khTaskId = khTaskId;
    }
    @ExcelResources(title="巡视任务id",order=4)
    public Long getKhTaskId(){
        return this.khTaskId;
    }

    public void setKhTaskType(Integer khTaskType){
        this.khTaskType = khTaskType;
    }
    @ExcelResources(title="巡视任务类型（0 特殊 1保电 2 正常）",order=5)
    public Integer getKhTaskType(){
        return this.khTaskType;
    }

    public void setPlanStartTime(Date planStartTime){
        this.planStartTime = planStartTime;
    }
    @ExcelResources(title="计划开始时间",order=6)
    public Date getPlanStartTime(){
        return this.planStartTime;
    }

    public void setPlanEndTime(Date planEndTime){
        this.planEndTime = planEndTime;
    }
    @ExcelResources(title="计划结束时间",order=7)
    public Date getPlanEndTime(){
        return this.planEndTime;
    }

    public void setRealStartTime(Date realStartTime){
        this.realStartTime = realStartTime;
    }
    @ExcelResources(title="实际开始时间",order=8)
    public Date getRealStartTime(){
        return this.realStartTime;
    }

    public void setRealEndTime(Date realEndTime){
        this.realEndTime = realEndTime;
    }
    @ExcelResources(title="实际结束时间",order=9)
    public Date getRealEndTime(){
        return this.realEndTime;
    }

    public void setStatus(Integer status){
        this.status = status;
    }
    @ExcelResources(title="执行情况 0未开始 1进行中 2已完成 3已超期",order=10)
    public Integer getStatus(){
        return this.status;
    }

    public void setDdxcTime(Date ddxcTime){
        this.ddxcTime = ddxcTime;
    }
    @ExcelResources(title="达到现场时间",order=11)
    public Date getDdxcTime(){
        return this.ddxcTime;
    }

    public void setDzwl(Integer dzwl){
        this.dzwl = dzwl;
    }
    public Integer getDzwl(){
        return this.dzwl;
    }

    public void setSfzg(Integer sfzg){
        this.sfzg = sfzg;
    }
    @ExcelResources(title="是否在岗(0是 1否)",order=13)
    public Integer getSfzg(){
        return this.sfzg;
    }

    public void setRyyz(Integer ryyz){
        this.ryyz = ryyz;
    }
    @ExcelResources(title="现场APP人员是否一直（0是 1否）",order=14)
    public Integer getRyyz(){
        return this.ryyz;
    }

    public void setDydj(Integer dydj){
        this.dydj = dydj;
    }
    @ExcelResources(title="是否清楚段落电压等级等（0是 1否）",order=15)
    public Integer getDydj(){
        return this.dydj;
    }

    public void setYhxx(Integer yhxx){
        this.yhxx = yhxx;
    }
    @ExcelResources(title="是否掌握现场隐患信息（0是 1否）",order=16)
    public Integer getYhxx(){
        return this.yhxx;
    }

    public void setCzfa(Integer czfa){
        this.czfa = czfa;
    }
    @ExcelResources(title="是否清楚隐患处置方案（0 是 1否）",order=17)
    public Integer getCzfa(){
        return this.czfa;
    }

    public void setQtwt(Integer qtwt){
        this.qtwt = qtwt;
    }
    @ExcelResources(title="是否存在其他问题（0 是 1否）",order=18)
    public Integer getQtwt(){
        return this.qtwt;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }
    @ExcelResources(title="创建时间",order=19)
    public Date getCreateTime(){
        return this.createTime;
    }

    public void setTdwxOrgid(String tdwxOrgid){
        this.tdwxOrgid = tdwxOrgid;
    }
    @ExcelResources(title="通道外协单位id",order=13)
    public String getTdwxOrgid(){
        return this.tdwxOrgid;
    }

    public void setTdywOrgid(String tdywOrgid){
        this.tdywOrgid = tdywOrgid;
    }
    @ExcelResources(title="通道运维单位",order=10)
    public String getTdywOrgid(){
        return this.tdywOrgid;
    }

    public Integer getSjt() {
        return sjt;
    }

    public void setSjt(Integer sjt) {
        this.sjt = sjt;
    }

    public Integer getDxjx() {
        return dxjx;
    }

    public void setDxjx(Integer dxjx) {
        this.dxjx = dxjx;
    }
}