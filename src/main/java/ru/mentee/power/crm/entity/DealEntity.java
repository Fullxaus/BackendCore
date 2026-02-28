package ru.mentee.power.crm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "deals")
@Getter
@Setter
@NoArgsConstructor
public class DealEntity {

    @Id
    private UUID id;

    @Column(name = "lead_id", nullable = false)
    private UUID leadId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DealProduct> dealProducts = new ArrayList<>();

    public void addDealProduct(DealProduct dealProduct) {
        dealProducts.add(dealProduct);
        dealProduct.setDeal(this);
    }

    public void removeDealProduct(DealProduct dealProduct) {
        dealProducts.remove(dealProduct);
        dealProduct.setDeal(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DealEntity that = (DealEntity) o;
        // Для JPA entity сравниваем только по ID
        // Если ID null - объекты не равны (новые объекты считаются разными)
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        // Для JPA entity hashCode только по ID
        // Если ID null - используем System.identityHashCode для новых объектов
        return id != null ? Objects.hash(id) : System.identityHashCode(this);
    }
}
