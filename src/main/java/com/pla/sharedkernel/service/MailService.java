package com.pla.sharedkernel.service;

import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.activation.FileDataSource;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Samir on 5/27/2015.
 */
@Service
public class MailService {

    private JavaMailSenderImpl mailSender;

    @Autowired
    public MailService(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public Map<String, Object> sendMailWithAttachment(final String subject, final String messageBody, final List<EmailAttachment> attachments, final String[] recipientMailAddress) throws DocumentException,
            IOException, Exception {
        final Map<String, Object> resultMap = new HashMap<String, Object>();
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                Properties properties = new Properties();
                ClassLoader bundleClassLoader = Thread.currentThread().getContextClassLoader();
                properties.load(bundleClassLoader.getResourceAsStream("mailsettings.properties"));
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, "UTF-8");
                message.setTo(recipientMailAddress);
                message.setFrom(properties.getProperty("mail.sentFrom"));
                message.setSubject(subject);
                message.setText(messageBody, true);
                for (final EmailAttachment attachment : attachments) {
                    String attachmentName = attachment.getFileName();
                    FileDataSource dataSource = new FileDataSource(attachment.getFile()) {
                        @Override
                        public String getContentType() {
                            return attachment.getContentType();
                        }
                    };
                    message.addAttachment(attachmentName, dataSource);
                }
            }
        };
        this.mailSender.send(preparator);
        return resultMap;
    }

}


