package ru.mentee.power.crm;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.servlet.LeadListServlet;
import jakarta.servlet.annotation.WebServlet;
import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {

        // Создайте LeadRepository (InMemoryLeadRepository)
        LeadRepository repository = new LeadRepository();

        // Создайте LeadService через конструктор DI (передайте Repository)
        LeadService leadService = new LeadService(repository);

        // Добавьте 5 тестовых лидов через leadService.addLead
        leadService.addLead("test1@example.com", "Company1", LeadStatus.NEW, new Address("Moscow", "Suvorova", "123456"), "1234567890");
        leadService.addLead("test2@example.com", "Company2", LeadStatus.NEW, new Address("St.Petersburg", "Pushkinskaya", "987654"), "9876543210");
        leadService.addLead("test3@example.com", "Company3", LeadStatus.NEW, new Address("Kazan", "Kazanskaya", "111111"), "1111111111");
        leadService.addLead("test4@example.com", "Company4", LeadStatus.NEW, new Address("Novosibirsk", "Novosibirskaya", "222222"), "2222222222");
        leadService.addLead("test5@example.com", "Company5", LeadStatus.NEW, new Address("Ekaterinburg", "Lermontova", "333333"), "3333333333");

        // Создайте Tomcat экземпляр через new Tomcat()
        Tomcat tomcat = new Tomcat();

        // Установите порт через tomcat.setPort(8080)
        tomcat.setPort(8080);

        // Создайте контекст приложения через tomcat.addContext("", new File(".").getAbsolutePath())
        Context context = tomcat.addContext("", new File(".").getAbsolutePath());

        // Сохраните LeadService в ServletContext через context.getServletContext().setAttribute("leadService", leadService)
        context.getServletContext().setAttribute("leadService", leadService);

        // Зарегистрируйте LeadListServlet через tomcat.addServlet(context, "LeadListServlet", new LeadListServlet())
        tomcat.addServlet(context, "LeadListServlet", new LeadListServlet());

        // Задайте URL маппинг через context.addServletMappingDecoded("/leads", "LeadListServlet")
        context.addServletMappingDecoded("/leads", "LeadListServlet");

        // Запустите сервер через tomcat.start()
        tomcat.start();

        // Заблокируйте main поток через tomcat.getServer().await() (чтобы сервер не завершился)
        tomcat.getServer().await();

        System.out.println("Tomcat started on port 8080");
        System.out.println("Open http://localhost:8080/leads in browser");
    }
}
