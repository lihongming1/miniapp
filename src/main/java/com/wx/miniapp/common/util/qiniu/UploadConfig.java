package com.wx.miniapp.common.util.qiniu;

import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.wx.miniapp.config.ApplicationConfig;

public class UploadConfig {

    private ApplicationConfig applicationConfig;

    private UploadManager uploadManager;

    private String token;

    private UploadConfig(){

    }

    public UploadConfig(ApplicationConfig applicationConfig){
        Configuration cfg = new Configuration();
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(applicationConfig.qiniuAccessKey, applicationConfig.qiniuSecretkey);
        String token = auth.uploadToken(applicationConfig.qiniuBucketName);
        setToken(token);
        setUploadManager(uploadManager);
        setApplicationConfig(applicationConfig);
    }

    public UploadManager getUploadManager() {
        return uploadManager;
    }

    public void setUploadManager(UploadManager uploadManager) {
        this.uploadManager = uploadManager;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }
}
