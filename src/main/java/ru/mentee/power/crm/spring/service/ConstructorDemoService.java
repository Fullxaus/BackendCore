package ru.mentee.power.crm.spring.service;

import org.springframework.stereotype.Service;

@Service
public class ConstructorDemoService {

    public String getStatus() {
        return "CONSTRUCTOR_INJECTED";
    }
}
