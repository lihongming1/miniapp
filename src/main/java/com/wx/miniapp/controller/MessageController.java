package com.wx.miniapp.controller;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateData;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.binarywang.wx.miniapp.bean.WxMaUniformMessage;
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

    /**
     * 消息发送
     * @param skey
     * @param formId
     * @return
     */
    @RequestMapping("/sendMsg")
    public ResponseEntity sendMsg(@RequestParam String skey, @RequestParam String formId){

        if("the formId is a mock one".equals(formId)){
            return ResponseEntity.status(-1).build();
        }

        String skey2openid = applicationConfig.skey2openid;

        Object openidobj = redisTemplate.opsForHash().get(skey2openid, skey);
        String openid = openidobj == null ? "" : openidobj.toString();

        WxMaTemplateMessage templateMessage = new WxMaTemplateMessage();
        templateMessage.setToUser(openid);
        templateMessage.setTemplateId("nH4kDo-gSSrUgd7dljmJr9eGNZq9R-C-ySM3I4ByEbQ");
        templateMessage.setFormId(formId);
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

    /**
     * 统一发送
     */
    @RequestMapping("/uniformSend")
    public ResponseEntity uniformSend(@RequestParam String skey, @RequestParam String formId){

        if("the formId is a mock one".equals(formId)){
            return ResponseEntity.status(-1).build();
        }

        String skey2openid = applicationConfig.skey2openid;

        Object openidobj = redisTemplate.opsForHash().get(skey2openid, skey);
        String openid = openidobj == null ? "" : openidobj.toString();

        WxMaUniformMessage uniformMessage = new WxMaUniformMessage();
        uniformMessage.setToUser(openid);
        uniformMessage.setFormId(formId);
        uniformMessage.setAppid(applicationConfig.appid);
        uniformMessage.setTemplateId("nH4kDo-gSSrUgd7dljmJr9eGNZq9R-C-ySM3I4ByEbQ");
        List<WxMaTemplateData> data = new ArrayList<>();
        WxMaTemplateData wxMaTemplateData = new WxMaTemplateData();
        wxMaTemplateData.setName("keyword1");
        wxMaTemplateData.setValue("123456");
        data.add(wxMaTemplateData);
        uniformMessage.setData(data);
        uniformMessage.setPage("pages/message/message");

//        private boolean isMpTemplateMsg;
//        private String url;
//        private WxMaUniformMessage.MiniProgram miniProgram;
//        private String emphasisKeyword;

        try{
            wxMaMsgService.sendUniformMsg(uniformMessage);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
