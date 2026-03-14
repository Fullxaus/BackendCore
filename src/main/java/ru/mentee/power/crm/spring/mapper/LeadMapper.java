package ru.mentee.power.crm.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.mentee.power.crm.entity.LeadEntity;
import ru.mentee.power.crm.spring.dto.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.LeadResponse;
import ru.mentee.power.crm.spring.dto.UpdateLeadRequest;

@Mapper // componentModel и unmappedTargetPolicy заданы глобально в compilerArgs
public interface LeadMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "status", constant = "NEW")
  @Mapping(target = "phone", constant = "-")
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "street", ignore = true)
  @Mapping(target = "zip", ignore = true)
  @Mapping(target = "companyName", source = "company")
  LeadEntity toEntity(CreateLeadRequest dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "status", constant = "NEW")
  @Mapping(target = "phone", constant = "-")
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "street", ignore = true)
  @Mapping(target = "zip", ignore = true)
  @Mapping(target = "companyName", source = "company")
  LeadEntity toEntityFromApi(ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest dto);

  @Mapping(target = "company", source = "companyName")
  @Mapping(
      target = "createdAt",
      expression =
          "java(entity.getCreatedAt() != null ? LocalDateTime.ofInstant(entity.getCreatedAt(), java.time.ZoneOffset.UTC) : null)")
  @Mapping(target = "firstName", ignore = true)
  @Mapping(target = "lastName", ignore = true)
  LeadResponse toResponse(LeadEntity entity);

  @Mapping(target = "company", source = "companyName")
  @Mapping(
      target = "createdAt",
      expression =
          "java(entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(java.time.ZoneOffset.UTC) : null)")
  @Mapping(target = "firstName", constant = "-")
  @Mapping(target = "lastName", constant = "-")
  ru.mentee.power.crm.spring.dto.generated.LeadResponse toGeneratedResponse(LeadEntity entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "phone", ignore = true)
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "street", ignore = true)
  @Mapping(target = "zip", ignore = true)
  @Mapping(target = "companyName", source = "company")
  void updateEntity(UpdateLeadRequest dto, @MappingTarget LeadEntity entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "phone", ignore = true)
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "street", ignore = true)
  @Mapping(target = "zip", ignore = true)
  @Mapping(target = "companyName", source = "company")
  void updateEntityFromApi(
      ru.mentee.power.crm.spring.dto.generated.UpdateLeadRequest dto,
      @MappingTarget LeadEntity entity);
}
