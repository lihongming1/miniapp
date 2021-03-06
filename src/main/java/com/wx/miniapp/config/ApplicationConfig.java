package com.wx.miniapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfig {

    @Value("${wx.miniapp.appid}")
    public String appid;

    @Value("${wx.miniapp.secret}")
    public String secret;

    @Value("${login.redis.openid2skey}")
    public String openid2skey;

    @Value("${login.redis.skey2openid}")
    public String skey2openid;

    @Value("${login.redis.skey2sessionKey}")
    public String skey2sessionKey;

    @Value("${wx.pay.mchId}")
    public String mchId;

    @Value("${wx.pay.signType}")
    public String signType;

    @Value("${wx.pay.notifyUrl}")
    public String notifyUrl;

    @Value("${wx.pay.signKey}")
    public String signKey;

    @Value("${wx.pay.refund.notifyUrl}")
    public String refundNotifyUrl;

    @Value("${wx.miniapp.keyPath}")
    public String keyPath;

    @Value("${qiniu.access.key}")
    public String qiniuAccessKey;

    @Value("${qiniu.secret.key}")
    public String qiniuSecretkey;

    @Value("${qiniu.bucket.name}")
    public String qiniuBucketName;

    @Value("${qiniu.bucket.host}")
    public String qiniuBucketHost;


}
