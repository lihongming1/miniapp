package com.wx.miniapp.controller;

import com.github.binarywang.wxpay.service.WxPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付
 */
@RestController
@RequestMapping(value = "/com/wx/pay")
public class PayController {

    @Autowired
    private WxPayService wxPayService;

}
