package ru.mentee.power.crm.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.repository.LeadRepository;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/leads")
public class LeadListServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        LeadRepository repository = new LeadRepository();
        LeadService leadService = new LeadService(repository);

        // Добавьте 5 тестовых лидов
        leadService.addLead("test1@example.com", "Company1", LeadStatus.NEW, new Address("Moscow", "Suvorova", "123456"), "1234567890");
        leadService.addLead("test2@example.com", "Company2", LeadStatus.NEW, new Address("St.Petersburg", "Pushkinskaya", "987654"), "9876543210");
        leadService.addLead("test3@example.com", "Company3", LeadStatus.NEW, new Address("Kazan", "Kazanskaya", "111111"), "1111111111");
        leadService.addLead("test4@example.com", "Company4", LeadStatus.NEW, new Address("Novosibirsk", "Novosibirskaya", "222222"), "2222222222");
        leadService.addLead("test5@example.com", "Company5", LeadStatus.NEW, new Address("Ekaterinburg", "Lermontova", "333333"), "3333333333");

        getServletContext().setAttribute("leadService", leadService);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LeadService leadService = (LeadService) getServletContext().getAttribute("leadService");

        try {
            List<Lead> leads = leadService.findAll();

            response.setContentType("text/html; charset=UTF-8");
            PrintWriter writer = response.getWriter();

            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head><title>CRM - Lead List</title></head>");
            writer.println("<body>");
            writer.println("<h1>Lead List</h1>");
            writer.println("<table border='1'>");
            writer.println("<thead>");
            writer.println("<tr>");
            writer.println("<th>Email</th>");
            writer.println("<th>Company</th>");
            writer.println("<th>Status</th>");
            writer.println("</tr>");
            writer.println("</thead>");
            writer.println("<tbody>");

            for (Lead lead : leads) {
                writer.println("<tr>");
                writer.println("<td>" + lead.contact().email() + "</td>");
                writer.println("<td>" + lead.company() + "</td>");
                writer.println("<td>" + lead.status() + "</td>");
                writer.println("</tr>");
            }

            writer.println("</tbody>");
            writer.println("</table>");
            writer.println("</body>");
            writer.println("</html>");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при получении данных");
        }
    }
}
