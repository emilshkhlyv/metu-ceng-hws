package com.ceng453.game.service;

import org.springframework.http.ResponseEntity;

/**
 * Reset Password Service Interface
 */
public interface ResetPasswordService {
    ResponseEntity<?> changePassword(String username,String code, String newPassword);
}
