package ru.mentee.power.crm;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final LeadService leadService;

    public DataInitializer(LeadService leadService) {
        this.leadService = leadService;
    }


    @Override
    public void run(String... args) throws Exception {
        leadService.addLead("test1@example.com", "Company1", LeadStatus.NEW, new Address("Moscow", "Suvorova", "123456"), "1234567890");
        leadService.addLead("test2@example.com", "Company2", LeadStatus.NEW, new Address("St.Petersburg", "Pushkinskaya", "987654"), "9876543210");
        leadService.addLead("test3@example.com", "Company3", LeadStatus.NEW, new Address("Kazan", "Kazanskaya", "111111"), "1111111111");
        leadService.addLead("test4@example.com", "Company4", LeadStatus.NEW, new Address("Novosibirsk", "Novosibirskaya", "222222"), "2222222222");
        leadService.addLead("test5@example.com", "Company5", LeadStatus.NEW, new Address("Ekaterinburg", "Lermontova", "333333"), "3333333333");
    }
}
