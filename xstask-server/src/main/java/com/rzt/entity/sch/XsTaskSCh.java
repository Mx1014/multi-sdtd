package com.rzt.entity.sch;

import java.io.Serializable;
public class XsTaskSCh implements Serializable {
    private String startDate;
    private String endDate;
    private Integer status;
    private Integer v_type;
    private Integer lineId;
    private String lineName;
    private Integer ispf;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getIspf() {
        return ispf;
    }

    public void setIspf(Integer ispf) {
        this.ispf = ispf;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getV_type() {
        return v_type;
    }

    public void setV_type(Integer v_type) {
        this.v_type = v_type;
    }

    public Integer getLineId() {
        return lineId;
    }

    public void setLineId(Integer lineId) {
        this.lineId = lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }
}
