package com.rzt.util;

/**
 * Created by 张虎成 on 2016/12/23.
 */
public class WebApiResponse<T> {

    private  boolean success;
    private  String error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

    private  T Data;

    public static <T> WebApiResponse<T> success(T data){
        WebApiResponse<T> response = new WebApiResponse<T>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static <T> WebApiResponse<T> erro(T data){
        WebApiResponse<T> response = new WebApiResponse<T>();
        response.setSuccess(false);
        response.setData(data);
        return response;
    }
}
