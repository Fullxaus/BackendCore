package ru.mentee.power.crm.spring.exception;

/**
 * Исключение выбрасывается когда запрошенная сущность не найдена. Маппится на HTTP 404 Not Found.
 */
public class EntityNotFoundException extends BusinessException {

  private final String entityType;
  private final String entityId;

  public EntityNotFoundException(String entityType, String entityId) {
    super(entityType + " not found with id: " + entityId);
    this.entityType = entityType;
    this.entityId = entityId;
  }

  public EntityNotFoundException(String message) {
    super(message);
    this.entityType = null;
    this.entityId = null;
  }

  public String getEntityType() {
    return entityType;
  }

  public String getEntityId() {
    return entityId;
  }
}
