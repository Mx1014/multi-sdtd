/**
 * 文件名：KhYhHistory
 * 版本信息：
 * 日期：2017/11/30 18:31:34
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.entity;

import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名称：KhYhHistory
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/11/30 18:31:34
 * 修改人：张虎成
 * 修改时间：2017/11/30 18:31:34
 * 修改备注：
 * @version
 */
@Entity
@Table(name = "XS_SB_YH")
public class XsSbYh implements Serializable {
    //字段描述: 输电平台id
    @Id
    private Long id;

    //字段描述：区段
    @Column(name = "SECTION")
    private String section;

    //字段描述: 电压等级
    @Column(name = "VTYPE")
    private String vtype;

    //字段描述: 涉及线路名称
    @Column(name = "LINE_NAME")
    private String lineName;

    //字段描述: 关联线路id
    @Column(name = "LINE_ID")
    private long lineId;

    //字段描述: 起始杆塔号
    @Column(name = "START_TOWER")
    private String startTower;

    //字段描述: 终止杆塔号
    @Column(name = "END_TOWER")
    private String endTower;

    //字段描述: 线路重要程度
    @Column(name = "XLZYCD")
    private String xlzycd;

    //字段描述: 危急程度
    @Column(name = "YHJB")
    private String yhjb;

    //字段描述：隐患级别
    @Column(name = "YHJB1")
    private String yhjb1;

    //字段描述：隐患类别
    @Column(name = "YHLB")
    private String yhlb;

    //字段描述: 隐患形成原因
    @Column(name = "YHXCYY")
    private String yhxcyy;

    //字段描述: 隐患描述
    @Column(name = "YHMS")
    private String yhms;

    //字段描述: 隐患发现时间
    @Column(name = "YHFXSJ")
    private Date yhfxsj;
    //字段描述: 隐患地点(村)
    @Column(name = "YHTDC")
    private String yhtdc;
    //字段描述: 隐患地点(乡镇街道)
    @Column(name = "YHTDXZJD")
    private String yhtdxzjd;
    //字段描述: 隐患地点(区县)
    @Column(name = "YHTDQX")
    private String yhtdqx;
    //字段描述: 隐患责任单位电话
    @Column(name = "YHZRDWDH")
    private String yhzrdwdh;
    //字段描述: 隐患责任单位联系人
    @Column(name = "YHZRDWLXR")
    private String yhzrdwlxr;
    //字段描述: 隐患责任单位
    @Column(name = "YHZRDW")
    private String yhzrdw;
    //字段描述: 管控措施
    @Column(name = "GKCS")
    private String gkcs;
    //字段描述: 维护单位（通道单位）
    @Column(name = "TDYW_ORG")
    private String tdywOrg;
    //字段描述: 通道外协单位
    @Column(name = "TDWX_ORG")
    private String tdwxOrg;
    //字段描述: 设备维护单位
    @Column(name = "SBYW_ORG")
    private String sbywOrg;
    //字段描述: 是否定级
    @Column(name = "SFDJ")
    private int sfdj;
    //字段描述: 数据创建时间
    @Column(name = "CREATE_TIME")
    private Date createTime;
    //字段描述: 0：pc 1：手机 2:excel导入
    @Column(name = "SDGS")
    private int sdgs;
    //字段描述: 隐患状态(0未消除1消除)
    @Column(name = "YHZT")
    private int yhzt;
    //字段描述: 看护任务id
    @Column(name = "TASK_ID")
    private Long taskId;
    //字段描述: 更新时间
    @Column(name = "UPDATE_TIME")
    private Date updateTime;
    //字段描述: 导线对隐患水平距离
    @Column(name = "DXDYHSPJL")
    private String dxdyhspjl;
    //字段描述: 导线对隐患垂直距离
    @Column(name = "DXXYHCZJL")
    private String dxxyhczjl;
    //字段描述: 导线对隐患净空距离
    @Column(name = "XDXYHJKJL")
    private String xdxyhjkjl;
    //字段描述: 纬度
    @Column(name = "WD")
    private String wd;
    //字段描述: 经度
    @Column(name = "JD")
    private String jd;
    //字段描述: 填报时间
    @Column(name = "TBSJ")
    private Date tbsj;
    //字段描述: 填报人ID
    @Column(name = "TBRID")
    private String tbrid;
    //字段描述: 隐患消除时间
    @Column(name = "YHXQ_TIME")
    private String yhxqTime;
    //字段描述: 定级人ID
    @Column(name = "DJYID")
    private String djyid;
    //字段描述: 施工情况
    @Column(name = "SGQK")
    private int sgqk;
    //字段描述：通道单位id
    @Column(name = "YWORG_ID")
    private String tdorgId;
    //字段描述: 外协单位id
    @Column(name = "WXORG_ID")
    private String wxorgId;
    //字段描述: 隐患点半径
    @Column(name = "radius")
    private String radius;
    //字段描述: 隐患点半径
    @Column(name = "XSTASK_ID")
    private long xstaskId;
    //字段描述: 所属队伍
    @Column(name = "CLASSNAME")
    private String className;
    //字段描述: 树木数
    @Column(name = "SMS")
    private String sms;
    //字段描述: 是否悬挂警示牌
    @Column(name = "JSP")
    private String jsp;
    //字段描述: 涉及线路
    @Column(name = "SJXL")
    private String sjxl;
    //字段描述: 班组id
    @Column(name = "CLASS_ID")
    private String classId;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getJsp() {
        return jsp;
    }

    public void setJsp(String jsp) {
        this.jsp = jsp;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @ExcelResources(title = "数据创建时间", order = 1)
    public Date getCreateTime() {
        return this.createTime;
    }


    public void setVtype(String vtype) {
        this.vtype = vtype;
    }

    @ExcelResources(title = "电压等级", order = 3)
    public String getVtype() {
        return this.vtype;
    }

    public void setSbywOrg(String sbywOrg) {
        this.sbywOrg = sbywOrg;
    }

    @ExcelResources(title = "设备维护单位", order = 4)
    public String getSbywOrg() {
        return this.sbywOrg;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    @ExcelResources(title = "涉及线路名称", order = 5)
    public String getLineName() {
        return this.lineName;
    }

    public void setEndTower(String endTower) {
        this.endTower = endTower;
    }

    @ExcelResources(title = "终止杆塔号", order = 6)
    public String getEndTower() {
        return this.endTower;
    }

    public void setStartTower(String startTower) {
        this.startTower = startTower;
    }

    @ExcelResources(title = "起始杆塔号", order = 7)
    public String getStartTower() {
        return this.startTower;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @ExcelResources(title = "更新时间", order = 8)
    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setSdgs(int sdgs) {
        this.sdgs = sdgs;
    }

    @ExcelResources(title = "0：pc 1：手机 2:excel导入", order = 9)
    public int getSdgs() {
        return this.sdgs;
    }

    public void setLineId(long lineId) {
        this.lineId = lineId;
    }

    @ExcelResources(title = "关联线路id", order = 10)
    public long getLineId() {
        return this.lineId;
    }

    public void setDxdyhspjl(String dxdyhspjl) {
        this.dxdyhspjl = dxdyhspjl;
    }

    @ExcelResources(title = "导线对隐患净空距离", order = 11)
    public String getDxdyhspjl() {
        return this.dxdyhspjl;
    }

    public void setDxxyhczjl(String dxxyhczjl) {
        this.dxxyhczjl = dxxyhczjl;
    }

    @ExcelResources(title = "导线对隐患水平距离", order = 12)
    public String getDxxyhczjl() {
        return this.dxxyhczjl;
    }

    public void setXdxyhjkjl(String xdxyhjkjl) {
        this.xdxyhjkjl = xdxyhjkjl;
    }

    @ExcelResources(title = "导线对隐患垂直距离", order = 13)
    public String getXdxyhjkjl() {
        return this.xdxyhjkjl;
    }

    public void setYhxcyy(String yhxcyy) {
        this.yhxcyy = yhxcyy;
    }

    @ExcelResources(title = "隐患形成原因", order = 14)
    public String getYhxcyy() {
        return this.yhxcyy;
    }

    public void setWd(String wd) {
        this.wd = wd;
    }

    @ExcelResources(title = "纬度", order = 17)
    public String getWd() {
        return this.wd;
    }

    public void setJd(String jd) {
        this.jd = jd;
    }

    @ExcelResources(title = "经度", order = 18)
    public String getJd() {
        return this.jd;
    }

    public void setYhzt(int yhzt) {
        this.yhzt = yhzt;
    }

    @ExcelResources(title = "隐患状态(0未消除1消除)", order = 19)
    public int getYhzt() {
        return this.yhzt;
    }

    public void setTbsj(Date tbsj) {
        this.tbsj = tbsj;
    }

    @ExcelResources(title = "填报时间", order = 20)
    public Date getTbsj() {
        return this.tbsj;
    }


    public void setTbrid(String tbrid) {
        this.tbrid = tbrid;
    }

    @ExcelResources(title = "填报人ID", order = 24)
    public String getTbrid() {
        return this.tbrid;
    }


    public void setGkcs(String gkcs) {
        this.gkcs = gkcs;
    }

    @ExcelResources(title = "管控措施", order = 30)
    public String getGkcs() {
        return this.gkcs;
    }


    public void setYhxqTime(String yhxqTime) {
        this.yhxqTime = yhxqTime;
    }

    @ExcelResources(title = "隐患消除时间", order = 33)
    public String getYhxqTime() {
        return this.yhxqTime;
    }


    public void setDjyid(String djyid) {
        this.djyid = djyid;
    }

    @ExcelResources(title = "定级人ID", order = 35)
    public String getDjyid() {
        return this.djyid;
    }

    public void setYhfxsj(Date yhfxsj) {
        this.yhfxsj = yhfxsj;
    }

    @ExcelResources(title = "隐患发现时间", order = 37)
    public Date getYhfxsj() {
        return this.yhfxsj;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    @ExcelResources(title = "看护任务id", order = 38)
    public Long getTaskId() {
        return this.taskId;
    }

    public void setYhzrdwdh(String yhzrdwdh) {
        this.yhzrdwdh = yhzrdwdh;
    }

    @ExcelResources(title = "隐患责任单位电话", order = 40)
    public String getYhzrdwdh() {
        return this.yhzrdwdh;
    }

    public void setYhzrdwlxr(String yhzrdwlxr) {
        this.yhzrdwlxr = yhzrdwlxr;
    }

    @ExcelResources(title = "隐患责任单位联系人", order = 41)
    public String getYhzrdwlxr() {
        return this.yhzrdwlxr;
    }

    public void setYhzrdw(String yhzrdw) {
        this.yhzrdw = yhzrdw;
    }

    @ExcelResources(title = "隐患责任单位", order = 42)
    public String getYhzrdw() {
        return this.yhzrdw;
    }

    public void setYhtdc(String yhtdc) {
        this.yhtdc = yhtdc;
    }

    @ExcelResources(title = "隐患地点(村)", order = 43)
    public String getYhtdc() {
        return this.yhtdc;
    }

    public void setYhtdxzjd(String yhtdxzjd) {
        this.yhtdxzjd = yhtdxzjd;
    }

    @ExcelResources(title = "隐患地点(乡镇街道)", order = 44)
    public String getYhtdxzjd() {
        return this.yhtdxzjd;
    }

    public void setYhtdqx(String yhtdqx) {
        this.yhtdqx = yhtdqx;
    }


    @ExcelResources(title = "隐患地点(区县)", order = 45)
    public String getYhtdqx() {
        return this.yhtdqx;
    }

    public void setYhms(String yhms) {
        this.yhms = yhms;
    }

    @ExcelResources(title = "隐患描述", order = 46)
    public String getYhms() {
        return this.yhms;
    }

    public void setYhjb(String yhjb) {
        this.yhjb = yhjb;
    }

    @ExcelResources(title = "危机程度", order = 48)
    public String getYhjb() {
        return this.yhjb;
    }

    public void setTdywOrg(String tdywOrg) {
        this.tdywOrg = tdywOrg;
    }

    @ExcelResources(title = "维护单位（通道单位）", order = 49)
    public String getTdywOrg() {
        return this.tdywOrg;
    }

    public void setTdwxOrg(String tdwxOrg) {
        this.tdwxOrg = tdwxOrg;
    }

    @ExcelResources(title = "维护单位id（orgid）", order = 50)
    public String getTdwxOrg() {
        return this.tdwxOrg;
    }

    public void setId(Long id){
        if(id==null||id==0){
            SnowflakeIdWorker instance = SnowflakeIdWorker.getInstance(8, 21);
            this.id = instance.nextId();
        }else{
            this.id = id;
        }
    }


    @ExcelResources(title = "输电平台id", order = 51)
    public Long getId() {
        return this.id;
    }

    public void setXlzycd(String xlzycd) {
        this.xlzycd = xlzycd;
    }

    public String getXlzycd() {
        return xlzycd;
    }

    public int getSfdj() {
        return sfdj;
    }

    public void setSfdj(int SFDJ) {
        this.sfdj = SFDJ;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getYhjb1() {
        return yhjb1;
    }

    public void setYhjb1(String yhjb1) {
        this.yhjb1 = yhjb1;
    }

    public String getYhlb() {
        return yhlb;
    }

    public void setYhlb(String yhlb) {
        this.yhlb = yhlb;
    }

    public int getSgqk() {
        return sgqk;
    }

    public void setSgqk(int sgqk) {
        this.sgqk = sgqk;
    }

    public String getTdorgId() {
        return tdorgId;
    }

    public void setTdorgId(String tdorgId) {
        this.tdorgId = tdorgId;
    }

    public String getWxorgId() {
        return wxorgId;
    }

    public void setWxorgId(String wxorgId) {
        this.wxorgId = wxorgId;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getRadius() {
        return radius;
    }

    public void setXstaskId(long xstaskId) {
        this.xstaskId = xstaskId;
    }

    public long getXstaskId() {
        return xstaskId;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setSjxl(String sjxl) {
        this.sjxl = sjxl;
    }

    public String getSjxl() {
        return sjxl;
    }
}