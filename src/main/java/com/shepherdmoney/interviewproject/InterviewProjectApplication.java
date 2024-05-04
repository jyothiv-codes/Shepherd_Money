package com.shepherdmoney.interviewproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.shepherdmoney.interviewproject.model.*;

@SpringBootApplication
@EnableJpaRepositories
@ComponentScan(basePackages = {"com.shepherdmoney.interviewproject"})
public class InterviewProjectApplication {
    public static void main(String[] args) {
        ApplicationUser user = new ApplicationUser();
        SpringApplication.run(InterviewProjectApplication.class, args);



    }
}
