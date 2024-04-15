package fpt.edu.capstone.service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface EmailService {
    void sendResetPasswordEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException;
}
