package com.github.sputnik1111.service.product;


import com.github.sputnik1111.service.product.domain.user.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringApp {

    public static void main(String[] args) {

        ApplicationContext applicationContext = SpringApplication.run(SpringApp.class, args);

        UserService userService = applicationContext.getBean(UserService.class);

        userService.clear();

    }
}
