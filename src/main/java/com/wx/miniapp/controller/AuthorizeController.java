package com.wx.miniapp.controller;

import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.wx.miniapp.business.AuthorizeBusinessService;
import com.wx.miniapp.config.ApplicationConfig;
import com.wx.miniapp.controller.vo.LoginParam;
import com.wx.miniapp.controller.vo.LoginResultData;
import com.wx.miniapp.controller.vo.LoginUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 登录授权
 * 参考：https://blog.51cto.com/sihai/2145116
 */
@RestController
@RequestMapping(value = "/com/wx/authorize")
public class AuthorizeController {

    @Autowired
    private WxMaUserService wxMaUserService;

    @Autowired
    private AuthorizeBusinessService authorizeBusinessService;

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private ApplicationConfig applicationConfig;


    @PostMapping("login")
    public ResponseEntity login(@RequestBody LoginParam loginParam) {

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

        WxMaJscode2SessionResult session = null;
        try {
            // 获取授权
            session = wxMaUserService.getSessionInfo(code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String openid = session.getOpenid();
        // 私钥
        String sessionKey = session.getSessionKey();

        // 获取用户
        WxMaUserInfo wxMaUserInfo = wxMaUserService.getUserInfo(sessionKey, encryptedData, iv);

        // 生产skey
        String skey = UUID.randomUUID().toString();

        // insert mysql
//        authorizeBusinessService.saveLoginUser(session, wxMaUserInfo);

        // cache redis
        cacheRedis(openid, sessionKey, skey);

        LoginResultData data = new LoginResultData();
        data.setSkey(skey);
        LoginUserInfo userInfo = new LoginUserInfo();
        userInfo.setNickName(wxMaUserInfo.getNickName());
        userInfo.setGender(wxMaUserInfo.getGender());
        userInfo.setLanguage(wxMaUserInfo.getLanguage());
        userInfo.setCity(wxMaUserInfo.getCity());
        userInfo.setProvince(wxMaUserInfo.getProvince());
        userInfo.setCountry(wxMaUserInfo.getCountry());
        userInfo.setAvatarUrl(wxMaUserInfo.getAvatarUrl());
        data.setUserInfo(userInfo);

        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    /**
     * 缓存数据
     *
     * @param openid
     * @param sessionKey
     * @param skey
     */
    public void cacheRedis(String openid, String sessionKey, String skey) {

        String openid2skey = applicationConfig.openid2skey;
        String skey2openid = applicationConfig.skey2openid;
        String skey2sessionKey = applicationConfig.skey2sessionKey;

        // openid -> skey
        Object skeyObj = redisTemplate.opsForHash().get(openid2skey, openid);
        String sky = skeyObj == null ? null : skeyObj.toString();
        if(!StringUtils.isEmpty(sky)){
            // 删除缓存
            redisTemplate.opsForHash().delete(openid2skey, openid);
            redisTemplate.opsForHash().delete(skey2openid, skey);
            redisTemplate.opsForHash().delete(skey2sessionKey, skey);
        }


        // openid -> skey
        redisTemplate.opsForHash().put(openid2skey, openid, skey);
        redisTemplate.expire(openid2skey, 5, TimeUnit.DAYS); //设置5天过期
        // skey -> openid
        redisTemplate.opsForHash().put(skey2openid, skey, openid);
        redisTemplate.expire(skey2openid, 5, TimeUnit.DAYS); //设置5天过期
        // skey -> sessionKey
        redisTemplate.opsForHash().put(skey2sessionKey, skey, sessionKey);
        redisTemplate.expire(skey2sessionKey, 5, TimeUnit.DAYS); //设置5天过期

    }

}

