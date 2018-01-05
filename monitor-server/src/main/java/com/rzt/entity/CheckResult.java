package com.rzt.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "check_result")
public class CheckResult implements Serializable {
	
	//主键
	@Id
	private Long id;
	
	//有问题的照片id（多个照片id，以逗号分隔）
	@Column(name = "PHOTO_IDS")
	private String photoIds;
	
	//问题内容
	@Column(name = "QUESTION_INFO")
	private String questionInfo;
	
	//创建时间
	@Column(name = "CREATE_TIME")
	private Date createTime;
	
	//线路id
	@Column(name = "LINE_ID")
	private Long lineId;
	
	//审核详情表的id
	@Column(name = "CHECK_DETAIL_ID")
	private Long checkDetailID;

	//字段描述: 问题类型
	@Column(name = "QUESTION_TYPE")
	private Integer questionType;

	public Long getId() {
		return id;
	}

	public Long getCheckDetailID() {
		return checkDetailID;
	}

	public void setCheckDetailID(Long checkDetailID) {
		this.checkDetailID = checkDetailID;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPhotoIds(String photoIds){
		this.photoIds = photoIds;
	}
	public String getPhotoIds(){
		return this.photoIds;
	}
	public String getQuestionInfo() {
		return questionInfo;
	}

	public void setQuestionInfo(String questionInfo) {
		this.questionInfo = questionInfo;
	}

	

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getLineId() {
		return lineId;
	}

	public void setLineId(Long lineId) {
		this.lineId = lineId;
	}

	public void setQuestionType(Integer questionType){
		this.questionType = questionType;
	}
	public Integer getQuestionType(){
		return this.questionType;
	}

	@Override
	public String toString() {
		return "CheckResult{" +
				"id=" + id +
				", photoIds='" + photoIds + '\'' +
				", questionInfo='" + questionInfo + '\'' +
				", createTime=" + createTime +
				", lineId=" + lineId +
				", checkDetailID=" + checkDetailID +
				", questionType=" + questionType +
				'}';
	}
}
