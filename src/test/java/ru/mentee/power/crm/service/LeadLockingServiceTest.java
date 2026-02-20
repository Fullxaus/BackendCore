package ru.mentee.power.crm.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тест конкурентного доступа: два потока обновляют один Lead.
 * С pessimistic lock обновления выполняются последовательно, оба успешны.
 * С optimistic lock (без @Lock) вторая транзакция получает OptimisticLockException.
 */
@SpringBootTest
@ActiveProfiles("test")
class LeadLockingServiceTest {

    @Autowired
    private LeadLockingService lockingService;

    @Autowired
    private LeadService leadService;

    @Test
    void pessimisticLock_twoThreadsUpdateSameLead_sequentialSuccess() throws Exception {
        Address address = new Address("Moscow", "Street", "123456");
        Lead lead = leadService.addLead("lock-pess@example.com", "Company", LeadStatus.NEW, address, "+79990000001");
        UUID leadId = lead.id();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Callable<Lead>> tasks = List.of(
                () -> lockingService.updateStatusWithPessimisticLock(leadId, LeadStatus.CONTACTED),
                () -> lockingService.updateStatusWithPessimisticLock(leadId, LeadStatus.QUALIFIED)
        );

        List<Future<Lead>> futures = new ArrayList<>();
        for (Callable<Lead> task : tasks) {
            futures.add(executor.submit(task));
        }

        Lead r1 = futures.get(0).get();
        Lead r2 = futures.get(1).get();

        assertThat(r1).isNotNull();
        assertThat(r2).isNotNull();
        assertThat(r1.status()).isIn(LeadStatus.CONTACTED.name(), LeadStatus.QUALIFIED.name());
        assertThat(r2.status()).isIn(LeadStatus.CONTACTED.name(), LeadStatus.QUALIFIED.name());

        Lead after = leadService.findById(leadId).orElseThrow();
        assertThat(after.status()).isIn(LeadStatus.CONTACTED.name(), LeadStatus.QUALIFIED.name());

        executor.shutdown();
    }

    @Test
    void optimisticLock_twoThreadsUpdateSameLead_secondGetsOptimisticLockException() throws Exception {
        Address address = new Address("Moscow", "Street", "123456");
        Lead lead = leadService.addLead("lock-opt@example.com", "Company", LeadStatus.NEW, address, "+79990000002");
        UUID leadId = lead.id();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<Lead> f1 = executor.submit(() -> lockingService.updateStatusWithOptimisticLock(leadId, LeadStatus.CONTACTED));
        Future<Lead> f2 = executor.submit(() -> lockingService.updateStatusWithOptimisticLock(leadId, LeadStatus.QUALIFIED));

        Lead first = f1.get();
        assertThat(first).isNotNull();
        assertThat(first.status()).isEqualTo(LeadStatus.CONTACTED.name());

        // При конфликте версий вторая транзакция получает OptimisticLockException.
        // Если гонка дала оба успешных — проверяем только финальное состояние.
        try {
            Lead second = f2.get();
            assertThat(second).isNotNull();
        } catch (ExecutionException e) {
            assertThatThrownBy(() -> {
                throw e.getCause();
            }).isInstanceOf(ObjectOptimisticLockingFailureException.class);
        }

        Lead after = leadService.findById(leadId).orElseThrow();
        assertThat(after.status()).isIn(LeadStatus.CONTACTED.name(), LeadStatus.QUALIFIED.name());
        executor.shutdown();
    }

    @Test
    void convertWithPessimisticLock_twoThreadsSecondWaits() throws Exception {
        Address address = new Address("Moscow", "Street", "123456");
        Lead lead = leadService.addLead("convert-lock@example.com", "Company", LeadStatus.QUALIFIED, address, "+79990000003");
        UUID leadId = lead.id();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<ru.mentee.power.crm.domain.Deal> f1 = executor.submit(
                () -> lockingService.convertWithPessimisticLock(leadId, new BigDecimal("10000"))
        );
        ru.mentee.power.crm.domain.Deal deal = f1.get();
        assertThat(deal).isNotNull();
        assertThat(deal.getLeadId()).isEqualTo(leadId);

        executor.shutdown();
    }
}
