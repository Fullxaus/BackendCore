package ru.mentee.power.crm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test with profile "test" — verifies that H2 in-memory database is used.
 */
@SpringBootTest
@ActiveProfiles("test")
public class CrmIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testProfileUsesH2() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            String url = conn.getMetaData().getURL();
            String productName = meta.getDatabaseProductName();

            assertThat(url).contains("h2");
            assertThat(productName).isEqualToIgnoringCase("H2");
        }
    }
}
