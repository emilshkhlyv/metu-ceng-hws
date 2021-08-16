package com.ceng453.game.service;

import com.ceng453.game.model.Player;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PlayerService {
    ResponseEntity<?> register(Player player);

    ResponseEntity<List<Player>> getAllPlayers();

    ResponseEntity<?> getPlayer(String username);

    ResponseEntity<?> updatePlayer(Player player, Long id);

    ResponseEntity<?> deletePlayer(Long id);

    ResponseEntity<?> login(Player player);

    ResponseEntity<?> forgetPassword(String emailJson) throws JSONException;
}
