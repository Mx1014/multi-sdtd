package com.rzt.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "check_detail")
public class CheckDetail implements Serializable {
	//主键
	@Id
	private Long id;
	
	//通道单位id
	@Column(name = "TD_ORG")
	private String tdOrg; 
	//审核详情分类（1巡视 2看护 3现场稽查 4后台稽查）
	@Column(name = "CHECK_DETAIL_TYPE")
	private Integer checkDetailType; 
	//问题任务id
	@Column(name = "QUESTION_TASK_ID")
	private Long questionTaskId; 
	//创建时间
	@Column(name = "CREATE_TIME")
	private Date createTime; 
	//审核人员id
	@Column(name = "CHECK_USER")
	private String checkUser;
	//审核单位id
	@Column(name = "CHECK_ORG")
	private String checkOrg;
	//问题人员id
	@Column(name = "QUESTION_USER_ID")
	private String questionUserId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTdOrg() {
		return tdOrg;
	}
	public void setTdOrg(String tdOrg) {
		this.tdOrg = tdOrg;
	}
	public Integer getCheckDetailType() {
		return checkDetailType;
	}
	public void setCheckDetailType(Integer checkDetailType) {
		this.checkDetailType = checkDetailType;
	}
	public Long getQuestionTaskId() {
		return questionTaskId;
	}
	public void setQuestionTaskId(Long questionTaskId) {
		this.questionTaskId = questionTaskId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCheckUser() {
		return checkUser;
	}
	public void setCheckUser(String checkUser) {
		this.checkUser = checkUser;
	}
	public String getCheckOrg() {
		return checkOrg;
	}
	public void setCheckOrg(String checkOrg) {
		this.checkOrg = checkOrg;
	}
	public String getQuestionUserId() {
		return questionUserId;
	}
	public void setQuestionUserId(String questionUserId) {
		this.questionUserId = questionUserId;
	}

	@Override
	public String toString() {
		return "CheckDetail{" +
				"id=" + id +
				", tdOrg='" + tdOrg + '\'' +
				", checkDetailType=" + checkDetailType +
				", questionTaskId=" + questionTaskId +
				", createTime=" + createTime +
				", checkUser='" + checkUser + '\'' +
				", checkOrg='" + checkOrg + '\'' +
				", questionUserId='" + questionUserId + '\'' +
				'}';
	}
}
