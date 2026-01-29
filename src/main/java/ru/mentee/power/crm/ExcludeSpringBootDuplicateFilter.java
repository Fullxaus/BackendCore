package ru.mentee.power.crm;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

/**
 * Исключает из сканирования второй класс с @SpringBootApplication в пакете spring,
 * чтобы избежать ConflictingBeanDefinitionException при запуске корневого Application.
 */
public class ExcludeSpringBootDuplicateFilter implements TypeFilter {

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        ClassMetadata metadata = metadataReader.getClassMetadata();
        String className = metadata.getClassName();
        // Исключаем только классы из пакета spring (не корневой Application)
        if (!className.startsWith("ru.mentee.power.crm.spring.")) {
            return false;
        }
        return metadataReader.getAnnotationMetadata().hasAnnotation(SpringBootApplication.class.getName());
    }
}
