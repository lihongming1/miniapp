package com.wx.miniapp.dao;

import com.wx.miniapp.dao.model.LoginUser;
import org.apache.ibatis.annotations.Param;

public interface LoginUserDao {

    int saveLoginUser(LoginUser user);

    LoginUser queryLoginUserByOpenId(@Param("openId") String openId);

}
