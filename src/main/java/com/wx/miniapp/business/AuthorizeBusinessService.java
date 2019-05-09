package com.wx.miniapp.business;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.wx.miniapp.service.LoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorizeBusinessService {

    @Autowired
    private LoginUserService loginUserService;

    /**
     * 保存数据库
     *
     * @param session
     * @param wxMaUserInfo
     */
    public String saveLoginUser(WxMaJscode2SessionResult session, WxMaUserInfo wxMaUserInfo) {
        String skey = UUID.randomUUID().toString();
        loginUserService.saveLoginUser(null);
        return skey;
    }

}
