package ru.mentee.power.crm.spring.service;

import org.springframework.stereotype.Service;

@Service
public class SetterDemoService {

    public String getStatus() {
        return "SETTER_INJECTED";
    }
}
