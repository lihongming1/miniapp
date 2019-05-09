package com.wx.miniapp.service;

import com.wx.miniapp.dao.model.LoginUser;

public interface LoginUserService {

    int saveLoginUser(LoginUser user);

    int updateLoginUser(LoginUser user);

    LoginUser queryLoginUserByOpenId(String openId);

}
