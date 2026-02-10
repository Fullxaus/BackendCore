package ru.mentee.power.crm;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.servlet.LeadListServlet;

import java.io.File;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {

        LeadRepository repository = new InMemoryLeadRepository();
        LeadService leadService = new LeadService(repository);

        leadService.addLead("test1@example.com", "Company1", LeadStatus.NEW, new Address("Moscow", "Suvorova", "123456"), "1234567890");
        leadService.addLead("test2@example.com", "Company2", LeadStatus.NEW, new Address("St.Petersburg", "Pushkinskaya", "987654"), "9876543210");
        leadService.addLead("test3@example.com", "Company3", LeadStatus.NEW, new Address("Kazan", "Kazanskaya", "111111"), "1111111111");
        leadService.addLead("test4@example.com", "Company4", LeadStatus.NEW, new Address("Novosibirsk", "Novosibirskaya", "222222"), "2222222222");
        leadService.addLead("test5@example.com", "Company5", LeadStatus.NEW, new Address("Ekaterinburg", "Lermontova", "333333"), "3333333333");

        Tomcat tomcat = new Tomcat();

        Connector connector = new Connector();
        connector.setPort(8080);
        tomcat.getService().addConnector(connector);
        tomcat.setConnector(connector);

        Context context = tomcat.addContext("", new File(".").getAbsolutePath());

        context.getServletContext().setAttribute("leadService", leadService);

        tomcat.addServlet(context, "LeadListServlet", new LeadListServlet());
        context.addServletMappingDecoded("/leads", "LeadListServlet");

        tomcat.start();

        System.out.println("tomcat started; connectors:");
        Arrays.stream(tomcat.getService().findConnectors())
                .forEach(c -> System.out.println(" connector: " + c.getProtocol() + " port=" + c.getPort() + " state=" + c.getState()));
        System.out.println("about to await");

        tomcat.getServer().await();
    }
}
