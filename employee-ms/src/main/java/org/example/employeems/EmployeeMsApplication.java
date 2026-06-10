package org.example.employeems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableJpaRepositories(basePackages = "org.example.employeems.repository.jpa")
@EnableRedisRepositories(basePackages = "org.example.employeems.repository.redis")
public class EmployeeMsApplication {


    public static void main(String[] args) {


        SpringApplication.run(EmployeeMsApplication.class, args);
    }

}
