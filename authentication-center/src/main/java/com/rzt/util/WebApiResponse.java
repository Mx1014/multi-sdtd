package com.rzt.util;

public class WebApiResponse {
	private boolean success;
	private String error;
	private Object Data;

	public WebApiResponse() {
	}

	public boolean isSuccess() {
		return this.success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Object getData() {
		return this.Data;
	}

	public void setData(Object data) {
		this.Data = data;
	}

	public static WebApiResponse success(Object data) {
		WebApiResponse response = new WebApiResponse();
		response.setSuccess(true);
		response.setData(data);
		return response;
	}

	public static WebApiResponse erro(String errorMessage) {
		WebApiResponse response = new WebApiResponse();
		response.setSuccess(false);
		response.setData(errorMessage);
		response.setError(errorMessage);
		return response;
	}
}
