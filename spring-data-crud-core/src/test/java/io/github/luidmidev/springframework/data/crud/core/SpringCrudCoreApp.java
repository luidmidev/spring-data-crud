package io.github.luidmidev.springframework.data.crud.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@SpringBootApplication
public class SpringCrudCoreApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringCrudCoreApp.class, args);
    }

}
