package com.rzt.constant;/**
 * Created by Administrator on 2017/7/7.
 */

/**
 * @param 参数：
 * @author 作者：hcy
 * @version V1.0.0
 * @method 方法名：EmailConst
 * @methodDesc 方法描述：
 * @return 返回值：
 * @description 描述：
 * @time 时间：2017-07-07 16:44
 */
public class ZzgdTaskConst {

    public static final String LOGIN_FAIL_MSG = "验证失败：邮箱地址或密码错误";
    public static final String LOGIN_QQMAIN_FAIL_MSG = "验证失败：邮箱地址或密码错误\n如果设置了独立密码，请输入独立密码";

    // 邮件正则验证
    public static final String REGEX_EMAIL_ADDRESS = "^[a-zA-Z0-9_]([\\.]?[a-zA-Z0-9_])*@([a-zA-Z0-9])+\\.(com|cn|net)$";

    // 邮件
    public static final String NETWORK_RECIEVE_POP3="pop3";
    public static final String NETWORK_RECIEVE_IMAP="IMAP";
}
