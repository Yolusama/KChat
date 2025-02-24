package KChat.Service;

import KChat.Configuration.EmailConfig;
import KChat.Functional.EMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final EMailSender host;

    @Autowired
    public EmailService(EmailConfig config)
    {
        host = new EMailSender(config.getHost(),config.getAuthorizationCode());
    }

    public void sendTo(String to,String title,String content)
    {
        host.sendTo(to, title, content);
    }
}
