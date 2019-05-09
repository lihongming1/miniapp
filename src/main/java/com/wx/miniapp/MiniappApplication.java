package com.wx.miniapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.wx.miniapp.dao")
public class MiniappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniappApplication.class, args);
	}

}
