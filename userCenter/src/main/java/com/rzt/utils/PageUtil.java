package com.rzt.utils;

/**
 * Created by Administrator on 2017/11/2.
 */
public class PageUtil {
	public static String getLimit(int page,int size){
		int start = size * page;
		return " limit " + start + "," + size;
	}
}
