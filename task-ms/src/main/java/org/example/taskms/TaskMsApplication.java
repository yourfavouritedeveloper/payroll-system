package org.example.taskms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableJpaRepositories(basePackages = "org.example.taskms.repository.jpa")
@EnableRedisRepositories(basePackages = "org.example.taskms.repository.redis")
public class TaskMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskMsApplication.class, args);
    }

}
