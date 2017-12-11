/**
 * 文件名：KHYHHISTORYBAK
 * 版本信息：
 * 日期：2017/12/01 10:37:32
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.entity;
import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * 类名称：KHYHHISTORYBAK
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/01 10:37:32
 * 修改人：张虎成
 * 修改时间：2017/12/01 10:37:32
 * 修改备注：
 * @version
 */
@Entity
public class KhYhHistoryBak implements Serializable{
    //字段描述: 输电平台id
    @Id
    private Long id;
    //字段描述: 维护单位id（orgid）
    @Column(name = "TDWH_ORGID")
    private String tdywOrgid;
    //字段描述: 维护单位（通道单位）
    @Column(name = "TDWH_ORG")
    private String tdywOrg;
    //字段描述: 隐患级别
    @Column(name = "YHJB")
    private String yhjb;
    //字段描述: 隐患代码
    @Column(name = "YHDM")
    private String yhdm;
    //字段描述: 隐患描述
    @Column(name = "YHMS")
    private String yhms;
    //字段描述: 隐患地点(区县)
    @Column(name = "YHTDQX")
    private String yhtdqx;
    //字段描述: 隐患地点(乡镇街道)
    @Column(name = "YHTDXZJD")
    private String yhtdxzjd;
    //字段描述: 隐患地点(村)
    @Column(name = "YHTDC")
    private String yhtdc;
    //字段描述: 隐患责任单位
    @Column(name = "YHZRDW")
    private String yhzrdw;
    //字段描述: 隐患责任单位联系人
    @Column(name = "YHZRDWLXR")
    private String yhzrdwlxr;
    //字段描述: 隐患责任单位电话
    @Column(name = "YHZRDWDH")
    private String yhzrdwdh;
    //字段描述: 隐患责任单位上级主管部门(空闲)
    @Column(name = "YHZRDWSJZRBM")
    private String yhzrdwsjzrbm;
    //字段描述: taskid
    @Column(name = "YHDDGDDWID")
    private String yhddgddwid;
    //字段描述: 隐患发现时间
    @Column(name = "YHFXSJ")
    private String yhfxsj;
    //字段描述: 定级时间
    @Column(name = "DJSJ")
    private String djsj;
    //字段描述: 定级人ID
    @Column(name = "DJYID")
    private String djyid;
    //字段描述: 定级人
    @Column(name = "DJY")
    private String djy;
    //字段描述: 隐患消除时间
    @Column(name = "YHXQ_TIME")
    private String yhxqTime;
    //字段描述: 所属派出所
    @Column(name = "SSPCS")
    private String sspcs;
    //字段描述: 派出所电话
    @Column(name = "PCSDH")
    private String pcsdh;
    //字段描述: 管控措施
    @Column(name = "GKCS")
    private String gkcs;
    //字段描述: 技防措施
    @Column(name = "JFCS")
    private String jfcs;
    //字段描述: 宣传牌
    @Column(name = "XCP")
    private String xcp;
    //字段描述: 隐患点外力情况
    @Column(name = "YHDWLQK")
    private String yhdwlqk;
    //字段描述: 护线信息员ID
    @Column(name = "HXXXYID")
    private String hxxxyid;
    //字段描述: 护线信息员
    @Column(name = "HXXXY")
    private String hxxxy;
    //字段描述: 填报人ID
    @Column(name = "TBRID")
    private String tbrid;
    //字段描述: 填报人
    @Column(name = "TBR")
    private String tbr;
    //字段描述: 填报部门ID
    @Column(name = "TBBMID")
    private String tbbmid;
    //字段描述: 填报部门
    @Column(name = "TBBM")
    private String tbbm;
    //字段描述: 填报时间
    @Column(name = "TBSJ")
    private String tbsj;
    //字段描述: 隐患状态(0未消除1消除)
    @Column(name = "YHZT")
    private String yhzt;
    //字段描述: 经度
    @Column(name = "JD")
    private String jd;
    //字段描述: 纬度
    @Column(name = "WD")
    private String wd;
    //字段描述: 停电处理完成时间
    @Column(name = "TDCLWCSJ")
    private String tdclwcsj;
    //字段描述: 树木管控措施
    @Column(name = "SMGKCS")
    private String smgkcs;
    //字段描述: 隐患形成原因
    @Column(name = "YHXCYY")
    private String yhxcyy;
    //字段描述: 导线对隐患垂直距离
    @Column(name = "XDXYHJKJL")
    private String xdxyhjkjl;
    //字段描述: 导线对隐患水平距离
    @Column(name = "DXXYHCZJL")
    private String dxxyhczjl;
    //字段描述: 导线对隐患净空距离
    @Column(name = "DXDYHSPJL")
    private String dxdyhspjl;
    //字段描述: 关联线路id
    @Column(name = "LINE_ID")
    private String lineId;
    //字段描述: 0：pc 1：手机 2:excel导入
    @Column(name = "SDGS")
    private String sdgs;
    //字段描述: 更新时间
    @Column(name = "UPDATE_TIME")
    private String updateTime;
    //字段描述: 起始杆塔号
    @Column(name = "START_TOWER")
    private String startTower;
    //字段描述: 终止杆塔号
    @Column(name = "END_TOWER")
    private String endTower;
    //字段描述: 涉及线路名称
    @Column(name = "LINE_NAME")
    private String lineName;
    //字段描述: 设备维护单位
    @Column(name = "SBWH_ORG")
    private String sbywOrg;
    //字段描述: 电压等级
    @Column(name = "VTYPE")
    private String vtype;
    //字段描述: 照片修改时间
    @Column(name = "ZPXGSJ")
    private String zpxgsj;
    //字段描述: 数据创建时间
    @Column(name = "CREATE_TIME")
    private String createTime;
    //字段描述: 最新隐患信息id
    @Column(name = "YH_ID")
    private String yhId;

    public void setId(Long id){
        this.id =   Long.valueOf(new SnowflakeIdWorker(0,0).nextId());
    }
    @ExcelResources(title="",order=1)
    public Long getId(){
        return this.id;
    }

    public void setTdywOrgid(String tdywOrgid){
        this.tdywOrgid = tdywOrgid;
    }
    @ExcelResources(title="维护单位id（orgid）",order=2)
    public String getTdywOrgid(){
        return this.tdywOrgid;
    }

    public void setTdywOrg(String tdywOrg){
        this.tdywOrg = tdywOrg;
    }
    @ExcelResources(title="维护单位（通道单位）",order=3)
    public String getTdywOrg(){
        return this.tdywOrg;
    }

    public void setYhjb(String yhjb){
        this.yhjb = yhjb;
    }
    @ExcelResources(title="隐患级别",order=4)
    public String getYhjb(){
        return this.yhjb;
    }

    public void setYhdm(String yhdm){
        this.yhdm = yhdm;
    }
    @ExcelResources(title="隐患代码",order=5)
    public String getYhdm(){
        return this.yhdm;
    }

    public void setYhms(String yhms){
        this.yhms = yhms;
    }
    @ExcelResources(title="隐患描述",order=6)
    public String getYhms(){
        return this.yhms;
    }

    public void setYhtdqx(String yhtdqx){
        this.yhtdqx = yhtdqx;
    }
    @ExcelResources(title="隐患地点(区县)",order=7)
    public String getYhtdqx(){
        return this.yhtdqx;
    }

    public void setYhtdxzjd(String yhtdxzjd){
        this.yhtdxzjd = yhtdxzjd;
    }
    @ExcelResources(title="隐患地点(乡镇街道)",order=8)
    public String getYhtdxzjd(){
        return this.yhtdxzjd;
    }

    public void setYhtdc(String yhtdc){
        this.yhtdc = yhtdc;
    }
    @ExcelResources(title="隐患地点(村)",order=9)
    public String getYhtdc(){
        return this.yhtdc;
    }

    public void setYhzrdw(String yhzrdw){
        this.yhzrdw = yhzrdw;
    }
    @ExcelResources(title="隐患责任单位",order=10)
    public String getYhzrdw(){
        return this.yhzrdw;
    }

    public void setYhzrdwlxr(String yhzrdwlxr){
        this.yhzrdwlxr = yhzrdwlxr;
    }
    @ExcelResources(title="隐患责任单位联系人",order=11)
    public String getYhzrdwlxr(){
        return this.yhzrdwlxr;
    }

    public void setYhzrdwdh(String yhzrdwdh){
        this.yhzrdwdh = yhzrdwdh;
    }
    @ExcelResources(title="隐患责任单位电话",order=12)
    public String getYhzrdwdh(){
        return this.yhzrdwdh;
    }

    public void setYhzrdwsjzrbm(String yhzrdwsjzrbm){
        this.yhzrdwsjzrbm = yhzrdwsjzrbm;
    }
    @ExcelResources(title="隐患责任单位上级主管部门(空闲)",order=13)
    public String getYhzrdwsjzrbm(){
        return this.yhzrdwsjzrbm;
    }

    public void setYhddgddwid(String yhddgddwid){
        this.yhddgddwid = yhddgddwid;
    }
    @ExcelResources(title="taskid",order=14)
    public String getYhddgddwid(){
        return this.yhddgddwid;
    }

    public void setYhfxsj(String yhfxsj){
        this.yhfxsj = yhfxsj;
    }
    @ExcelResources(title="隐患发现时间",order=15)
    public String getYhfxsj(){
        return this.yhfxsj;
    }

    public void setDjsj(String djsj){
        this.djsj = djsj;
    }
    @ExcelResources(title="定级时间",order=16)
    public String getDjsj(){
        return this.djsj;
    }

    public void setDjyid(String djyid){
        this.djyid = djyid;
    }
    @ExcelResources(title="定级人ID",order=17)
    public String getDjyid(){
        return this.djyid;
    }

    public void setDjy(String djy){
        this.djy = djy;
    }
    @ExcelResources(title="定级人",order=18)
    public String getDjy(){
        return this.djy;
    }

    public void setYhxqTime(String yhxqTime){
        this.yhxqTime = yhxqTime;
    }
    @ExcelResources(title="隐患消除时间",order=19)
    public String getYhxqTime(){
        return this.yhxqTime;
    }

    public void setSspcs(String sspcs){
        this.sspcs = sspcs;
    }
    @ExcelResources(title="所属派出所",order=20)
    public String getSspcs(){
        return this.sspcs;
    }

    public void setPcsdh(String pcsdh){
        this.pcsdh = pcsdh;
    }
    @ExcelResources(title="派出所电话",order=21)
    public String getPcsdh(){
        return this.pcsdh;
    }

    public void setGkcs(String gkcs){
        this.gkcs = gkcs;
    }
    @ExcelResources(title="管控措施",order=22)
    public String getGkcs(){
        return this.gkcs;
    }

    public void setJfcs(String jfcs){
        this.jfcs = jfcs;
    }
    @ExcelResources(title="技防措施",order=23)
    public String getJfcs(){
        return this.jfcs;
    }

    public void setXcp(String xcp){
        this.xcp = xcp;
    }
    @ExcelResources(title="宣传牌",order=24)
    public String getXcp(){
        return this.xcp;
    }

    public void setYhdwlqk(String yhdwlqk){
        this.yhdwlqk = yhdwlqk;
    }
    @ExcelResources(title="隐患点外力情况",order=25)
    public String getYhdwlqk(){
        return this.yhdwlqk;
    }

    public void setHxxxyid(String hxxxyid){
        this.hxxxyid = hxxxyid;
    }
    @ExcelResources(title="护线信息员ID ",order=26)
    public String getHxxxyid(){
        return this.hxxxyid;
    }

    public void setHxxxy(String hxxxy){
        this.hxxxy = hxxxy;
    }
    @ExcelResources(title="护线信息员",order=27)
    public String getHxxxy(){
        return this.hxxxy;
    }

    public void setTbrid(String tbrid){
        this.tbrid = tbrid;
    }
    @ExcelResources(title="填报人ID",order=28)
    public String getTbrid(){
        return this.tbrid;
    }

    public void setTbr(String tbr){
        this.tbr = tbr;
    }
    @ExcelResources(title="填报人",order=29)
    public String getTbr(){
        return this.tbr;
    }

    public void setTbbmid(String tbbmid){
        this.tbbmid = tbbmid;
    }
    @ExcelResources(title="填报部门ID",order=30)
    public String getTbbmid(){
        return this.tbbmid;
    }

    public void setTbbm(String tbbm){
        this.tbbm = tbbm;
    }
    @ExcelResources(title="填报部门",order=31)
    public String getTbbm(){
        return this.tbbm;
    }

    public void setTbsj(String tbsj){
        this.tbsj = tbsj;
    }
    @ExcelResources(title="填报时间",order=32)
    public String getTbsj(){
        return this.tbsj;
    }

    public void setYhzt(String yhzt){
        this.yhzt = yhzt;
    }
    @ExcelResources(title="隐患状态(0未消除1消除)",order=33)
    public String getYhzt(){
        return this.yhzt;
    }

    public void setJd(String jd){
        this.jd = jd;
    }
    @ExcelResources(title="经度",order=34)
    public String getJd(){
        return this.jd;
    }

    public void setWd(String wd){
        this.wd = wd;
    }
    @ExcelResources(title="纬度",order=35)
    public String getWd(){
        return this.wd;
    }

    public void setTdclwcsj(String tdclwcsj){
        this.tdclwcsj = tdclwcsj;
    }
    @ExcelResources(title="停电处理完成时间",order=36)
    public String getTdclwcsj(){
        return this.tdclwcsj;
    }

    public void setSmgkcs(String smgkcs){
        this.smgkcs = smgkcs;
    }
    @ExcelResources(title="树木管控措施",order=37)
    public String getSmgkcs(){
        return this.smgkcs;
    }

    public void setYhxcyy(String yhxcyy){
        this.yhxcyy = yhxcyy;
    }
    @ExcelResources(title="隐患形成原因",order=38)
    public String getYhxcyy(){
        return this.yhxcyy;
    }

    public void setXdxyhjkjl(String xdxyhjkjl){
        this.xdxyhjkjl = xdxyhjkjl;
    }
    @ExcelResources(title="导线对隐患垂直距离",order=39)
    public String getXdxyhjkjl(){
        return this.xdxyhjkjl;
    }

    public void setDxxyhczjl(String dxxyhczjl){
        this.dxxyhczjl = dxxyhczjl;
    }
    @ExcelResources(title="导线对隐患水平距离",order=40)
    public String getDxxyhczjl(){
        return this.dxxyhczjl;
    }

    public void setDxdyhspjl(String dxdyhspjl){
        this.dxdyhspjl = dxdyhspjl;
    }
    @ExcelResources(title="导线对隐患净空距离",order=41)
    public String getDxdyhspjl(){
        return this.dxdyhspjl;
    }

    public void setLineId(String lineId){
        this.lineId = lineId;
    }
    @ExcelResources(title="关联线路id",order=42)
    public String getLineId(){
        return this.lineId;
    }

    public void setSdgs(String sdgs){
        this.sdgs = sdgs;
    }
    @ExcelResources(title="0：pc 1：手机 2:excel导入",order=43)
    public String getSdgs(){
        return this.sdgs;
    }

    public void setUpdateTime(String updateTime){
        this.updateTime = updateTime;
    }
    @ExcelResources(title="更新时间",order=44)
    public String getUpdateTime(){
        return this.updateTime;
    }

    public void setStartTower(String startTower){
        this.startTower = startTower;
    }
    @ExcelResources(title="起始杆塔号",order=45)
    public String getStartTower(){
        return this.startTower;
    }

    public void setEndTower(String endTower){
        this.endTower = endTower;
    }
    @ExcelResources(title="终止杆塔号",order=46)
    public String getEndTower(){
        return this.endTower;
    }

    public void setLineName(String lineName){
        this.lineName = lineName;
    }
    @ExcelResources(title="涉及线路名称",order=47)
    public String getLineName(){
        return this.lineName;
    }

    public void setSbywOrg(String sbywOrg){
        this.sbywOrg = sbywOrg;
    }
    @ExcelResources(title="设备维护单位",order=48)
    public String getSbywOrg(){
        return this.sbywOrg;
    }

    public void setVtype(String vtype){
        this.vtype = vtype;
    }
    @ExcelResources(title="电压等级",order=49)
    public String getVtype(){
        return this.vtype;
    }

    public void setZpxgsj(String zpxgsj){
        this.zpxgsj = zpxgsj;
    }
    @ExcelResources(title="照片修改时间",order=50)
    public String getZpxgsj(){
        return this.zpxgsj;
    }

    public void setCreateTime(String createTime){
        this.createTime = createTime;
    }
    @ExcelResources(title="数据创建时间",order=51)
    public String getCreateTime(){
        return this.createTime;
    }

    public void setYhId(String yhId){
        this.yhId = yhId;
    }
    @ExcelResources(title="最新隐患信息id",order=52)
    public String getYhId(){
        return this.yhId;
    }

}