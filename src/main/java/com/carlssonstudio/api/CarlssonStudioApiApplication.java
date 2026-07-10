package com.carlssonstudio.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import com.carlssonstudio.api.config.MailProperties;

@SpringBootApplication
@EnableConfigurationProperties(MailProperties.class)
@EnableAsync
public class CarlssonStudioApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarlssonStudioApiApplication.class, args);
	}

}