package ru.mentee.power.crm.spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Controller
public class LeadController {
    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping("/leads/new")//получение данных
    public String showCreateForm(Model model) {
        model.addAttribute("statuses", LeadStatus.values());
        return "leads/create";
    }

    @PostMapping("/leads")//отправка данных на сервер
    public String createLead(
            @RequestParam String email,
            @RequestParam String company,
            @RequestParam LeadStatus status) {
        Address address = new Address("-", "-", "-");
        leadService.addLead(email, company, status, address, "-");
        return "redirect:/leads";
    }

    @PostMapping("/lead")
    public String createLeadFull(
            @RequestParam String email,
            @RequestParam String company,
            @RequestParam LeadStatus status,
            @RequestParam String phone,
            @RequestParam String city,
            @RequestParam String street,
            @RequestParam String zip) {
        Address address = new Address(city, street, zip);
        leadService.addLead(email, company, status, address, phone);
        return "redirect:/leads";
    }

    @GetMapping("/leads")
    public String showLeads(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            Model model) {
        List<Lead> leads = leadService.findLeads(search, status);
        model.addAttribute("leads", leads);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("status", status != null ? status : "");
        return "leads/list";
    }

    @GetMapping("/leads/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Optional<Lead> optionalLead = leadService.findById(id);
        Lead lead = optionalLead.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
        model.addAttribute("lead", lead);
        model.addAttribute("statuses", LeadStatus.values());
        return "edit";
    }


    @PostMapping("/leads/{id}")
    public String updateLead(
            @PathVariable UUID id,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam String company,
            @RequestParam LeadStatus status) {
        String safePhone = (phone == null || phone.isBlank()) ? "-" : phone;
        leadService.update(id, email, safePhone, company, status);
        return "redirect:/leads";
    }




    @PostMapping("/leads/{id}/delete")
    public String deleteLead(@PathVariable UUID id) {
        leadService.delete(id);
        return "redirect:/leads";
    }
}
