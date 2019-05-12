package com.wx.miniapp.controller;

import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.service.WxPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 支付
 */
@RestController
@RequestMapping(value = "/com/wx/pay")
public class PayController {

    @Autowired
    private WxPayService wxPayService;

    @PostMapping("unifiedOrder")
    public void unifiedOrder(){
        WxPayUnifiedOrderRequest request = null;
        try{
            // 设备号
            request.setDeviceInfo("");
            // 随机字符串
            request.setNonceStr("");
            // 签名
            request.setSign("");
            // 商品描述
            request.setBody("");
            // 商品详情
            request.setDetail("");
            // 附加数据
            request.setAttach("");
            // 商户订单号
            request.setOutTradeNo("");
            // 币种
            request.setFeeType("CNY");
            // 金额
            request.setTotalFee(100);
            // 终端IP
            request.setSpbillCreateIp("");
            // 交易起始时间
            request.setTimeStart("");
            // 交易结束时间
            request.setTimeExpire("");
            // 订单优惠标记
            request.setGoodsTag("");
            // 通知地址
            request.setNotifyUrl("");
            // 交易类型
            request.setTradeType("JSAPI");
            // 商品Id
            request.setProductId("");
            // 执行支付方式
            request.setLimitPay("no_credit");
            // 用户标识
            request.setOpenid("");
            // 场景信息
            request.setSceneInfo("");
            WxPayUnifiedOrderResult orderResult = wxPayService.unifiedOrder(request);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
