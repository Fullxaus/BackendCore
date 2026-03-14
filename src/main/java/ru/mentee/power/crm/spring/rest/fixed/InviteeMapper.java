package ru.mentee.power.crm.spring.rest.fixed;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.power.crm.domain.Invitee;
import ru.mentee.power.crm.spring.rest.fixed.dto.CreateInviteeRequest;
import ru.mentee.power.crm.spring.rest.fixed.dto.InviteeResponse;

/**
 * Маппер Entity ↔ DTO  (fixed API).
 * @see ru.mentee.power.crm.spring.rest.fixed.InviteeController
 */
@Mapper
public interface InviteeMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", constant = "NEW")
  @Mapping(target = "createdAt", ignore = true)
  Invitee toEntity(CreateInviteeRequest request);

  InviteeResponse toResponse(Invitee invitee);
}
