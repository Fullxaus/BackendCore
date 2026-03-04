package ru.mentee.power.crm.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.entity.DealEntity;
import ru.mentee.power.crm.entity.DealProduct;
import ru.mentee.power.crm.entity.LeadEntity;
import ru.mentee.power.crm.domain.Product;
import ru.mentee.power.crm.spring.repository.ProductJpaRepository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class DealProductIntegrationTest {

    @Autowired
    private DealJpaRepository dealRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    @Test
    void testSaveDealWithProducts() {
        LeadEntity lead = new LeadEntity();
        lead.setEmail("deal-product-test@mail.ru");
        lead.setPhone("+79991234567");
        lead.setCompanyName("Test Company");
        lead.setStatus("NEW");
        lead.setCreatedAt(Instant.now());
        lead = leadRepository.save(lead);

        DealEntity deal = new DealEntity();
        deal.setId(UUID.randomUUID());
        deal.setLeadId(lead.getId());
        deal.setAmount(new BigDecimal("150000"));
        deal.setStatus("OPEN");
        deal.setCreatedAt(Instant.now());

        Product product1 = new Product();
        product1.setName("Ноутбук Dell");
        product1.setSku("LAPTOP-001");
        product1.setPrice(new BigDecimal("90000"));
        product1.setActive(true);
        product1 = productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Монитор LG");
        product2.setSku("MONITOR-001");
        product2.setPrice(new BigDecimal("25000"));
        product2.setActive(true);
        product2 = productRepository.save(product2);

        DealProduct dealProduct1 = new DealProduct();
        dealProduct1.setProduct(product1);
        dealProduct1.setQuantity(2);
        dealProduct1.setUnitPrice(new BigDecimal("81000"));

        DealProduct dealProduct2 = new DealProduct();
        dealProduct2.setProduct(product2);
        dealProduct2.setQuantity(1);
        dealProduct2.setUnitPrice(new BigDecimal("25000"));

        deal.addDealProduct(dealProduct1);
        deal.addDealProduct(dealProduct2);

        dealRepository.save(deal);

        UUID dealId = deal.getId();
        Optional<DealEntity> loaded = dealRepository.findById(dealId);
        assertThat(loaded).isPresent();
        DealEntity fromDb = loaded.get();
        List<DealProduct> dealProducts = fromDb.getDealProducts();
        assertThat(dealProducts).hasSize(2);

        DealProduct first = dealProducts.stream()
                .filter(dp -> dp.getQuantity() == 2)
                .findFirst()
                .orElseThrow();
        assertThat(first.getQuantity()).isEqualTo(2);
        assertThat(first.getUnitPrice()).isEqualByComparingTo(new BigDecimal("81000"));
        assertThat(first.getProduct()).isNotNull();
        assertThat(first.getProduct().getSku()).isEqualTo("LAPTOP-001");

        DealProduct second = dealProducts.stream()
                .filter(dp -> dp.getQuantity() == 1)
                .findFirst()
                .orElseThrow();
        assertThat(second.getQuantity()).isEqualTo(1);
        assertThat(second.getUnitPrice()).isEqualByComparingTo(new BigDecimal("25000"));
        assertThat(second.getProduct().getSku()).isEqualTo("MONITOR-001");
    }

    @Test
    void testEntityGraphSolvesNPlusOne() {
        LeadEntity lead = new LeadEntity();
        lead.setEmail("nplusone-test@mail.ru");
        lead.setPhone("+79997654321");
        lead.setCompanyName("N+1 Company");
        lead.setStatus("NEW");
        lead.setCreatedAt(Instant.now());
        lead = leadRepository.save(lead);

        DealEntity deal = new DealEntity();
        deal.setId(UUID.randomUUID());
        deal.setLeadId(lead.getId());
        deal.setAmount(new BigDecimal("200000"));
        deal.setStatus("OPEN");
        deal.setCreatedAt(Instant.now());

        for (int i = 1; i <= 3; i++) {
            Product p = new Product();
            p.setName("Product " + i);
            p.setSku("SKU-" + i);
            p.setPrice(new BigDecimal(10000 * i));
            p.setActive(true);
            p = productRepository.save(p);
            DealProduct dp = new DealProduct();
            dp.setProduct(p);
            dp.setQuantity(i);
            dp.setUnitPrice(new BigDecimal(9000 * i));
            deal.addDealProduct(dp);
        }
        dealRepository.saveAndFlush(deal);
        UUID dealId = deal.getId();

        entityManager.clear();

        Optional<DealEntity> withGraph = dealRepository.findDealWithProducts(dealId);
        assertThat(withGraph).as("findDealWithProducts should find deal %s", dealId).isPresent();
        DealEntity loadedWithGraph = withGraph.get();
        List<DealProduct> listWithGraph = loadedWithGraph.getDealProducts();
        assertThat(listWithGraph).hasSize(3);
        for (DealProduct dp : listWithGraph) {
            assertThat(dp.getProduct()).isNotNull();
            assertThat(dp.getProduct().getName()).isNotBlank();
        }

        entityManager.clear();

        Optional<DealEntity> withoutGraph = dealRepository.findById(dealId);
        assertThat(withoutGraph).isPresent();
        DealEntity loadedWithout = withoutGraph.get();
        List<DealProduct> listWithout = loadedWithout.getDealProducts();
        assertThat(listWithout).hasSize(3);
        for (DealProduct dp : listWithout) {
            assertThat(dp.getProduct()).isNotNull();
            assertThat(dp.getProduct().getName()).isNotBlank();
        }

        List<String> skusWithGraph = loadedWithGraph.getDealProducts().stream()
                .map(dp -> dp.getProduct().getSku())
                .sorted()
                .toList();
        List<String> skusWithout = loadedWithout.getDealProducts().stream()
                .map(dp -> dp.getProduct().getSku())
                .sorted()
                .toList();
        assertThat(skusWithGraph).containsExactlyInAnyOrderElementsOf(skusWithout);
    }

    @Test
    void testExplainAnalyzeDealWithProductsQuery() throws Exception {
        LeadEntity lead = new LeadEntity();
        lead.setEmail("explain-analyze@mail.ru");
        lead.setPhone("+79990000000");
        lead.setCompanyName("Explain Co");
        lead.setStatus("NEW");
        lead.setCreatedAt(Instant.now());
        lead = leadRepository.save(lead);

        DealEntity deal = new DealEntity();
        deal.setId(UUID.randomUUID());
        deal.setLeadId(lead.getId());
        deal.setAmount(new BigDecimal("100000"));
        deal.setStatus("OPEN");
        deal.setCreatedAt(Instant.now());

        Product p = new Product();
        p.setName("Product");
        p.setSku("EXPLAIN-SKU");
        p.setPrice(BigDecimal.TEN);
        p.setActive(true);
        p = productRepository.save(p);
        DealProduct dp = new DealProduct();
        dp.setProduct(p);
        dp.setQuantity(1);
        dp.setUnitPrice(BigDecimal.TEN);
        deal.addDealProduct(dp);
        dealRepository.saveAndFlush(deal);
        entityManager.clear();

        UUID dealId = deal.getId();
        String explainQuery = "EXPLAIN ANALYZE SELECT de1_0.id, de1_0.amount, de1_0.created_at, de1_0.lead_id, de1_0.status, "
                + "dp1_0.deal_id, dp1_0.id AS dp_id, dp1_0.product_id, dp1_0.quantity, dp1_0.unit_price "
                + "FROM deals de1_0 LEFT JOIN deal_product dp1_0 ON de1_0.id = dp1_0.deal_id WHERE de1_0.id = ?";

        List<String> lines = new ArrayList<>();
        lines.add("EXPLAIN ANALYZE result for Deal with Products (EntityGraph JOIN query)");
        lines.add("Query: " + explainQuery.replace("?", dealId.toString()));
        lines.add("");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(explainQuery)) {
            ps.setObject(1, dealId);
            try (ResultSet rs = ps.executeQuery()) {
                int colCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    StringBuilder row = new StringBuilder();
                    for (int i = 1; i <= colCount; i++) {
                        if (i > 1) row.append(" | ");
                        row.append(rs.getString(i));
                    }
                    lines.add(row.toString());
                }
            }
        }

        Path outDir = Paths.get(System.getProperty("user.dir", "."), "build");
        Files.createDirectories(outDir);
        Path outFile = outDir.resolve("explain-analyze-deal-products.txt");
        Files.write(outFile, lines);
    }
}
