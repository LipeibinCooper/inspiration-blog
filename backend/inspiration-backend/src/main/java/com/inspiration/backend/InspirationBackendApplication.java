package com.inspiration.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("com.inspiration.backend.mapper")
public class InspirationBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(InspirationBackendApplication.class, args);
    }

}
