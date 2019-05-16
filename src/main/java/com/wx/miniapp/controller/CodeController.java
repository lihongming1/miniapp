package com.wx.miniapp.controller;

import cn.binarywang.wx.miniapp.api.WxMaQrcodeService;
import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import com.alibaba.fastjson.JSON;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.wx.miniapp.common.util.qiniu.UploadConfig;
import com.wx.miniapp.common.util.qiniu.UploadExecutor;
import com.wx.miniapp.common.util.qiniu.UploadUtil;
import com.wx.miniapp.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * 小程序二维码
 */
@RestController
@RequestMapping(value = "/com/wx/code")
public class CodeController {

    @Autowired
    private WxMaQrcodeService wxMaQrcodeService;

    @Autowired
    private ApplicationConfig applicationConfig;

    /**
     * 生产二维码
     *
     * @param scene 最大32个可见字符，只支持数字，大小写英文以及部分特殊字符
     */
    @GetMapping("/createCode")
    public void createCode(@RequestParam String scene, HttpServletResponse response) {

        // 主页: 未发布的小程序page不给，可以生成图片
        String page = "";
        // 二维码的宽度
        int width = 430;
        // 二维码的宽度
        boolean autoColor = false;
        // uto_color 为 false 时生效，使用 rgb 设置颜色
        WxMaCodeLineColor lineColor = new WxMaCodeLineColor("0", "0", "0");
        // 是否需要透明底色，为 true 时，生成透明底色的小程序
        boolean isHyaline = true;

        byte[] bytes = null;
        try {
            bytes = wxMaQrcodeService.createWxaCodeUnlimitBytes(scene, page, width, autoColor, lineColor, isHyaline);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 上传七牛云
        UploadConfig uploadConfig = new UploadConfig(applicationConfig);
        UploadUtil uploadUtil = UploadExecutor.build(uploadConfig);
        String imagePath = uploadUtil.uploadFile(bytes);
        System.out.println(imagePath);

        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(bytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

}
