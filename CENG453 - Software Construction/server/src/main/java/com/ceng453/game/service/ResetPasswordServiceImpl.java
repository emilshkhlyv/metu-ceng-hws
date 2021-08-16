package com.ceng453.game.service;

import com.ceng453.game.model.Player;
import com.ceng453.game.model.ResetCode;
import com.ceng453.game.repository.PlayerRepository;
import com.ceng453.game.repository.ResetPasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of Reset Password Service Interface
 */
@Service
@RequiredArgsConstructor
public class ResetPasswordServiceImpl implements ResetPasswordService{

    /**
     * PlayerRepository Injection
     */
    private final PlayerRepository playerRepository;

    /**
     * ResetPasswordRepository Injection
     */
    private final ResetPasswordRepository resetPasswordRepository;

    /**
     * PasswordEncoder Injection
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * This method handles the change password of the player. If player gives username, code and new password information
     * correctly, the password will be changed successfully. Otherwise, it returns error message.
     * @param username of the player
     * @param code sent via e-mail
     * @param newPassword of the player
     * @return response message
     */
    @Override
    public ResponseEntity<?> changePassword(String username,String code, String newPassword) {
        if(username.isEmpty() || code.isEmpty() || newPassword.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username,code or new password is empty.");
        }
        else{
            Optional<Player> optionalPlayer = playerRepository.findByUsername(username);
            if(optionalPlayer.isPresent()){
                Player player = optionalPlayer.get();
                Optional<ResetCode> optionalResetCode = resetPasswordRepository.findByPlayer(player);
                if(optionalResetCode.isPresent()){
                    ResetCode resetCode = optionalResetCode.get();
                    if(resetCode.getCode().equals(code)){
                        player.setPassword(passwordEncoder.encode(newPassword));
                        playerRepository.save(player);
                        resetPasswordRepository.delete(resetCode);
                        return ResponseEntity.ok().body("Password is changed successfully.");
                    }
                    else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code isn't correct");
                }
                else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no change password request.");
            }
            else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User isn't found.");
        }
    }
}
