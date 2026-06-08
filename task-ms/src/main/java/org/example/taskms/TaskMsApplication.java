package org.example.taskms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class TaskMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskMsApplication.class, args);
    }

}
