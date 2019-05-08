package com.wx.miniapp.config.authorize;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaUserServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import com.wx.miniapp.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WxMaUserConfig {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Bean
    public WxMaUserService wxMaUserService(){
        WxMaService wxMaService = new WxMaServiceImpl();
        WxMaInMemoryConfig wxMaConfig = new WxMaInMemoryConfig();
        wxMaConfig.setAppid(applicationConfig.appid);
        wxMaConfig.setSecret(applicationConfig.secret);
        wxMaService.setWxMaConfig(wxMaConfig);
        WxMaUserService wxMaUserService = new WxMaUserServiceImpl(wxMaService);
        return wxMaUserService;
    }


}
