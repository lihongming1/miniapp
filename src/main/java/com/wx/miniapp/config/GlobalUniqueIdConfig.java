package com.wx.miniapp.config;

import com.wx.miniapp.common.util.SnowflakeIdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GlobalUniqueIdConfig {

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker(){
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        return idWorker;
    }

}
