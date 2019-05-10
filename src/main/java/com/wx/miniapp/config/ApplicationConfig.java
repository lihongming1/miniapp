package com.wx.miniapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfig {

    @Value("${wx.miniapp.appid}")
    public String appid;

    @Value("${wx.miniapp.secret}")
    public String secret;

    @Value("${login.redis.openid2skey}")
    public String openid2skey;

    @Value("${login.redis.skey2openid}")
    public String skey2openid;

    @Value("${login.redis.skey2sessionKey}")
    public String skey2sessionKey;


}
