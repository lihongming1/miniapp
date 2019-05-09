package com.wx.miniapp.controller.vo;

import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;

import java.io.Serializable;

public class LoginResultData implements Serializable {

    private String skey;

    private WxMaUserInfo userInfo;

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public WxMaUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(WxMaUserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
