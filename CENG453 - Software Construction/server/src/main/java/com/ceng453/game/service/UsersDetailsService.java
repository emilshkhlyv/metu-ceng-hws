package com.ceng453.game.service;

import com.ceng453.game.model.Player;
import com.ceng453.game.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Implementation of UserDetailsService
 */
@Service
@RequiredArgsConstructor
public class UsersDetailsService implements UserDetailsService {

    /**
     * PlayerRepository Injection
     */
    private final PlayerRepository playerRepository;

    /**
     * This method overrides the method of loadUserByUsername of UserDetailsService
     * @param username Player's username
     * @return user
     * @throws UsernameNotFoundException when username or password is incorrect
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Player> player = playerRepository.findByUsername(username);
        if(player.isPresent()){
            Player user = player.get();
            return new User(user.getUsername(),user.getPassword(),new ArrayList<>());
        }
        else {
            throw new UsernameNotFoundException("Incorrect username or password.");
        }
    }
}
