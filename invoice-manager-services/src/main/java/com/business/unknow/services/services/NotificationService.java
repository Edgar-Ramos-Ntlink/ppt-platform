package com.business.unknow.services.services;

import com.business.unknow.model.dto.services.NotificationDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Notification;
import com.business.unknow.services.mapper.NotificationMapper;
import com.business.unknow.services.repositories.NotificationRepository;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

  @Autowired private NotificationRepository repository;

  @Autowired private NotificationMapper mapper;

  private List<NotificationDto> notifications;

  @PostConstruct
  public void buildNotifications() {
    log.info("Loading notifications");
    notifications = mapper.getDtosFromEntities(repository.findAll());
  }

  public List<NotificationDto> getNotifications() {
    return notifications;
  }

  public Optional<NotificationDto> getNotificationByType(String notification) {
    return notifications.stream().filter(a -> a.getEvent().equals(notification)).findFirst();
  }

  public NotificationDto postNotifications(NotificationDto notificationDto) {
    Notification notification = repository.save(mapper.getEntityFromDto(notificationDto));
    buildNotifications();
    return mapper.getDtoFromEntity(notification);
  }

  public void deleteNotifications(int id) throws InvoiceManagerException {
    Optional<NotificationDto> notification =
        notifications.stream().filter(a -> a.getId() == id).findFirst();
    if (notification.isPresent()) {
      repository.delete(mapper.getEntityFromDto(notification.get()));
      buildNotifications();
    } else {
      throw new InvoiceManagerException(
          String.format("Notification with the id %d does not exists ", id),
          HttpStatus.CONFLICT.value());
    }
  }

  public NotificationDto updateNotifications(NotificationDto notificationDto)
      throws InvoiceManagerException {
    Optional<NotificationDto> notification =
        notifications.stream().filter(a -> a.getId() == notificationDto.getId()).findFirst();
    if (notification.isPresent()) {
      NotificationDto deltaNotificationDto =
          mapper.getDtoFromEntity(repository.save(mapper.getEntityFromDto(notification.get())));
      buildNotifications();
      return deltaNotificationDto;
    } else {
      throw new InvoiceManagerException(
          String.format("Notification with the id %d does not exists ", notificationDto.getId()),
          HttpStatus.CONFLICT.value());
    }
  }
}
