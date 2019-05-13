package com.wx.miniapp.controller;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.service.WxPayService;
import com.wx.miniapp.common.util.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * 支付
 * 参考：
 * https://blog.csdn.net/qq_37105358/article/details/81285779
 * https://developers.weixin.qq.com/miniprogram/dev/api/wx.requestPayment.html
 * https://www.cnblogs.com/yi1036943655/p/7211275.html
 */
@RestController
@RequestMapping(value = "/com/wx/pay")
public class PayController {

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 预支付
     */
    @PostMapping("payment")
    public void payment() {

        long globalUniqueId = snowflakeIdWorker.nextId();

        System.out.println(globalUniqueId);

        WxPayUnifiedOrderRequest request = null;
        try {
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 支付回调
     *
     * @param request
     * @param response
     */
    public void payCallback(HttpServletRequest request, HttpServletResponse response) {
        String inputLine = "";
        String notityXml = "";
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = request.getReader();
            writer = response.getWriter();

            while ((inputLine = reader.readLine()) != null) {
                notityXml += inputLine;
            }

            WxPayOrderNotifyResult wxPayOrderNotifyResult = wxPayService.parseOrderNotifyResult(notityXml);
            if ("SUCCESS".equals(wxPayOrderNotifyResult.getResultCode())) {
                // 商户订单号
                String outTradeNo = wxPayOrderNotifyResult.getOutTradeNo();
                // 查询数据库
                // 修改订单状态-已支付
                // 查询是否更新成功
                // 返回微信段成功， 否则会一直询问 咱们服务器 是否回调成功
                StringBuffer buffer = new StringBuffer();
                buffer.append("<xml>");
                buffer.append("<return_code>SUCCESS</return_code>");
                buffer.append("<return_msg>OK</return_msg>");
                buffer.append("</xml>");
                //返回
                writer.print(buffer.toString());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    ;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                    ;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
