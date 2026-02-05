package ru.mentee.power.crm.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.spring.service.ConstructorDemoService;
import ru.mentee.power.crm.spring.service.FieldDemoService;
import ru.mentee.power.crm.spring.service.SetterDemoService;

import java.util.Map;

/**
 * Demo controller showing all three types of Dependency Injection for comparison.
 * - Constructor Injection: immutable, testable without Spring
 * - Field Injection: not testable without Spring, mutable
 * - Setter Injection: mutable, testable but less explicit
 */
@RestController
public class DemoController {


    private final ConstructorDemoService constructorService;


    @Autowired
    private FieldDemoService fieldService;


    private SetterDemoService setterService;

    public DemoController(ConstructorDemoService constructorService) {
        this.constructorService = constructorService;
    }

    @Autowired
    public void setSetterService(SetterDemoService setterService) {
        this.setterService = setterService;
    }

    @GetMapping("/demo")
    public ResponseEntity<Map<String, String>> getDemoStatus() {
        return ResponseEntity.ok(Map.of(
                "constructorInjection", getStatus(constructorService, "CONSTRUCTOR_INJECTED"),
                "fieldInjection", getStatus(fieldService, "FIELD_INJECTED"),
                "setterInjection", getStatus(setterService, "SETTER_INJECTED")
        ));
    }

    private String getStatus(Object service, String expectedStatus) {
        if (service == null) {
            return "NULL";
        }
        if (service instanceof ConstructorDemoService cs) {
            return cs.getStatus();
        }
        if (service instanceof FieldDemoService fs) {
            return fs.getStatus();
        }
        if (service instanceof SetterDemoService ss) {
            return ss.getStatus();
        }
        return expectedStatus;
    }
}
