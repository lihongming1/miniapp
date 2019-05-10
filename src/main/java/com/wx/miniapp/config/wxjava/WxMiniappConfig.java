package com.wx.miniapp.config.wxjava;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.api.impl.WxMaMsgServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaUserServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import com.wx.miniapp.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WxMiniappConfig {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Bean
    public WxMaService wxMaService() {
        WxMaService wxMaService = new WxMaServiceImpl();
        WxMaInMemoryConfig wxMaConfig = new WxMaInMemoryConfig();
        wxMaConfig.setAppid(applicationConfig.appid);
        wxMaConfig.setSecret(applicationConfig.secret);
        wxMaService.setWxMaConfig(wxMaConfig);
        return wxMaService;
    }

    @Bean
    public WxMaUserService wxMaUserService() {
        WxMaService wxMaService = wxMaService();
        WxMaUserService wxMaUserService = new WxMaUserServiceImpl(wxMaService);
        return wxMaUserService;
    }

    @Bean
    public WxMaMsgService wxMaMsgService() {
        WxMaService wxMaService = wxMaService();
        WxMaMsgService wxMaMsgService = new WxMaMsgServiceImpl(wxMaService);
        return wxMaMsgService;
    }

}