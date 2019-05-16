package com.wx.miniapp.common.util.qiniu;

import com.alibaba.fastjson.JSON;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.wx.miniapp.config.ApplicationConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class UploadExecutor implements UploadUtil {

    private UploadConfig uploadConfig;

    private static final UploadExecutor executor = new UploadExecutor();

    public static UploadUtil build(UploadConfig uploadConfig) {
        executor.setUploadConfig(uploadConfig);
        return executor;
    }

    public void setUploadConfig(UploadConfig uploadConfig) {
        this.uploadConfig = uploadConfig;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        return null;
    }

    @Override
    public String uploadFile(String filePath, MultipartFile multipartFile) {
        return null;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile, String fileName) {
        return null;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile, String fileName, String filePath) {
        return null;
    }

    @Override
    public String uploadFile(File file) {
        return null;
    }

    @Override
    public String uploadFile(String filePath, File file) {
        return null;
    }

    @Override
    public String uploadFile(File file, String fileName) {
        return null;
    }

    @Override
    public String uploadFile(File file, String fileName, String filePath) {
        return null;
    }

    @Override
    public String uploadFile(byte[] data) {
        UploadManager uploadManager = uploadConfig.getUploadManager();
        String token = uploadConfig.getToken();
        ApplicationConfig applicationConfig = uploadConfig.getApplicationConfig();
        try {
            Response response = uploadManager.put(data, null, token);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            return "http://" + applicationConfig.qiniuBucketHost + "/" + putRet.key;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public String uploadFile(String filePath, byte[] data) {
        return null;
    }

    @Override
    public String uploadFile(byte[] data, String fileName) {
        return null;
    }

    @Override
    public String uploadFile(byte[] data, String fileName, String filePath) {
        return null;
    }
}
