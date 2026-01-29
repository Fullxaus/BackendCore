package ru.mentee.power.crm.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;
import java.util.List;

@Controller
public class LeadController {
    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping("/lead")
    public String createLead(
                             @RequestParam String email,
                             @RequestParam String company,
                             @RequestParam LeadStatus status,
                             @RequestParam String phone,
                             @RequestParam String city,
                             @RequestParam String street,
                             @RequestParam String zip  ){
        Address adress = new Address(city, street, zip);
        leadService.addLead(email,company,status,adress,phone);

        return "redirect:/leads";
    }

    @GetMapping("/leads")
    public String showLeads(
            @RequestParam(required = false) LeadStatus status, // nullable параметр
            Model model
    ) {
        List<Lead> leads;
        if (status == null) {
            leads = leadService.findAll(); // Без фильтрации, если статус не указан
        } else {
            leads = leadService.findByStatus(status); // Фильтруем по указанному статусу
        }

        model.addAttribute("leads", leads);                 // Передача списка лидов в представление
        model.addAttribute("currentFilter", status);        // Текущий фильтр для отображения в представлении
        return "leads/list";                                // Переход к странице отображения лидов
    }
}
