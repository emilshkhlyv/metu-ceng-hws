package com.ceng453.game.api;

import com.ceng453.game.service.ResetPasswordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class of Reset Password related end-points
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resetPassword")
@Api(value = "Reset Password API Documentation")
public class ResetPasswordController {

    /**
     * Injection of ResetPasswordService
     */
    private final ResetPasswordService resetPasswordService;

    /**
     * This method handles POST request to change password of the player
     * @param username of the player
     * @param code sent via e-mail
     * @param newPassword of the player
     * @return Response message
     */
    @ApiOperation(  value = "Reset password using username and verification code",
                    notes = "Username, code and new password needed",
                    response = ResponseEntity.class)
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestParam String username,@RequestParam String code, @RequestParam String newPassword){
        return resetPasswordService.changePassword(username,code,newPassword);
    }
}
