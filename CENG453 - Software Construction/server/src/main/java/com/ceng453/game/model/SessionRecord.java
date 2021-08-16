package com.ceng453.game.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Model of SessionRecord
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ApiModel(description = "Session record entity details")
@Table(name="SESSIONRECORD")
public class SessionRecord {
    /**
     * Unique generated id for each session
     */
    @Id
    @GeneratedValue
    @Column(name = "ID")
    @ApiModelProperty(notes = "The unique generated id of a session record")
    private Long id;

    /**
     * Total score of the player
     */
    @Column(name="SCORE")
    @ApiModelProperty(notes = "The score of the player in session")
    private Long score;

    /**
     * Time of the session creation
     */
    @Column(name = "DATE")
    @ApiModelProperty(notes = "The date of session created")
    private LocalDateTime date;

    /**
     * Player of the session
     */
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    @ApiModelProperty(notes = "The player of the session")
    Player player;
}
