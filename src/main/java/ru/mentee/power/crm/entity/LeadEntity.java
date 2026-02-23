package ru.mentee.power.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "leads")
@Getter
@Setter
@NoArgsConstructor
public class LeadEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 50)
    private String phone;

    @Column(nullable = false, length = 255)
    private String company;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(length = 100)
    private String city;

    @Column(length = 255)
    private String street;

    @Column(length = 20)
    private String zip;

    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private Long version;

    @PrePersist
    void ensureId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeadEntity that = (LeadEntity) o;
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
