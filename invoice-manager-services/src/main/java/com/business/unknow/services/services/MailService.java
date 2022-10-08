package com.business.unknow.services.services;

import com.business.unknow.model.config.MailContent;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.PreencodedMimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class MailService {

  @Value("${invoce.email-host}")
  private String host;

  @Value("${invoce.email-port}")
  private String port;

  @Value("${invoce.email}")
  private String email;

  @Value("${invoce.email-pw}")
  private String password;

  private Session getMailSession() {
    Properties props = System.getProperties();
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.ssl.trust", host);
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.port", port);

    return Session.getInstance(
        props,
        new Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(email, password);
          }
        });
  }

  public void sendEmail(List<String> recipients, MailContent content) {
    Session session = getMailSession();
    MimeMessage message = new MimeMessage(session);
    try {
      message.setFrom(new InternetAddress(email));
      for (String recipient : recipients) {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
      }
      message.setSubject(content.getSubject());

      // Create message part
      Multipart multipart = new MimeMultipart();
      BodyPart messageBodyPart = new MimeBodyPart();
      // Mail body text
      String htmlText = "<img src=\"cid:image\" width=\"500\"><br/><br/>" + content.getBodyText();
      messageBodyPart.setContent(htmlText, "text/html; charset=UTF-8");
      multipart.addBodyPart(messageBodyPart);

      if (!Objects.isNull(content.getAttachments())) {
        for (Map.Entry<String, MailContent.MailFile> attachment :
            content.getAttachments().entrySet()) {
          BodyPart fileBodyPart = new PreencodedMimeBodyPart("base64");
          fileBodyPart.setText(attachment.getKey());
          ByteArrayDataSource rawData =
              new ByteArrayDataSource(
                  attachment.getValue().getData().getBytes(), attachment.getValue().getType());
          fileBodyPart.setFileName(attachment.getKey());
          fileBodyPart.setDataHandler(new DataHandler(rawData));
          multipart.addBodyPart(fileBodyPart);
        }
      }
      // Send the complete message parts
      message.setContent(multipart);

      Transport transport = session.getTransport("smtp");
      transport.connect(host, email, password);
      transport.sendMessage(message, message.getAllRecipients());
      log.info("A mail was sent to {}, with subject {}", recipients, content.getSubject());
      transport.close();
    } catch (MessagingException e) {
      log.error("Error sending mail", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          String.format("Disculpa el inconveniente, error enviando el correo a [%s]", recipients));
    }
  }
}
