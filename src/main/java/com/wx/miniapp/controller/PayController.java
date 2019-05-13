package com.wx.miniapp.controller;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.util.SignUtils;
import com.wx.miniapp.common.util.SnowflakeIdWorker;
import com.wx.miniapp.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * 预支付
     */
    @GetMapping("/payment")
    public void payment(@RequestParam String skey, @RequestParam String productId) {

        String skey2openid = applicationConfig.skey2openid;
        Object openidObj = redisTemplate.opsForHash().get(skey2openid, skey);
        String openid = openidObj == null ? "" : openidObj.toString();
        if (StringUtils.isEmpty(openid)) {
            // 重新登陆
        }
        // 通过openid查询用户记录
        // 通过productId查询商品记录

        long globalUniqueId = snowflakeIdWorker.nextId();

        System.out.println(globalUniqueId);

        WxPayUnifiedOrderRequest request = null;
        try {

            // 用户 商品 => 订单 => 支付 => 回调

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateStr = sdf.format(new Date());

            InetAddress addr = InetAddress.getLocalHost();
            String ipStr = addr.getHostAddress();

            // 用户标识
            request.setOpenid(openid);
            // 商品ID
            request.setProductId(productId);
            // 设备号
            request.setDeviceInfo("");
            // 随机字符串
            request.setNonceStr("");
            // 商品描述
            request.setBody("商品描述test");
            // 商品详情
            request.setDetail("商品详情test");
            // 附加数据
            request.setAttach("");
            // 商户订单号
            request.setOutTradeNo(String.valueOf(globalUniqueId));
            // 币种
            request.setFeeType("CNY");
            // 金额
            request.setTotalFee(10);
            // 终端IP
            request.setSpbillCreateIp(ipStr);
            // 交易起始时间
            request.setTimeStart(dateStr);
            // 交易结束时间
            request.setTimeExpire(dateStr);
            // 订单优惠标记
            request.setGoodsTag("");
            // 交易类型
            request.setTradeType("JSAPI");
            // 执行支付方式
            request.setLimitPay("no_credit");
            // 场景信息
            request.setSceneInfo("");

            // 密钥
            String signKey = "";
            // 参数
            Map<String, String> params = new HashMap<>();
            String sign = SignUtils.createSign(params, "MD5",signKey,null);
            // 签名
            request.setSign("");


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
