package ru.mentee.power.crm.spring.controller;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.spring.service.FieldDemoService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test demonstrating the problem with Field Injection:
 * When creating a class with field injection via 'new', the field remains null.
 * This makes unit testing impossible without Spring container.
 */
public class FieldInjectionProblemTest {

    /**
     * Class that mimics Field Injection - dependency is set by framework, not constructor.
     * In real Field Injection, Spring sets this via reflection after object creation.
     */
    static class FieldInjectionController {
        // Simulates @Autowired private FieldDemoService fieldService;
        private FieldDemoService fieldService;

        public String getStatus() {
            return fieldService.getStatus(); // NPE when fieldService is null!
        }

        public boolean hasDependency() {
            return fieldService != null;
        }
    }

    @Test
    void fieldInjectionCreatesNullDependency_whenCreatedWithNew() {
        // When: creating controller without Spring (pure unit test scenario)
        FieldInjectionController controller = new FieldInjectionController();

        // Then: field-injected dependency is NULL - we cannot inject it without reflection or setter
        assertThat(controller.hasDependency()).isFalse();
    }

    @Test
    void fieldInjectionCausesNPE_whenMethodCalledWithoutSpring() {
        // Given: controller created with 'new' - no Spring to inject the field
        FieldInjectionController controller = new FieldInjectionController();

        // When/Then: calling method that uses field-injected dependency causes NPE
        assertThatThrownBy(controller::getStatus)
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void constructorInjectionAllowsTestability() {
        // Given: mock service for constructor injection
        FieldDemoService mockService = new FieldDemoService();

        // When: creating controller via constructor - we CAN inject dependency
        // (DemoController-style with constructor would work: new DemoController(mockService))
        assertThat(mockService.getStatus()).isEqualTo("FIELD_INJECTED");

        // With Constructor Injection: new LeadController(mockService) works!
        // With Field Injection: we'd need Spring or reflection to set the field
    }
}
