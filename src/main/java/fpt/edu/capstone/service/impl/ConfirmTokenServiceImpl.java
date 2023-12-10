package fpt.edu.capstone.service.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import fpt.edu.capstone.entity.ConfirmToken;
import fpt.edu.capstone.repository.ConfirmTokenRepository;
import fpt.edu.capstone.service.ConfirmTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ConfirmTokenServiceImpl implements ConfirmTokenService {
    @Autowired
    ConfirmTokenRepository confirmTokenRepository;

    @Override
    public void saveConfirmToken(ConfirmToken confirmToken) {
        confirmTokenRepository.save(confirmToken);
    }

    @Override
    public void verifyEmailUser(String email, String token) throws IOException {
        Email from = new Email("hive.connect.social@gmail.com");
        Email to = new Email(email);

        String subject = "Welcome to Hive Connect Social";
        Content content = new Content("text/html",
                "To confirm your account, please click here : "+"http://localhost:4200/auth/confirm-token?token="+token);

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("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");
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);

        System.out.println(response.getStatusCode());
        System.out.println(response.getHeaders());
        System.out.println(response.getBody());
    }

    @Override
    public ConfirmToken getByUserId(long userId) {
        return confirmTokenRepository.getByUserId(userId);
    }

    @Override
    public ConfirmToken getByConfirmToken(String token) {
        return confirmTokenRepository.getByConfirmationToken(token);
    }
}
