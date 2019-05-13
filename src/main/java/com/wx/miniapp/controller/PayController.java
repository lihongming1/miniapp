package com.wx.miniapp.controller;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.util.SignUtils;
import com.wx.miniapp.business.PayBusinessService;
import com.wx.miniapp.common.util.SnowflakeIdWorker;
import com.wx.miniapp.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private ApplicationConfig applicationConfig;

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private PayBusinessService payBusinessService;

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 预支付
     */
    @GetMapping("/payment")
    public ResponseEntity payment(@RequestParam String skey, @RequestParam String formId, @RequestParam String productId) {

        String skey2openid = applicationConfig.skey2openid;
        Object openidObj = redisTemplate.opsForHash().get(skey2openid, skey);
        String openid = openidObj == null ? "" : openidObj.toString();
        if (StringUtils.isEmpty(openid)) {
            // 重新登陆
            return ResponseEntity.status(-1).build();
        }
        // 通过openid查询用户记录
        // 通过productId查询商品记录

        long globalUniqueId = snowflakeIdWorker.nextId();

        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
        try {

            // 用户标识
            request.setOpenid(openid);
            // 商品ID
            request.setProductId(productId);
            // 设备号
            request.setDeviceInfo("");
            // 随机字符串
            request.setNonceStr(UUID.randomUUID().toString().replaceAll("-", ""));
            // 商品描述
            request.setBody("商品描述test");
            // 商品详情
            request.setDetail("");
            // 附加数据
            request.setAttach("");
            // 商户订单号
            request.setOutTradeNo(String.valueOf(globalUniqueId));
            // 币种
            request.setFeeType("CNY");
            // 金额
            request.setTotalFee(1);
            // 终端IP
            InetAddress addr = InetAddress.getLocalHost();
            String ipStr = addr.getHostAddress();
            request.setSpbillCreateIp(ipStr);
            // 交易起始时间
            request.setTimeStart(sdf.format(new Date()));
            // 交易结束时间+30M
            Calendar nowTime = Calendar.getInstance();
            nowTime.add(Calendar.MINUTE, 30);
            request.setTimeExpire(sdf.format(nowTime.getTime()));
            // 订单优惠标记
            request.setGoodsTag("");
            // 交易类型
            request.setTradeType("JSAPI");
            // 执行支付方式
            request.setLimitPay("no_credit");
            // 场景信息
            request.setSceneInfo("");

            WxPayUnifiedOrderResult orderResult = wxPayService.unifiedOrder(request);

            if ("SUCCESS".equals(orderResult.getReturnCode()) && orderResult.getReturnCode().equals(orderResult.getResultCode())) {
                // 二次签名
                Map<String, String> data = new HashMap<>();
                data.put("appId", applicationConfig.appid);
                data.put("timeStamp", System.currentTimeMillis() / 1000 + "");
                data.put("nonceStr", UUID.randomUUID().toString().replaceAll("-", ""));
                data.put("package", "prepay_id=" + orderResult.getPrepayId());
                data.put("signType", "MD5");
                String sign = SignUtils.createSign(data, "MD5", applicationConfig.signKey, null);
                data.put("paySign", sign);
                return ResponseEntity.status(HttpStatus.OK).body(data);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ResponseEntity.status(-1).build();
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
                Map<String, String> params = wxPayOrderNotifyResult.toMap();
                // 签名类型
                String signType = applicationConfig.signType;
                // 校验签名是否正确
                boolean check = SignUtils.checkSign(params, signType, null);
                if (check) {
                    // 商户订单号
                    String outTradeNo = wxPayOrderNotifyResult.getOutTradeNo();
                    // 更新支付状态
                    boolean callbackResult = payBusinessService.payCallback(outTradeNo);
                    if (callbackResult) {
                        // 返回微信段成功， 否则会一直询问 咱们服务器 是否回调成功
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("<xml>");
                        buffer.append("<return_code>SUCCESS</return_code>");
                        buffer.append("<return_msg>OK</return_msg>");
                        buffer.append("</xml>");
                        //返回
                        writer.print(buffer.toString());
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
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
