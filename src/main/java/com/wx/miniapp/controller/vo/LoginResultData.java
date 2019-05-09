package com.wx.miniapp.controller.vo;

import java.io.Serializable;

public class LoginResultData implements Serializable {

    private String skey;

    private LoginUserInfo userInfo;

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public LoginUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(LoginUserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
