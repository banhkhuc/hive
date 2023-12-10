package fpt.edu.capstone.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmailServiceImplTest {

    @Mock
    private JavaMailSender mockJavaMailSender;

    @InjectMocks
    private EmailServiceImpl emailServiceImplUnderTest;

    @Test
    public void testSendResetPasswordEmail() throws Exception {
        final MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        mimeMessage.setFrom("hive.connect.social@gmail.com");
        MimeMessageHelper mimeMessageHelper;
        mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom("hive.connect.social@gmail.com", "Hive Connect Support");
        mimeMessageHelper.setTo("recipientEmail@gmail.com");
        when(mockJavaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailServiceImplUnderTest.sendResetPasswordEmail("recipientEmail@gmail.com", "link");
        verify(mockJavaMailSender).send(any(MimeMessage.class));
    }

    @Test
    public void testSendResetPasswordEmail_JavaMailSenderSendThrowsMailException() {
        final MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mockJavaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(MailException.class).when(mockJavaMailSender).send(any(MimeMessage.class));
        assertThatThrownBy(
                () -> emailServiceImplUnderTest.sendResetPasswordEmail("recipientEmail", "link"))
                .isInstanceOf(Exception.class);
    }
}
