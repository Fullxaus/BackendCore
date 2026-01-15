package ru.mentee.power.crm.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.service.LeadService;
import java.util.List;

@Controller
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping("/leads")
    public String showLeads(Model model) {
        List<Lead> list = leadService.findAll();
        model.addAttribute("leads", list == null ? List.of() : list);
        return "leads/list";
    }
}
