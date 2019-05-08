package com.wx.miniapp.controller;

import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.alibaba.fastjson.JSON;
import com.wx.miniapp.controller.vo.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/com/wx/authorize")
public class AuthorizeController {

    @Autowired
    private WxMaUserService wxMaUserService;

    @PostMapping("login")
    public String login(@RequestParam LoginParam loginParam) {

        // 临时登录凭证code
        String code = loginParam.getCode();
        // 用户非敏感信息
        String rawData = loginParam.getRawData();
        // 签名
        String signature = loginParam.getSignature();
        // 用户敏感信息
        String encryptedData = loginParam.getEncryptedData();
        // 解密算法的向量
        String iv = loginParam.getIv();

        System.out.println("querySessionKey.code=" + code + ", encryptedData=" + encryptedData + ", iv=" + iv);

        WxMaJscode2SessionResult result = null;
        try {
            // 获取授权
            result = wxMaUserService.getSessionInfo(code);
            return JSON.toJSONString(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String openid = result.getOpenid();
        // 私钥
        String sessionKey = result.getSessionKey();

        // 获取用户
        WxMaUserInfo wxMaUserInfo = wxMaUserService.getUserInfo(sessionKey, encryptedData, iv);

        // 获取手机号
        WxMaPhoneNumberInfo wxMaPhoneNumberInfo = wxMaUserService.getPhoneNoInfo(sessionKey, encryptedData, iv);

        // insert mysql
        String skey = insertDB(openid, wxMaUserInfo, wxMaPhoneNumberInfo);

        // cache redis
        cacheRedis(openid, sessionKey, skey);

        return skey;
    }

    /**
     * 保存数据库
     *
     * @param openid
     * @param wxMaUserInfo
     * @param wxMaPhoneNumberInfo
     */
    public String insertDB(String openid, WxMaUserInfo wxMaUserInfo, WxMaPhoneNumberInfo wxMaPhoneNumberInfo) {
        return null;
    }

    /**
     * 缓存数据
     *
     * @param openid
     * @param sessionKey
     * @param skey
     */
    public void cacheRedis(String openid, String sessionKey, String skey) {
    }

}

