package com.ceng453.game.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Player game object class
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    /**
     * id of the player
     */
    private Long id;

    /**
     * username of the player
     */
    private String username;

    /**
     * email of the player
     */
    private String email;

    /**
     * password of the player, stored encrypted
     */
    private String password;
}
