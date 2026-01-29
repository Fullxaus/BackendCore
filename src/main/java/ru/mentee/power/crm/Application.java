package ru.mentee.power.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;


@SpringBootApplication
@ComponentScan(
        basePackages = "ru.mentee.power.crm",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = ru.mentee.power.crm.spring.Application.class))
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
}
