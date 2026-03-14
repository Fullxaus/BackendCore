package ru.mentee.power.crm.spring.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ru.mentee.power.crm.domain.Invitee;

public interface InviteeRepository {

  List<Invitee> findAll();

  Optional<Invitee> findById(UUID id);

  Invitee save(Invitee invitee);

  void delete(Invitee invitee);

  boolean existsByEmail(String email);
}
