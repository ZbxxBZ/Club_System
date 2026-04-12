package com.bistu.clubsystembackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.bistu.clubsystembackend.mapper")
@SpringBootApplication
@EnableScheduling
public class ClubSystemBackendApplication extends SpringBootServletInitializer {

    /**
     * WAR 部署到 TongWeb 时的入口
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ClubSystemBackendApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ClubSystemBackendApplication.class, args);
    }

}
