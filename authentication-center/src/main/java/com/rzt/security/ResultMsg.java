package com.rzt.security;



import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * Created by 张虎成 on 2016/12/20.
 */
public class ResultMsg implements Serializable {
    private String errcode;
    private String errmsg;
    public ResultMsg(String errcode, String errmsg, Object o) {
        this.errcode=errcode;
        this.errmsg=errmsg;
    }
    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String toString() {
      return   JSONObject.toJSONString(this);
    }



}
