package ru.mentee.power.crm.spring.service;

import org.springframework.stereotype.Service;

@Service
public class FieldDemoService {

    public String getStatus() {
        return "FIELD_INJECTED";
    }
}
