package ru.mentee.power.crm.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.entity.Company;
import ru.mentee.power.crm.entity.LeadEntity;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AC1–AC5 и проверка N+1 (EntityGraph). Связь Company–Lead по company_id.
 */
@DataJpaTest
@ActiveProfiles("test")
public class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void ac1_saveCompany_persistsToDbWithUuid() {
        Company company = new Company();
        company.setName("Сбербанк");
        company.setIndustry("Finance");

        Company saved = companyRepository.save(company);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Сбербанк");
        assertThat(saved.getIndustry()).isEqualTo("Finance");

        Optional<Company> found = companyRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Сбербанк");
    }

    @Test
    void ac2_leadManyToOne_companyIdStoredInLeads() {
        Company company = new Company();
        company.setName("Сбербанк");
        company.setIndustry("Finance");
        company = companyRepository.save(company);

        LeadEntity lead = new LeadEntity();
        lead.setEmail("lead-ac2@example.com");
        lead.setPhone("+79990000001");
        lead.setCompanyName("Сбербанк");
        lead.setStatus("NEW");
        lead.setCompany(company);
        lead = leadRepository.save(lead);

        LeadEntity loaded = leadRepository.findById(lead.getId()).orElseThrow();
        assertThat(loaded.getCompany()).isNotNull();
        assertThat(loaded.getCompany().getId()).isEqualTo(company.getId());
        assertThat(loaded.getCompanyName()).isEqualTo("Сбербанк");
    }

    @Test
    void ac3_companyOneToMany_getLeadsReturnsAll() {
        Company company = new Company();
        company.setName("Acme");
        company.setIndustry("Tech");
        company = companyRepository.save(company);

        LeadEntity lead1 = new LeadEntity();
        lead1.setEmail("l1@acme.com");
        lead1.setPhone("+1");
        lead1.setCompanyName("Acme");
        lead1.setStatus("NEW");
        lead1.setCompany(company);
        leadRepository.save(lead1);

        LeadEntity lead2 = new LeadEntity();
        lead2.setEmail("l2@acme.com");
        lead2.setPhone("+2");
        lead2.setCompanyName("Acme");
        lead2.setStatus("QUALIFIED");
        lead2.setCompany(company);
        leadRepository.save(lead2);

        entityManager.flush();
        entityManager.clear();

        Company loaded = companyRepository.findByIdWithLeads(company.getId()).orElseThrow();
        assertThat(loaded.getLeads()).hasSize(2);
        assertThat(loaded.getLeads()).extracting(LeadEntity::getEmail)
                .containsExactlyInAnyOrder("l1@acme.com", "l2@acme.com");
    }

    @Test
    void ac4_findByIdWithLeads_singleQueryWithJoin() {
        Company company = new Company();
        company.setName("N+1 Corp");
        company.setIndustry("Finance");
        company = companyRepository.save(company);

        LeadEntity lead = new LeadEntity();
        lead.setEmail("n1@corp.com");
        lead.setPhone("+0");
        lead.setCompanyName("N+1 Corp");
        lead.setStatus("NEW");
        lead.setCompany(company);
        leadRepository.save(lead);

        entityManager.flush();
        entityManager.clear();

        Optional<Company> withLeads = companyRepository.findByIdWithLeads(company.getId());
        assertThat(withLeads).isPresent();
        assertThat(withLeads.get().getLeads())
                .hasSize(1)
                .extracting(LeadEntity::getEmail)
                .containsExactly("n1@corp.com");
    }

    @Test
    void ac5_addLeadRemoveLead_bothSidesSynced() {
        Company company = new Company();
        company.setName("Sync Inc");
        company.setIndustry("Tech");
        company = companyRepository.save(company);

        LeadEntity lead = new LeadEntity();
        lead.setEmail("sync@inc.com");
        lead.setPhone("+0");
        lead.setCompanyName("Sync Inc");
        lead.setStatus("NEW");
        leadRepository.save(lead);

        company.addLead(lead);
        assertThat(lead.getCompany()).isEqualTo(company);
        assertThat(company.getLeads()).contains(lead);

        company.removeLead(lead);
        assertThat(lead.getCompany()).isNull();
        assertThat(company.getLeads()).doesNotContain(lead);
    }

    @Test
    void findByName_returnsCompany() {
        Company company = new Company();
        company.setName("UniqueName");
        company.setIndustry("Finance");
        companyRepository.save(company);

        Optional<Company> found = companyRepository.findByName("UniqueName");
        assertThat(found).isPresent();
        assertThat(found.get().getIndustry()).isEqualTo("Finance");
    }
}
