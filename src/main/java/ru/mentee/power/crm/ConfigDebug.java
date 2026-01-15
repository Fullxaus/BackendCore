package ru.mentee.power.crm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConfigDebug implements CommandLineRunner {
    @Value("${gg.jte.templateLocation:NOT_SET}")
    String loc;

    @Override
    public void run(String... args) {
        System.out.println("GG.JTE.TEMPLATELOCATION=" + loc);
    }
}
