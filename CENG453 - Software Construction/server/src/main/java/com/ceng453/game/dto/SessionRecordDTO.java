package com.ceng453.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data Transfer Object class used for listing leaderboard
 */
@AllArgsConstructor
@Getter
@Setter
public class SessionRecordDTO {
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
    private LocalDateTime date;
}
