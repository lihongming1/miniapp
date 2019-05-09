package com.wx.miniapp.service.impl;

import com.wx.miniapp.dao.LoginUserDao;
import com.wx.miniapp.dao.model.LoginUser;
import com.wx.miniapp.service.LoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginUserServiceImpl implements LoginUserService {

    @Autowired
    private LoginUserDao loginUserDao;

    @Override
    public int saveLoginUser(LoginUser user) {
        return loginUserDao.saveLoginUser(user);
    }
}
