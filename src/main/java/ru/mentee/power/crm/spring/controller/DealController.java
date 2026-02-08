package ru.mentee.power.crm.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.spring.service.DealService;
import ru.mentee.power.crm.service.LeadService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/deals")
public class DealController {
    private final DealService dealService;
    private final LeadService leadService;

    public DealController(DealService dealService, LeadService leadService) {
        this.dealService = dealService;
        this.leadService = leadService;
    }

    @GetMapping
    public String listDeals(Model model) {
        var deals = dealService.getAllDeals();
        model.addAttribute("deals", deals);
        model.addAttribute("leadNames", buildLeadNamesMap(deals));
        return "deals/list";
    }

    @GetMapping("/kanban")
    public String kanbanView(Model model) {
        var dealsByStatus = dealService.getDealsByStatusForKanban();
        var allDeals = dealService.getAllDeals();
        model.addAttribute("dealsByStatus", dealsByStatus);
        model.addAttribute("statuses", DealStatus.values());
        model.addAttribute("leadNames", buildLeadNamesMap(allDeals));
        return "deals/kanban";
    }

    private Map<UUID, String> buildLeadNamesMap(List<Deal> deals) {
        Map<UUID, String> leadNames = new HashMap<>();
        for (Deal deal : deals) {
            leadService.findById(deal.getLeadId())
                    .ifPresent(lead -> leadNames.put(deal.getLeadId(), lead.company()));
        }
        return leadNames;
    }

    @GetMapping("/convert/{leadId}")
    public String showConvertForm(@PathVariable UUID leadId, Model model) {
        var lead = leadService.findById(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
        model.addAttribute("lead", lead);
        return "deals/convert";
    }

    @PostMapping("/convert")
    public String convertLeadToDeal(@RequestParam UUID leadId, @RequestParam BigDecimal amount) {
        dealService.convertLeadToDeal(leadId, amount);
        return "redirect:/deals";
    }

    @PostMapping("/{id}/transition")
    public String transitionStatus(@PathVariable UUID id, @RequestParam DealStatus newStatus) {
        dealService.transitionDealStatus(id, newStatus);
        return "redirect:/deals/kanban";
    }
}
