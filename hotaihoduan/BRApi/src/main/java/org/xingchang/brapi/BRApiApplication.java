package org.xingchang.brapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.xingchang.brapi.mapper")
public class BRApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BRApiApplication.class, args);
    }

}
