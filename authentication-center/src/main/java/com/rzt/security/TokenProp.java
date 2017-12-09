package com.rzt.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by 张虎成 on 2016/12/26.
 */
@Component
public class TokenProp {
    @Value("${token.expiretime}")
    private long expireTime;

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }



}
