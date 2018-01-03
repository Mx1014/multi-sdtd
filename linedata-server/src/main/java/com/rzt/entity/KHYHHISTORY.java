/**
 * 文件名：KHYHHISTORY           
 * 版本信息：    
 * 日期：2017/12/27 18:49:01
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
 * 类名称：KHYHHISTORY
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/27 18:49:01
 * 修改人：张虎成
 * 修改时间：2017/12/27 18:49:01
 * 修改备注：
 * @version
 */
@Entity
@Table(name="KH_YH_HISTORY")
public class KHYHHISTORY implements Serializable{
	//字段描述: 外协单位id
	@Column(name = "WXORG_ID")
	private String wxorgId;
	//字段描述: 通道单位id
	@Column(name = "YWORG_ID")
	private String yworgId;
	//字段描述: 施工情况（0 未施工 1 正在施工 2 已暂停施工）
	@Column(name = "SGQK")
	private String sgqk;
	//字段描述: 隐患类别（大棚类、仓储类等）
	@Column(name = "YHLB")
	private String yhlb;
	//字段描述: 区段
	@Column(name = "SECTION")
	private String section;
	//字段描述: 创建时间
	@Column(name = "CREATE_TIME")
	private Date createTime;
	//字段描述: 是否定级
	@Column(name = "SFDJ")
	private String sfdj;
	//字段描述: 线路重要程度（生命线 电压跌落线 一般线路）
	@Column(name = "XLZYCD")
	private String xlzycd;
	//字段描述: 照片修改时间
	@Column(name = "ZPXGSJ")
	private Date zpxgsj;
	//字段描述: 电压等级
	@Column(name = "VTYPE")
	private String vtype;
	//字段描述: 设备维护单位
	@Column(name = "SBYW_ORG")
	private String sbywOrg;
	//字段描述: 涉及线路名称
	@Column(name = "LINE_NAME")
	private String lineName;
	//字段描述: 终止杆塔号ID
	@Column(name = "END_TOWER")
	private String endTower;
	//字段描述: 起始杆塔号ID
	@Column(name = "START_TOWER")
	private String startTower;
	//字段描述: 更新时间
	@Column(name = "UPDATE_TIME")
	private Date updateTime;
	//字段描述: 0：pc 1：手机 2:excel导入
	@Column(name = "SDGS")
	private String sdgs;
	//字段描述: 关联线路id
	@Column(name = "LINE_ID")
	private String lineId;
	//字段描述: 导线对隐患净空距离
	@Column(name = "DXDYHSPJL")
	private String dxdyhspjl;
	//字段描述: 导线对隐患水平距离
	@Column(name = "DXXYHCZJL")
	private String dxxyhczjl;
	//字段描述: 导线对隐患垂直距离
	@Column(name = "XDXYHJKJL")
	private String xdxyhjkjl;
	//字段描述: 隐患形成原因
	@Column(name = "YHXCYY")
	private String yhxcyy;
	//字段描述: 树木管控措施
	@Column(name = "SMGKCS")
	private String smgkcs;
	//字段描述: 停电处理完成时间
	@Column(name = "TDCLWCSJ")
	private Date tdclwcsj;
	//字段描述: 纬度
	@Column(name = "WD")
	private float wd;
	//字段描述: 经度
	@Column(name = "JD")
	private float jd;
	//字段描述: 隐患状态(0未消除1消除)
	@Column(name = "YHZT")
	private String yhzt;
	//字段描述: 填报时间
	@Column(name = "TBSJ")
	private String tbsj;
	//字段描述: 填报部门
	@Column(name = "TBBM")
	private String tbbm;
	//字段描述: 填报部门ID
	@Column(name = "TBBMID")
	private String tbbmid;
	//字段描述: 填报人
	@Column(name = "TBR")
	private String tbr;
	//字段描述: 填报人ID
	@Column(name = "TBRID")
	private String tbrid;
	//字段描述: 护线信息员
	@Column(name = "HXXXY")
	private String hxxxy;
	//字段描述: 护线信息员ID
	@Column(name = "HXXXYID")
	private String hxxxyid;
	//字段描述: 隐患点外力情况
	@Column(name = "YHDWLQK")
	private String yhdwlqk;
	//字段描述: 宣传牌
	@Column(name = "XCP")
	private String xcp;
	//字段描述: 技防措施
	@Column(name = "JFCS")
	private String jfcs;
	//字段描述: 管控措施
	@Column(name = "GKCS")
	private String gkcs;
	//字段描述: 派出所电话
	@Column(name = "PCSDH")
	private String pcsdh;
	//字段描述: 所属派出所
	@Column(name = "SSPCS")
	private String sspcs;
	//字段描述: 隐患消除时间
	@Column(name = "YHXQ_TIME")
	private Date yhxqTime;
	//字段描述: 定级人
	@Column(name = "DJY")
	private String djy;
	//字段描述: 定级人ID
	@Column(name = "DJYID")
	private String djyid;
	//字段描述: 定级时间
	@Column(name = "DJSJ")
	private Date djsj;
	//字段描述: 隐患发现时间
	@Column(name = "YHFXSJ")
	private Date yhfxsj;
	//字段描述: 看护任务id
	@Column(name = "TASK_ID")
	private String taskId;
	//字段描述: 隐患责任单位上级主管部门(空闲)
	@Column(name = "YHZRDWSJZRBM")
	private String yhzrdwsjzrbm;
	//字段描述: 隐患责任单位电话
	@Column(name = "YHZRDWDH")
	private String yhzrdwdh;
	//字段描述: 隐患责任单位联系人
	@Column(name = "YHZRDWLXR")
	private String yhzrdwlxr;
	//字段描述: 隐患责任单位
	@Column(name = "YHZRDW")
	private String yhzrdw;
	//字段描述: 隐患地点(村)
	@Column(name = "YHTDC")
	private String yhtdc;
	//字段描述: 隐患地点(乡镇街道)
	@Column(name = "YHTDXZJD")
	private String yhtdxzjd;
	//字段描述: 隐患地点(区县)
	@Column(name = "YHTDQX")
	private String yhtdqx;
	//字段描述: 隐患描述
	@Column(name = "YHMS")
	private String yhms;
	//字段描述: 隐患级别（固定隐患 动态隐患 动态风险）
	@Column(name = "YHJB1")
	private String yhjb1;
	//字段描述: 危机程度（一般、重要、危机）
	@Column(name = "YHJB")
	private String yhjb;
	//字段描述: 维护单位（通道单位）
	@Column(name = "TDYW_ORG")
	private String tdywOrg;
	//字段描述: 通道外协单位
	@Column(name = "TDWX_ORG")
	private String tdwxOrg;
	//字段描述: 输电平台id
	@Id
	private Long id;

	public void setWxorgId(String wxorgId){
		this.wxorgId = wxorgId;
	}
	public String getWxorgId(){
		return this.wxorgId;
	}



	public void setYworgId(String yworgId){
		this.yworgId = yworgId;
	}
	public String getYworgId(){
		return this.yworgId;
	}



	public void setSgqk(String sgqk){
		this.sgqk = sgqk;
	}
	public String getSgqk(){
		return this.sgqk;
	}



	public void setYhlb(String yhlb){
		this.yhlb = yhlb;
	}
	public String getYhlb(){
		return this.yhlb;
	}



	public void setSection(String section){
		this.section = section;
	}
	public String getSection(){
		return this.section;
	}



	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	public Date getCreateTime(){
		return this.createTime;
	}



	public void setSfdj(String sfdj){
		this.sfdj = sfdj;
	}
	public String getSfdj(){
		return this.sfdj;
	}



	public void setXlzycd(String xlzycd){
		this.xlzycd = xlzycd;
	}
	public String getXlzycd(){
		return this.xlzycd;
	}



	public void setZpxgsj(Date zpxgsj){
		this.zpxgsj = zpxgsj;
	}
	public Date getZpxgsj(){
		return this.zpxgsj;
	}



	public void setVtype(String vtype){
		this.vtype = vtype;
	}
	public String getVtype(){
		return this.vtype;
	}



	public void setSbywOrg(String sbywOrg){
		this.sbywOrg = sbywOrg;
	}
	public String getSbywOrg(){
		return this.sbywOrg;
	}



	public void setLineName(String lineName){
		this.lineName = lineName;
	}
	public String getLineName(){
		return this.lineName;
	}



	public void setEndTower(String endTower){
		this.endTower = endTower;
	}
	public String getEndTower(){
		return this.endTower;
	}



	public void setStartTower(String startTower){
		this.startTower = startTower;
	}
	public String getStartTower(){
		return this.startTower;
	}



	public void setUpdateTime(Date updateTime){
		this.updateTime = updateTime;
	}
	public Date getUpdateTime(){
		return this.updateTime;
	}



	public void setSdgs(String sdgs){
		this.sdgs = sdgs;
	}
	public String getSdgs(){
		return this.sdgs;
	}



	public void setLineId(String lineId){
		this.lineId = lineId;
	}
	public String getLineId(){
		return this.lineId;
	}



	public void setDxdyhspjl(String dxdyhspjl){
		this.dxdyhspjl = dxdyhspjl;
	}
	public String getDxdyhspjl(){
		return this.dxdyhspjl;
	}



	public void setDxxyhczjl(String dxxyhczjl){
		this.dxxyhczjl = dxxyhczjl;
	}
	public String getDxxyhczjl(){
		return this.dxxyhczjl;
	}



	public void setXdxyhjkjl(String xdxyhjkjl){
		this.xdxyhjkjl = xdxyhjkjl;
	}
	public String getXdxyhjkjl(){
		return this.xdxyhjkjl;
	}



	public void setYhxcyy(String yhxcyy){
		this.yhxcyy = yhxcyy;
	}
	public String getYhxcyy(){
		return this.yhxcyy;
	}



	public void setSmgkcs(String smgkcs){
		this.smgkcs = smgkcs;
	}
	public String getSmgkcs(){
		return this.smgkcs;
	}



	public void setTdclwcsj(Date tdclwcsj){
		this.tdclwcsj = tdclwcsj;
	}
	public Date getTdclwcsj(){
		return this.tdclwcsj;
	}



	public void setWd(float wd){
		this.wd = wd;
	}
	public float getWd(){
		return this.wd;
	}



	public void setJd(float jd){
		this.jd = jd;
	}
	public float getJd(){
		return this.jd;
	}



	public void setYhzt(String yhzt){
		this.yhzt = yhzt;
	}
	public String getYhzt(){
		return this.yhzt;
	}



	public void setTbsj(String tbsj){
		this.tbsj = tbsj;
	}
	public String getTbsj(){
		return this.tbsj;
	}



	public void setTbbm(String tbbm){
		this.tbbm = tbbm;
	}
	public String getTbbm(){
		return this.tbbm;
	}



	public void setTbbmid(String tbbmid){
		this.tbbmid = tbbmid;
	}
	public String getTbbmid(){
		return this.tbbmid;
	}



	public void setTbr(String tbr){
		this.tbr = tbr;
	}
	public String getTbr(){
		return this.tbr;
	}



	public void setTbrid(String tbrid){
		this.tbrid = tbrid;
	}
	public String getTbrid(){
		return this.tbrid;
	}



	public void setHxxxy(String hxxxy){
		this.hxxxy = hxxxy;
	}
	public String getHxxxy(){
		return this.hxxxy;
	}



	public void setHxxxyid(String hxxxyid){
		this.hxxxyid = hxxxyid;
	}
	public String getHxxxyid(){
		return this.hxxxyid;
	}



	public void setYhdwlqk(String yhdwlqk){
		this.yhdwlqk = yhdwlqk;
	}
	public String getYhdwlqk(){
		return this.yhdwlqk;
	}



	public void setXcp(String xcp){
		this.xcp = xcp;
	}
	public String getXcp(){
		return this.xcp;
	}



	public void setJfcs(String jfcs){
		this.jfcs = jfcs;
	}
	public String getJfcs(){
		return this.jfcs;
	}



	public void setGkcs(String gkcs){
		this.gkcs = gkcs;
	}
	public String getGkcs(){
		return this.gkcs;
	}



	public void setPcsdh(String pcsdh){
		this.pcsdh = pcsdh;
	}
	public String getPcsdh(){
		return this.pcsdh;
	}



	public void setSspcs(String sspcs){
		this.sspcs = sspcs;
	}
	public String getSspcs(){
		return this.sspcs;
	}



	public void setYhxqTime(Date yhxqTime){
		this.yhxqTime = yhxqTime;
	}
	public Date getYhxqTime(){
		return this.yhxqTime;
	}



	public void setDjy(String djy){
		this.djy = djy;
	}
	public String getDjy(){
		return this.djy;
	}



	public void setDjyid(String djyid){
		this.djyid = djyid;
	}
	public String getDjyid(){
		return this.djyid;
	}



	public void setDjsj(Date djsj){
		this.djsj = djsj;
	}
	public Date getDjsj(){
		return this.djsj;
	}



	public void setYhfxsj(Date yhfxsj){
		this.yhfxsj = yhfxsj;
	}
	public Date getYhfxsj(){
		return this.yhfxsj;
	}



	public void setTaskId(String taskId){
		this.taskId = taskId;
	}
	public String getTaskId(){
		return this.taskId;
	}



	public void setYhzrdwsjzrbm(String yhzrdwsjzrbm){
		this.yhzrdwsjzrbm = yhzrdwsjzrbm;
	}
	public String getYhzrdwsjzrbm(){
		return this.yhzrdwsjzrbm;
	}



	public void setYhzrdwdh(String yhzrdwdh){
		this.yhzrdwdh = yhzrdwdh;
	}
	public String getYhzrdwdh(){
		return this.yhzrdwdh;
	}



	public void setYhzrdwlxr(String yhzrdwlxr){
		this.yhzrdwlxr = yhzrdwlxr;
	}
	public String getYhzrdwlxr(){
		return this.yhzrdwlxr;
	}



	public void setYhzrdw(String yhzrdw){
		this.yhzrdw = yhzrdw;
	}
	public String getYhzrdw(){
		return this.yhzrdw;
	}



	public void setYhtdc(String yhtdc){
		this.yhtdc = yhtdc;
	}
	public String getYhtdc(){
		return this.yhtdc;
	}



	public void setYhtdxzjd(String yhtdxzjd){
		this.yhtdxzjd = yhtdxzjd;
	}
	public String getYhtdxzjd(){
		return this.yhtdxzjd;
	}



	public void setYhtdqx(String yhtdqx){
		this.yhtdqx = yhtdqx;
	}
	public String getYhtdqx(){
		return this.yhtdqx;
	}



	public void setYhms(String yhms){
		this.yhms = yhms;
	}
	public String getYhms(){
		return this.yhms;
	}



	public void setYhjb1(String yhjb1){
		this.yhjb1 = yhjb1;
	}
	public String getYhjb1(){
		return this.yhjb1;
	}



	public void setYhjb(String yhjb){
		this.yhjb = yhjb;
	}
	public String getYhjb(){
		return this.yhjb;
	}



	public void setTdywOrg(String tdywOrg){
		this.tdywOrg = tdywOrg;
	}
	public String getTdywOrg(){
		return this.tdywOrg;
	}



	public void setTdwxOrg(String tdwxOrg){
		this.tdwxOrg = tdwxOrg;
	}
	public String getTdwxOrg(){
		return this.tdwxOrg;
	}



	public void setId(Long id){
		if(id==null||id==0){
			this.id = new SnowflakeIdWorker(0,0).nextId();
		}else{
			this.id = id;
		}
	}
	public Long getId(){
		return this.id;
	}



}
