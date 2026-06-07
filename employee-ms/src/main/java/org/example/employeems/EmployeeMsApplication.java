package org.example.employeems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EmployeeMsApplication {


    public static void main(String[] args) {

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("Nihad123!"));

        SpringApplication.run(EmployeeMsApplication.class, args);
    }

}
