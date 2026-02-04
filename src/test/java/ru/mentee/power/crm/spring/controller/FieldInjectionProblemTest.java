package ru.mentee.power.crm.spring.controller;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.spring.service.FieldDemoService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class FieldInjectionProblemTest {


    static class FieldInjectionController {

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

        FieldInjectionController controller = new FieldInjectionController();


        assertThat(controller.hasDependency()).isFalse();
    }

    @Test
    void fieldInjectionCausesNPE_whenMethodCalledWithoutSpring() {

        FieldInjectionController controller = new FieldInjectionController();


        assertThatThrownBy(controller::getStatus)
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void constructorInjectionAllowsTestability() {

        FieldDemoService mockService = new FieldDemoService();

        assertThat(mockService.getStatus()).isEqualTo("FIELD_INJECTED");

    }
}
