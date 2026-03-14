package ru.mentee.power.crm.spring.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Invitee;

@Repository
public class InMemoryInviteeRepository implements InviteeRepository {

  private final Map<UUID, Invitee> storage = new ConcurrentHashMap<>();

  @Override
  public List<Invitee> findAll() {
    return List.copyOf(storage.values());
  }

  @Override
  public Optional<Invitee> findById(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public Invitee save(Invitee invitee) {
    storage.put(invitee.getId(), invitee);
    return invitee;
  }

  @Override
  public void delete(Invitee invitee) {
    storage.remove(invitee.getId());
  }

  @Override
  public boolean existsByEmail(String email) {
    return storage.values().stream().anyMatch(i -> email != null && email.equals(i.getEmail()));
  }
}
