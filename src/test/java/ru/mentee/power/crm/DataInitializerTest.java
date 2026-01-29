package ru.mentee.power.crm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DataInitializerTest {

    private LeadService leadService;
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        leadService = mock(LeadService.class);
        dataInitializer = new DataInitializer(leadService);
    }

    @Test
    void run_shouldAddFiveLeads() throws Exception {
        // act
        dataInitializer.run();

        // assert number of invocations
        verify(leadService, times(5)).addLead(anyString(), anyString(), any(LeadStatus.class), any(Address.class), anyString());

        // capture arguments to inspect them
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> companyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LeadStatus> statusCaptor = ArgumentCaptor.forClass(LeadStatus.class);
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        ArgumentCaptor<String> phoneCaptor = ArgumentCaptor.forClass(String.class);

        verify(leadService, times(5)).addLead(
                emailCaptor.capture(),
                companyCaptor.capture(),
                statusCaptor.capture(),
                addressCaptor.capture(),
                phoneCaptor.capture()
        );

        // verify captured values (order same as in DataInitializer)
        assertEquals("test1@example.com", emailCaptor.getAllValues().get(0));
        assertEquals("Company1", companyCaptor.getAllValues().get(0));
        assertEquals(LeadStatus.NEW, statusCaptor.getAllValues().get(0));
        Address addr1 = addressCaptor.getAllValues().get(0);
        assertEquals("Moscow", addr1.city());
        assertEquals("Suvorova", addr1.street());
        assertEquals("123456", addr1.zip());
        assertEquals("1234567890", phoneCaptor.getAllValues().get(0));

        assertEquals("test2@example.com", emailCaptor.getAllValues().get(1));
        assertEquals("Company2", companyCaptor.getAllValues().get(1));
        Address addr2 = addressCaptor.getAllValues().get(1);
        assertEquals("St.Petersburg", addr2.city());
        assertEquals("Pushkinskaya", addr2.street());
        assertEquals("987654", addr2.zip());
        assertEquals("9876543210", phoneCaptor.getAllValues().get(1));

        assertEquals("test3@example.com", emailCaptor.getAllValues().get(2));
        assertEquals("Company3", companyCaptor.getAllValues().get(2));
        Address addr3 = addressCaptor.getAllValues().get(2);
        assertEquals("Kazan", addr3.city());
        assertEquals("Kazanskaya", addr3.street());
        assertEquals("111111", addr3.zip());
        assertEquals("1111111111", phoneCaptor.getAllValues().get(2));

        assertEquals("test4@example.com", emailCaptor.getAllValues().get(3));
        assertEquals("Company4", companyCaptor.getAllValues().get(3));
        Address addr4 = addressCaptor.getAllValues().get(3);
        assertEquals("Novosibirsk", addr4.city());
        assertEquals("Novosibirskaya", addr4.street());
        assertEquals("222222", addr4.zip());
        assertEquals("2222222222", phoneCaptor.getAllValues().get(3));

        assertEquals("test5@example.com", emailCaptor.getAllValues().get(4));
        assertEquals("Company5", companyCaptor.getAllValues().get(4));
        Address addr5 = addressCaptor.getAllValues().get(4);
        assertEquals("Ekaterinburg", addr5.city());
        assertEquals("Lermontova", addr5.street());
        assertEquals("333333", addr5.zip());
        assertEquals("3333333333", phoneCaptor.getAllValues().get(4));
    }
}
