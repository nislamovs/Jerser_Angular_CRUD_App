package com.jerseyexample.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Slf4j
@SpringBootApplication
@EnableJpaRepositories
@EnableSpringDataWebSupport
public class JerseydemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JerseydemoApplication.class, args);
    }
}

