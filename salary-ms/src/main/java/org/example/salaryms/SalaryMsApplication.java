package org.example.salaryms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableJpaRepositories(basePackages = "org.example.salaryms.repository.jpa")
@EnableRedisRepositories(basePackages = "org.example.salaryms.repository.redis")
public class SalaryMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalaryMsApplication.class, args);
    }

}
