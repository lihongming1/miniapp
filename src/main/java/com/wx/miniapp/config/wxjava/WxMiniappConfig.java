package com.wx.miniapp.config.wxjava;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.api.impl.WxMaMsgServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaUserServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.wx.miniapp.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WxMiniappConfig {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Bean
    public WxPayConfig wxPayConfig(){
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(applicationConfig.appid);
        wxPayConfig.setMchId(applicationConfig.mchId);
        wxPayConfig.setSignType(applicationConfig.signType);
        wxPayConfig.setNotifyUrl(applicationConfig.notifyUrl);
        wxPayConfig.setMchKey(applicationConfig.signKey);
        return wxPayConfig;
    }

    @Bean
    public WxPayService wxPayService(){
        WxPayService wxPayService = new WxPayServiceImpl();
        WxPayConfig wxPayConfig = wxPayConfig();
        wxPayService.setConfig(wxPayConfig);
        return wxPayService;
    }

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
