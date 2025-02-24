package KChat.Functional;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.util.Properties;

public class EMailSender {

  private EMailSender(){}
  private String from;
  private String password;

  public EMailSender(String host,String authorizationCode)
  {
      from = host;
      password = authorizationCode;
  }

  public  void sendTo(String to,String subject,String msg){
      Properties properties = System.getProperties();

      // 连接协议
      properties.put("mail.transport.protocol", "smtp");
      // 验证权限
      properties.put("mail.smtp.auth", "true");
      // qq是smtp.qq.com
      properties.put("mail.smtp.host", "smtp.qq.com");
      // ssl邮箱端口
      properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      properties.put("mail.smtp.socketFactory.port", "465");

      /*properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      properties.put("mail.smtp.socketFactory.port", 587);*/
      properties.put("mail.smtp.socketFactory.fallback","false");

      Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator(){
          protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
              return new javax.mail.PasswordAuthentication(from, password);
          }
      });
      try {
          MimeMessage message = new MimeMessage(session);
          message.setFrom(new InternetAddress(from));
          message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
          message.setSubject(subject);

          MimeBodyPart messageBodyPart = new MimeBodyPart();
          messageBodyPart.setText(msg);
          MimeMultipart multipart = new MimeMultipart();
          multipart.addBodyPart(messageBodyPart);
          message.setContent(multipart);
          Transport.send(message);
      }
      catch (Exception ex){
          ex.printStackTrace();
      }
  }
}
