package ru.mentee.power.crm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Гарантирует наличие колонки company_id в таблице leads.
 * Выполняется после инициализации БД (Liquibase), чтобы к моменту первого запроса
 * к leads колонка уже существовала.
 */
@Component
@DependsOnDatabaseInitialization
@ConditionalOnBean(DataSource.class)
public class EnsureCompanyIdColumnRunner {

    private static final Logger log = LoggerFactory.getLogger(EnsureCompanyIdColumnRunner.class);

    private final DataSource dataSource;

    public EnsureCompanyIdColumnRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void ensureCompanyIdColumn() {
        String sql = "ALTER TABLE leads ADD COLUMN IF NOT EXISTS company_id UUID REFERENCES companies(id)";
        try (Connection conn = dataSource.getConnection();
             Statement st = conn.createStatement()) {
            st.execute(sql);
            log.info("Колонка leads.company_id проверена/добавлена");
        } catch (Exception e) {
            log.warn("Не удалось добавить колонку company_id в leads: {}", e.getMessage());
        }
    }
}
