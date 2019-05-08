package com.wx.miniapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfig {

    @Value("${wx.miniapp.appid}")
    public String appid;

    @Value("${wx.miniapp.secret}")
    public String secret;



}
