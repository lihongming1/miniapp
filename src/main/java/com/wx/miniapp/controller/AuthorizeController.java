package com.wx.miniapp.controller;

import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/com/wx/authorize")
public class AuthorizeController {

    @Autowired
    private WxMaUserService wxMaUserService;

    @GetMapping(value = "/authorize")
    public String querySessionKey(@RequestParam String code) {
        System.out.println("querySessionKey.code=" + code);
        try {
            WxMaJscode2SessionResult result = wxMaUserService.getSessionInfo(code);
            return JSON.toJSONString(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "OK";
    }

}

