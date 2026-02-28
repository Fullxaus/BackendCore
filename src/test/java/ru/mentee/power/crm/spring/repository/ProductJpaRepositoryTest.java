package ru.mentee.power.crm.spring.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
public class ProductJpaRepositoryTest {

    @Autowired
    private ProductJpaRepository productRepository;

    @Test
    void shouldSaveAndFindProduct_whenValidData() {
        // Given
        Product product = new Product();
        product.setName("Консультация по архитектуре");
        product.setSku("CONSULT-ARCH-001");
        product.setPrice(new BigDecimal("50000.00"));
        product.setActive(true);

        // When
        Product saved = productRepository.save(product);

        // Then
        assertThat(saved.getId()).isNotNull();
        Optional<Product> found = productRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSku()).isEqualTo("CONSULT-ARCH-001");
    }

    @Test
    void shouldFindProductBySku_whenProductExists() {
        // Given продукт с SKU сохранён
        Product product = new Product();
        product.setName("Ноутбук");
        product.setSku("LAPTOP-001");
        product.setPrice(new BigDecimal("89900.00"));
        product.setActive(true);
        productRepository.save(product);

        // When вызываем findBySku("LAPTOP-001")
        Optional<Product> found = productRepository.findBySku("LAPTOP-001");

        // Then Optional содержит продукт
        assertThat(found).isPresent();
        assertThat(found.get().getSku()).isEqualTo("LAPTOP-001");
        assertThat(found.get().getName()).isEqualTo("Ноутбук");
    }

    @Test
    void shouldReturnEmptyOptional_whenFindBySkuForNonExistentSku() {
        Optional<Product> found = productRepository.findBySku("NON-EXISTENT");
        assertThat(found).isEmpty();
    }

    @Test
    void shouldReturnOnlyActiveProducts_whenFindByActiveTrue() {
        // Given 2 активных продукта и 1 неактивный
        Product active1 = new Product();
        active1.setName("Активный 1");
        active1.setSku("ACTIVE-001");
        active1.setPrice(BigDecimal.ONE);
        active1.setActive(true);
        productRepository.save(active1);

        Product active2 = new Product();
        active2.setName("Активный 2");
        active2.setSku("ACTIVE-002");
        active2.setPrice(BigDecimal.ONE);
        active2.setActive(true);
        productRepository.save(active2);

        Product inactive = new Product();
        inactive.setName("Неактивный");
        inactive.setSku("INACTIVE-001");
        inactive.setPrice(BigDecimal.ONE);
        inactive.setActive(false);
        productRepository.save(inactive);

        // When вызываем findByActiveTrue()
        List<Product> activeProducts = productRepository.findByActiveTrue();

        // Then возвращается список из 2 продуктов
        assertThat(activeProducts).hasSize(2);
        assertThat(activeProducts).extracting(Product::getSku).containsExactlyInAnyOrder("ACTIVE-001", "ACTIVE-002");
    }

    @Test
    void shouldThrowDataIntegrityViolationException_whenDuplicateSku() {
        // Given продукт с SKU "TEST-001" сохранён
        Product first = new Product();
        first.setName("Первый");
        first.setSku("TEST-001");
        first.setPrice(BigDecimal.ONE);
        first.setActive(true);
        productRepository.saveAndFlush(first);

        // When пытаемся сохранить второй продукт с тем же SKU
        Product second = new Product();
        second.setName("Второй");
        second.setSku("TEST-001");
        second.setPrice(BigDecimal.TEN);
        second.setActive(true);

        // Then выбрасывается DataIntegrityViolationException
        assertThatThrownBy(() -> productRepository.saveAndFlush(second))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
