package com.rzt.security;

/**
 * Created by 张虎成 on 2016/12/20.
 */
public enum ResultStatusCode {

     NO_TOKEN("3001", "没有明确的Token信息"),
    INVALID_TOKEN("3002", "无效的token信息"),
    TOKEN_EXPIRES("3003","token信息过期"),
    REACT_TOKEN("3004","正确token信息"),
    NO_AUTH("3005","没有访问权限");

    ResultStatusCode(String errorcode, String errmsg ) {
        this.errocode=errorcode;
        this.errmsg=errmsg;
    }

    private String errocode;

    private String  errmsg;


    public String getErrocode() {
        return errocode;
    }

    public void setErrocode(String errocode) {
        this.errocode = errocode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }







        }