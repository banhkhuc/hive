package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.ConfirmToken;

import java.io.IOException;

public interface ConfirmTokenService {
    void saveConfirmToken(ConfirmToken confirmToken);

    void verifyEmailUser(String email, String token)throws IOException;

    ConfirmToken getByUserId(long userId);

    ConfirmToken getByConfirmToken(String token);
}
