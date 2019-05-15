package com.wx.miniapp.controller;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
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
    @PostMapping("/payCallback")
    public void payCallback(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("payCallback.notityXml.into...");

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

            System.out.println("payCallback.notityXml=" + notityXml);

            WxPayOrderNotifyResult wxPayOrderNotifyResult = wxPayService.parseOrderNotifyResult(notityXml);

            System.out.println("payCallback.wxPayOrderNotifyResult=" + wxPayOrderNotifyResult == null ? "null" : JSON.toJSONString(wxPayOrderNotifyResult));

            if ("SUCCESS".equals(wxPayOrderNotifyResult.getResultCode())) {

                try {
                    // 验证返回结果，验证签名
                    wxPayOrderNotifyResult.checkResult(wxPayService, applicationConfig.signType, true);

                    // 商户订单号
                    String outTradeNo = wxPayOrderNotifyResult.getOutTradeNo();

                    System.out.println("payCallback.outTradeNo=" + outTradeNo);

                    // 更新支付状态
                    boolean callbackResult = payBusinessService.payCallback(outTradeNo);

                    System.out.println("payCallback.callbackResult=" + callbackResult);

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
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw ex;
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

    /**
     * 查询订单
     *
     * @param outTradeNo
     * @return
     */
    @GetMapping("/orderQuery")
    public ResponseEntity orderQuery(@RequestParam String outTradeNo) {

        try {
            WxPayOrderQueryResult result = wxPayService.queryOrder(null, outTradeNo);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ResponseEntity.status(-1).build();
    }

    /**
     * 申请退款
     * 参考：
     * https://blog.csdn.net/x1032019725/article/details/83376523
     * https://blog.csdn.net/maqingbin8888/article/details/83505771
     *
     * @return
     */
    @GetMapping("/refund")
    public ResponseEntity refund(@RequestParam String outTradeNo) {
        WxPayRefundRequest request = new WxPayRefundRequest();

        long globalUniqueId = snowflakeIdWorker.nextId();

        try {
            // 小程序ID
            request.setAppid(applicationConfig.appid);
            // 商户号
            request.setMchId(applicationConfig.mchId);
            // 随机字符串
            request.setNonceStr(UUID.randomUUID().toString().replaceAll("-", ""));
            // 签名类型
            request.setSignType(applicationConfig.signType);
            // 商户订单号
            request.setOutTradeNo(outTradeNo);
            // 商户退款单号
            request.setOutRefundNo(String.valueOf(globalUniqueId));
            // 订单金额
            request.setTotalFee(1);
            // 退款金额
            request.setRefundFee(1);
            // 货币种类
            request.setRefundFeeType("CNY");
            // 退款原因
            request.setRefundDesc("");
            // 退款资金来源
            request.setRefundAccount("");
            // 退款结果通知url
            request.setNotifyUrl(applicationConfig.refundNotifyUrl);
            WxPayRefundResult result = wxPayService.refund(request);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ResponseEntity.status(-1).build();
    }

    /**
     * 退款回调
     *
     * @param request
     * @param response
     */
    @PostMapping("/refundCallback")
    public void refundCallback(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("refundCallback.into...");

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

            WxPayRefundNotifyResult wxPayRefundNotifyResult = wxPayService.parseRefundNotifyResult(notityXml);

            try {
                // 验证返回结果，验证签名
                wxPayRefundNotifyResult.checkResult(wxPayService, applicationConfig.signType, true);
                WxPayRefundNotifyResult.ReqInfo reqInfo = wxPayRefundNotifyResult.getReqInfo();
                System.out.println("refundCallback.reqInfo=" + JSON.toJSONString(reqInfo));
                // 商户订单号
                String outTradeNo = reqInfo.getOutTradeNo();
                // 退款状态
                String refundStatus = reqInfo.getRefundStatus();
                // 修改退款状态
                // 返回微信段成功， 否则会一直询问 咱们服务器 是否回调成功
                StringBuffer buffer = new StringBuffer();
                buffer.append("<xml>");
                buffer.append("<return_code>SUCCESS</return_code>");
                buffer.append("<return_msg>OK</return_msg>");
                buffer.append("</xml>");
                //返回
                writer.print(buffer.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
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
