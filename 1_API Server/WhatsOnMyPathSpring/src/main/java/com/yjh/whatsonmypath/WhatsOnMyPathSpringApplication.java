package com.yjh.whatsonmypath;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.yjh"})
public class WhatsOnMyPathSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhatsOnMyPathSpringApplication.class, args);
    }

}
