package com.wx.miniapp.controller;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateData;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import com.google.common.collect.Lists;
import com.wx.miniapp.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息模版
 */
@RestController
@RequestMapping(value = "/com/wx/message")
public class MessageController {

    @Autowired
    private WxMaMsgService wxMaMsgService;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Resource
    private RedisTemplate redisTemplate;

    @RequestMapping("/sendMsg")
    public ResponseEntity sendMsg(@RequestParam String skey){

        String openid2skey = applicationConfig.openid2skey;
        String skey2openid = applicationConfig.skey2openid;
        String skey2sessionKey = applicationConfig.skey2sessionKey;



        WxMaTemplateMessage templateMessage = new WxMaTemplateMessage();
        templateMessage.setToUser(applicationConfig.appid);
        templateMessage.setTemplateId("nH4kDo-gSSrUgd7dljmJr9eGNZq9R-C-ySM3I4ByEbQ");
        templateMessage.setFormId("the formId is a mock one");
        List<WxMaTemplateData> data = new ArrayList<>();
        WxMaTemplateData wxMaTemplateData = new WxMaTemplateData();
        wxMaTemplateData.setName("keyword1");
        wxMaTemplateData.setValue("123456");
        data.add(wxMaTemplateData);
        templateMessage.setData(data);

        try{
            wxMaMsgService.sendTemplateMsg(templateMessage);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
