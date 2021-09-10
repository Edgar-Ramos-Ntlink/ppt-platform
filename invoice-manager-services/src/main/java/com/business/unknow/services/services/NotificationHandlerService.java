package com.business.unknow.services.services;

import com.business.unknow.builder.EmailConfigBuilder;
import com.business.unknow.model.dto.services.NotificationDto;
import com.business.unknow.model.error.InvoiceCommonException;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.config.properties.GlocalConfigs;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class NotificationHandlerService {

  @Autowired private NotificationService notificationService;

  @Autowired private GlocalConfigs glocalConfigs;

  @Autowired private MailService mailService;

  private static final Logger log = LoggerFactory.getLogger(NotificationHandlerService.class);

  public void sendNotification(String notificationType, String extraDetails)
      throws InvoiceManagerException {
    Optional<NotificationDto> notification =
        notificationService.getNotificationByType(notificationType);
    if (notification.isPresent()) {
      EmailConfigBuilder builder =
          new EmailConfigBuilder()
              .addReceptors(notification.get().getEmails())
              .setAsunto(notification.get().getResume())
              .setCuerpo(notification.get().getNotification().concat(extraDetails))
              .setEmisor(glocalConfigs.getEmail())
              .setPort(glocalConfigs.getEmail())
              .setPwEmisor(glocalConfigs.getEmailPw())
              .setDominio(glocalConfigs.getEmailHost());
      CompletableFuture.runAsync(
          () -> {
            try {
              mailService.sendEmail(builder.build());
            } catch (InvoiceCommonException e) {
              log.info("Error sending notification email");
            }
          });
    }
  }
}
