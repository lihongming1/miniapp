package com.wx.miniapp.business;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.wx.miniapp.dao.model.LoginUser;
import com.wx.miniapp.service.LoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public int saveLoginUser(WxMaJscode2SessionResult session, WxMaUserInfo wxMaUserInfo) {
        LoginUser loginUser = loginUserService.queryLoginUserByOpenId(session.getOpenid());
        if(loginUser == null){
            // save
            LoginUser user = new LoginUser();
            user.setOpenId(session.getOpenid());
            user.setNickName(wxMaUserInfo.getNickName());
            user.setGender(wxMaUserInfo.getGender());
            user.setLanguage(wxMaUserInfo.getLanguage());
            user.setCity(wxMaUserInfo.getCity());
            user.setProvince(wxMaUserInfo.getProvince());
            user.setCountry(wxMaUserInfo.getCountry());
            user.setAvatarUrl(wxMaUserInfo.getAvatarUrl());
            user.setUnionId(wxMaUserInfo.getUnionId() == null ? "" : wxMaUserInfo.getUnionId());
            user.setSessionKey(session.getSessionKey());
            user.setPhoneNumber("");
            user.setPurePhoneNumber("");
            user.setCountryCode("");
            loginUserService.saveLoginUser(user);
        }else{
            // update
            loginUser.setNickName(wxMaUserInfo.getNickName());
            loginUser.setGender(wxMaUserInfo.getGender());
            loginUser.setProvince(wxMaUserInfo.getProvince());
            loginUser.setCountry(wxMaUserInfo.getCountry());
            loginUser.setAvatarUrl(wxMaUserInfo.getAvatarUrl());
            loginUser.setUpdateDate(new Date());
            loginUserService.updateLoginUser(loginUser);
        }
        return 0;
    }

}
