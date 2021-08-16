package com.ceng453.game.service;

import com.ceng453.game.config.EmailConfigurer;
import com.ceng453.game.model.Player;
import com.ceng453.game.model.ResetCode;
import com.ceng453.game.repository.PlayerRepository;
import com.ceng453.game.repository.ResetPasswordRepository;
import com.ceng453.game.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

/**
 * Implementation of Player Service
 */
@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService{
    /**
     * PlayerRepository Injection
     */
    private final PlayerRepository playerRepository;

    /**
     * PasswordEncoder Injection
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * AuthenticationProvider Injection
     */
    private final AuthenticationProvider authenticationProvider;

    /**
     * JwtUtil Injection
     */
    private final JwtUtil jwtUtil;

    /**
     * EmailConfigurer Injection
     */
    private final EmailConfigurer emailConfigurer;

    /**
     * ResetPasswordRepository Injection
     */
    private final ResetPasswordRepository resetPasswordRepository;

    /**
     * This method handles the given player information to be saved to database.
     * If password or username is not empty and also username is not token already, it insert a new player to the player table.
     * Otherwise, it returns error messages.
     * @param player information contains username,e-mail and password
     * @return Response message
     */
    @Override
    public ResponseEntity<String> register(Player player) {
        if(player.getPassword().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password can't be empty.");
        }
        else if (player.getUsername().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username can't be empty.");
        }
        else if(player.getEmail().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("E-mail can't be empty.");
        }
        else if (playerRepository.findByUsername(player.getUsername()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Try a different username.");
        }
        else{
            Player newPlayer = new Player();
            newPlayer.setUsername(player.getUsername());
            newPlayer.setPassword(passwordEncoder.encode(player.getPassword()));
            newPlayer.setEmail(player.getEmail());
            playerRepository.save(newPlayer);
            return ResponseEntity.ok().body("Register operation is successful.");
        }
    }

    /**
     * This method retrieves all player that are saved on database
     * @return list of all players
     */
    @Override
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok().body(playerRepository.findAll());
    }

    /**
     * This method retrieves the player's information with given username
     * @param username of the player to be retrieved
     * @return player information
     */
    @Override
    public ResponseEntity<?> getPlayer(String username) {
        Optional<Player> player = playerRepository.findByUsername(username);
        if (player.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User isn't found");
        }
        else {
            return ResponseEntity.ok().body(player.get());
        }
    }

    /**
     * This method updates the player's information of the player with given player id
     * @param player that contains new information of the player
     * @param id of the player to be updated
     * @return Response message
     */
    @Override
    public ResponseEntity<?>updatePlayer(Player player, Long id) {
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        if(optionalPlayer.isPresent()){
            Player updatePlayer = optionalPlayer.get();
            updatePlayer.setUsername(player.getUsername());
            updatePlayer.setEmail(player.getEmail());
            updatePlayer.setPassword(passwordEncoder.encode(player.getPassword()));
            playerRepository.save(updatePlayer);
            return ResponseEntity.ok().body("Player is updated successfully.");
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player isn't found.");
        }
    }

    /**
     * This method deletes the player with given id from database if the player is exists
     * Otherwise, returns a message that is "User isn't found."
     * @param id of the player to be deleted
     * @return Response message
     */
    @Override
    public ResponseEntity<?> deletePlayer(Long id) {
        Optional<Player> player = playerRepository.findById(id);
        if(player.isPresent()){
            playerRepository.deletePlayerById(id);
            return  ResponseEntity.ok().body("Player is deleted successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player isn't found.");
        }

    }

    /**
     * This method retrieves new generated token for a player if username and password are valid and player exists
     * Otherwise it returns error message
     * @param player that contains username and password
     * @return Response message
     */
    @Override
    public ResponseEntity<?> login(Player player) {
        if(player.getUsername().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username can't be empty.");
        }
        else if(player.getPassword().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password can't be empty.");
        }
        else{
            Optional<Player> optionalPlayer = playerRepository.findByUsername(player.getUsername());
            if(optionalPlayer.isPresent()){
                Authentication authentication = new UsernamePasswordAuthenticationToken(player.getUsername(),player.getPassword());
                try{
                    Authentication authenticatePlayer =authenticationProvider.authenticate(authentication);
                    String jwtToken = jwtUtil.generateToken(authenticatePlayer);
                    return ResponseEntity.ok().body(jwtToken);
                }
                catch (AuthenticationException e){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.toString());
                }
                catch (Exception e){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Incorrect username or password.");
            }
        }
    }

    /**
     * This method checks whether an actual player exists with given e-mail or not.
     * If player exists, it resets the password of the player with randomly generated password
     * Send new password as an e-mail to given e-mail address
     * If player is not exists, it returns error message
     * @param emailJson that is e-mail of the player
     * @return Response message
     * @throws JSONException when an exception caught
     */
    @Override
    public ResponseEntity<?> forgetPassword(String emailJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(emailJson);
        String email= jsonObject.get("email").toString();
        Optional<Player> optionalPlayer = playerRepository.findPlayerByEmail(email);
        if(optionalPlayer.isPresent()){
            Player player = optionalPlayer.get();
            String code = UUID.randomUUID().toString();
            ResetCode resetCode = new ResetCode();
            resetCode.setCode(code);
            resetCode.setPlayer(player);
            resetPasswordRepository.save(resetCode);
            sendEmail(code,player);
            return ResponseEntity.ok().body("Reset code is sent. Please check your e-mails.");
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account with given e-mail isn't found.");
        }
    }

    /**
     * This methods establishes configurations for sending an email to player
     * @param code unique verification code
     * @param player who forgot their passwords
     */
    private void sendEmail(String code, Player player){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailConfigurer.getHost());
        mailSender.setPort(emailConfigurer.getPort());
        mailSender.setUsername(emailConfigurer.getUsername());
        mailSender.setPassword(emailConfigurer.getPassword());
		Properties prop = new Properties();
        prop.setProperty("mail.smtp.starttls.enable","true");
        mailSender.setJavaMailProperties(prop);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("metuceng453@gmail.com");
        mailMessage.setTo(player.getEmail());
        mailMessage.setSubject("Password Change Request");
        mailMessage.setText("Hello "+ player.getUsername()+",\n"
                + "Please use this code to reset your password as soon as possible:\n" + code + "\n"
                + "Thanks & Regards,\n"
                + "Group 14");
        mailSender.send(mailMessage);
    }
}
