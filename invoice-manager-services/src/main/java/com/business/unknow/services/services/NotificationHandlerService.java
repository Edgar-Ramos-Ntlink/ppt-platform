package com.business.unknow.services.services;

import com.business.unknow.model.config.EmailConfig;
import com.business.unknow.model.dto.services.NotificationDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.config.properties.GlocalConfigs;
import com.google.common.collect.ImmutableList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationHandlerService {

  @Autowired private NotificationService notificationService;

  @Autowired private GlocalConfigs glocalConfigs;

  @Autowired private MailService mailService;

  public void sendNotification(String notificationType, String extraDetails)
      throws InvoiceManagerException {
    Optional<NotificationDto> notification =
        notificationService.getNotificationByType(notificationType);
    if (notification.isPresent()) {
      EmailConfig emailConfig =
          EmailConfig.builder()
              .receptor(ImmutableList.of(notification.get().getEmails()))
              .asunto(notification.get().getResume())
              .cuerpo(notification.get().getNotification().concat(extraDetails))
              .emisor(glocalConfigs.getEmail())
              .port(glocalConfigs.getEmail())
              .pwEmisor(glocalConfigs.getEmailPw())
              .dominio(glocalConfigs.getEmailHost())
              .build();
      CompletableFuture.runAsync(
          () -> {
            // TODO:REFACTOR THIS CODE
            // mailService.sendEmail(emailConfig);

          });
    }
  }
}
