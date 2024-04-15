package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;


    public void sendResetPasswordEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom(sender, "Hive Connect Support");
            mimeMessageHelper.setTo(recipientEmail);

            String subject = "Here's the link to reset your password";

            String content = "Hello,"
                    + "\nYou have requested to reset your password. "
                    + "\nClick the link below to change your password: "
                    + link
                    + "\nIgnore this email if you do remember your password, "
                    + "or you have not made the request.";

            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, true);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw e;
        }
    }
}
