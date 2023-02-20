package com.cqupt.xuetu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.cqupt.xuetu.dao")
@SpringBootApplication
public class XuetuApplication {
    public static void main(String[] args) {
        SpringApplication.run(XuetuApplication.class, args);
    }
}
