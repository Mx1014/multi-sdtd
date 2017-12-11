package com.rzt.filter;

/**
 * 类名称：druidStatFilter
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/09/21 17:14:13
 * 修改人：张虎成
 * 修改时间：2017/09/21 17:14:13
 * 修改备注：
 * @version
 */
import com.alibaba.druid.support.http.WebStatFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/**
 * Created by qianlong on 16/12/21.
 */
@WebFilter(filterName="druidStatFilter",urlPatterns="/*",
    initParams={
        @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")// 忽略资源
    })
public class DruidStatFilter extends WebStatFilter {

}