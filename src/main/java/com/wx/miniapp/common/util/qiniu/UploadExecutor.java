package com.wx.miniapp.common.util.qiniu;

import com.alibaba.fastjson.JSON;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.wx.miniapp.config.ApplicationConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 七牛云上传图片工具
 */
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
        byte[] bytes = getBytesWithMultipartFile(multipartFile);
        return this.uploadFile(bytes);
    }

    @Override
    public String uploadFile(String filePath, MultipartFile multipartFile) {
        byte[] bytes = getBytesWithMultipartFile(multipartFile);
        return this.uploadFile(filePath, bytes);
    }

    @Override
    public String uploadFile(MultipartFile multipartFile, String fileName) {
        byte[] bytes = getBytesWithMultipartFile(multipartFile);
        return this.uploadFile(bytes, fileName);
    }

    @Override
    public String uploadFile(MultipartFile multipartFile, String fileName, String filePath) {
        byte[] bytes = getBytesWithMultipartFile(multipartFile);
        return this.uploadFile(bytes, fileName, filePath);
    }

    @Override
    public String uploadFile(File file) {
        return this.uploadFile(file, null, null);
    }

    @Override
    public String uploadFile(String filePath, File file) {
        return this.uploadFile(file, null, filePath);
    }

    @Override
    public String uploadFile(File file, String fileName) {
        return this.uploadFile(file, fileName, null);
    }

    @Override
    public String uploadFile(File file, String fileName, String filePath) {
        UploadManager uploadManager = uploadConfig.getUploadManager();
        String token = uploadConfig.getToken();
        ApplicationConfig applicationConfig = uploadConfig.getApplicationConfig();
        try {
            String key = preHandle(fileName, filePath);
            Response response = uploadManager.put(file, key, token);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            return "http://" + applicationConfig.qiniuBucketHost + "/" + putRet.key;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        return this.uploadFile(data, null, filePath);
    }

    @Override
    public String uploadFile(byte[] data, String fileName) {
        return this.uploadFile(data, fileName, null);
    }

    @Override
    public String uploadFile(byte[] data, String fileName, String filePath) {
        UploadManager uploadManager = uploadConfig.getUploadManager();
        String token = uploadConfig.getToken();
        ApplicationConfig applicationConfig = uploadConfig.getApplicationConfig();
        try {
            String key = preHandle(fileName, filePath);
            Response response = uploadManager.put(data, key, token);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            return "http://" + applicationConfig.qiniuBucketHost + "/" + putRet.key;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String preHandle(String fileName, String filePath) throws Exception {
        if (StringUtils.isNotBlank(fileName) && !fileName.contains(".")) {
            throw new Exception("文件名必须包含尾缀");
        }
        if (StringUtils.isNotBlank(filePath) && !filePath.startsWith("/")) {
            throw new Exception("前缀必须以'/'开头");
        }
        String name = StringUtils.isBlank(fileName) ? RandomStringUtils.randomAlphanumeric(32) : fileName;
        if (StringUtils.isBlank(filePath)) {
            return name;
        }
        String prefix = filePath.replaceFirst("/", "");
        return (prefix.endsWith("/") ? prefix : prefix.concat("/")).concat(name);
    }

    private byte[] getBytesWithMultipartFile(MultipartFile multipartFile) {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
