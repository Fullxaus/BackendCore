package ru.mentee.power.crm;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Гарантирует, что Liquibase выполнится до создания EntityManagerFactory,
 * чтобы миграции создали таблицы (companies, leads и т.д.) до валидации схемы Hibernate.
 */
@Component
public class JpaLiquibaseOrderConfig implements BeanFactoryPostProcessor, Ordered {

    private static final String EMF_BEAN_NAME = "entityManagerFactory";
    /** Выполняем раньше других post-processors, чтобы зависимость применилась. */
    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE;

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof DefaultListableBeanFactory bf)) {
            return;
        }
        String[] emfNames = bf.getBeanNamesForType(org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.class);
        if (emfNames.length == 0) {
            emfNames = bf.getBeanNamesForType(jakarta.persistence.EntityManagerFactory.class);
        }
        BeanDefinitionRegistry registry = bf;
        if (emfNames.length == 0 && registry.containsBeanDefinition(EMF_BEAN_NAME)) {
            emfNames = new String[]{EMF_BEAN_NAME};
        }
        // Всегда ставим зависимость от "liquibase", чтобы миграции выполнились до создания EMF
        final String liquibaseBeanName = "liquibase";
        for (String name : emfNames) {
            if (!registry.containsBeanDefinition(name)) {
                continue;
            }
            BeanDefinition bd = registry.getBeanDefinition(name);
            String[] dependsOn = bd.getDependsOn();
            if (dependsOn == null) {
                bd.setDependsOn(liquibaseBeanName);
            } else {
                boolean already = false;
                for (String d : dependsOn) {
                    if (liquibaseBeanName.equals(d)) {
                        already = true;
                        break;
                    }
                }
                if (!already) {
                    String[] newDependsOn = new String[dependsOn.length + 1];
                    newDependsOn[0] = liquibaseBeanName;
                    System.arraycopy(dependsOn, 0, newDependsOn, 1, dependsOn.length);
                    bd.setDependsOn(newDependsOn);
                }
            }
        }
    }
}
