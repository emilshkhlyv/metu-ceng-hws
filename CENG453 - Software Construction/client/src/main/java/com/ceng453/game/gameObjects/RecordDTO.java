package com.ceng453.game.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object class used for listing leaderboard
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class RecordDTO {
    /**
     * username of the player in the current session record
     */
    private String username;

    /**
     * score of the current session record
     */
    private Long score;

    /**
     * date of the current session record
     */
    private String date;
}
