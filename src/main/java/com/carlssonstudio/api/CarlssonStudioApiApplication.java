package com.carlssonstudio.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import com.carlssonstudio.api.config.MailProperties;
import com.carlssonstudio.api.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    MailProperties.class,
    JwtProperties.class
})
@EnableAsync
public class CarlssonStudioApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarlssonStudioApiApplication.class, args);
	}

}