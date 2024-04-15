package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.ConfirmToken;
import fpt.edu.capstone.repository.ConfirmTokenRepository;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ConfirmTokenServiceImplTest {

    private ConfirmTokenServiceImpl confirmTokenServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        confirmTokenServiceImplUnderTest = new ConfirmTokenServiceImpl();
        confirmTokenServiceImplUnderTest.confirmTokenRepository = mock(ConfirmTokenRepository.class);
    }

    @Test
    public void testSaveConfirmToken() {
        final ConfirmToken confirmToken = new ConfirmToken(1L, 1L, "confirmationToken",
                LocalDateTime.now());
        final ConfirmToken token = new ConfirmToken(1L, 1L, "confirmationToken", LocalDateTime.now());
        when(confirmTokenServiceImplUnderTest.confirmTokenRepository.save(any(ConfirmToken.class))).thenReturn(token);
        confirmTokenServiceImplUnderTest.saveConfirmToken(confirmToken);
        verify(confirmTokenServiceImplUnderTest.confirmTokenRepository).save(any(ConfirmToken.class));
    }

    @Test
    public void testVerifyEmailUser() throws Exception {
        confirmTokenServiceImplUnderTest.verifyEmailUser("email@gmail.com", "token");
    }

    @Test
    public void testVerifyEmailUser_ThrowsIOException() {
        assertThatThrownBy(() -> confirmTokenServiceImplUnderTest.verifyEmailUser("email", "token"))
                .isInstanceOf(IOException.class);
    }

    @Test
    public void testGetByUserId() {
        final ConfirmToken token = new ConfirmToken(1L, 1L, "confirmationToken", LocalDateTime.now());
        when(confirmTokenServiceImplUnderTest.confirmTokenRepository.getByUserId(1L)).thenReturn(token);
        final ConfirmToken result = confirmTokenServiceImplUnderTest.getByUserId(1L);
    }

    @Test
    public void testGetByConfirmToken() {
        final ConfirmToken token = new ConfirmToken(1L, 1L, "confirmationToken", LocalDateTime.now());
        when(confirmTokenServiceImplUnderTest.confirmTokenRepository.getByConfirmationToken("token")).thenReturn(token);
        final ConfirmToken result = confirmTokenServiceImplUnderTest.getByConfirmToken("token");
    }
}
