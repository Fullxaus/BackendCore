package ru.mentee.power.crm.spring.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadForm;
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

    @GetMapping("/leads/new")
    public String showCreateForm(Model model) {
        model.addAttribute("lead", new LeadForm());
        model.addAttribute("statuses", LeadStatus.values());
        model.addAttribute("leadId", null);
        model.addAttribute("errors", null);
        return "leads/form";
    }

    @PostMapping("/leads")
    public String createLead(
            @ModelAttribute("lead") @Valid LeadForm lead,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", LeadStatus.values());
            model.addAttribute("leadId", null);
            model.addAttribute("errors", result);
            return "leads/form";
        }
        Address address = new Address("-", "-", "-");
        leadService.addLead(lead.email(), lead.name(), lead.status(), address, lead.phone());
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
        LeadStatus status = parseLeadStatus(lead.status());
        LeadForm form = new LeadForm(
                lead.company(),
                lead.contact().email(),
                lead.contact().phone(),
                status
        );
        model.addAttribute("lead", form);
        model.addAttribute("leadId", id);
        model.addAttribute("statuses", LeadStatus.values());
        model.addAttribute("errors", null);
        return "leads/form";
    }

    @PostMapping("/leads/{id}")
    public String updateLead(
            @PathVariable UUID id,
            @ModelAttribute("lead") @Valid LeadForm lead,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("leadId", id);
            model.addAttribute("statuses", LeadStatus.values());
            model.addAttribute("errors", result);
            return "leads/form";
        }
        String safePhone = (lead.phone() == null || lead.phone().isBlank()) ? "-" : lead.phone();
        leadService.update(id, lead.email(), safePhone, lead.name(), lead.status());
        return "redirect:/leads";
    }



    @PostMapping("/leads/{id}/delete")
    public String deleteLead(@PathVariable UUID id) {
        leadService.delete(id);
        return "redirect:/leads";
    }

    private static LeadStatus parseLeadStatus(String value) {
        if (value == null || value.isBlank()) {
            return LeadStatus.NEW;
        }
        try {
            return LeadStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return LeadStatus.NEW;
        }
    }
}
