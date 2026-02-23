package ru.mentee.power.crm.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Сущность компании. Связь 1:N с Lead через OneToMany/ManyToOne.
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 100)
    private String industry;

    @OneToMany(mappedBy = "company", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<LeadEntity> leads = new ArrayList<>();

    /**
     * Синхронизация обеих сторон связи: добавляет лид в коллекцию и устанавливает company у лида.
     */
    public void addLead(LeadEntity lead) {
        if (lead == null) {
            return;
        }
        if (!leads.contains(lead)) {
            leads.add(lead);
            lead.setCompany(this);
        }
    }

    /**
     * Синхронизация обеих сторон связи: удаляет лид из коллекции и обнуляет company у лида.
     */
    public void removeLead(LeadEntity lead) {
        if (lead == null) {
            return;
        }
        if (leads.remove(lead)) {
            lead.setCompany(null);
        }
    }
}
