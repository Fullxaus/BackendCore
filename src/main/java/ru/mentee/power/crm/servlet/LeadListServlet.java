package ru.mentee.power.crm.servlet;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.service.LeadService;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class LeadListServlet extends HttpServlet {


    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        try {
            Path templatePath = Path.of("src/main/jte");
            DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(templatePath);
            this.templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        } catch (Exception e) {
            throw new ServletException("Failed to init TemplateEngine", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LeadService leadService = (LeadService) getServletContext().getAttribute("leadService");

        if (leadService == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "LeadService not initialized");
            return;
        }

        try {
            String search = request.getParameter("search");
            String status = request.getParameter("status");
            List<Lead> leads = (search != null && !search.isBlank()) || (status != null && !status.isBlank())
                    ? leadService.findLeads(search != null ? search : "", status != null ? status : "")
                    : leadService.findAll();

            response.setContentType("text/html; charset=UTF-8");
            Writer writer = response.getWriter();

            Map<String, Object> params = new HashMap<>();
            params.put("leads", leads);
            params.put("search", search != null ? search : "");
            params.put("status", status != null ? status : "");

            StringOutput out = new StringOutput();
            templateEngine.render("leads/list.jte", params, out);
            writer.write(out.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при получении данных");
        }
    }
}
