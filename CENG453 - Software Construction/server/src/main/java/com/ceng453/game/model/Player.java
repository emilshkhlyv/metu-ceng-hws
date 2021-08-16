package com.ceng453.game.model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * Model of Player
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ApiModel(description = "Player entity details")
@Table(name="PLAYER")
public class Player {

    /**
     * Unique generated id for each player
     */
    @Id
    @GeneratedValue
    @Column(name = "ID")
    @ApiModelProperty(notes = "The unique generated id of a player")
    private Long id;

    /**
     * Username of the player
     */

    @Column(name = "USERNAME",unique = true)
    @NotNull
    @ApiModelProperty(notes = "The unique username of a player")
    private String username;

    /**
     * Password of the player
     */

    @Column(name = "PASSWORD")
    @NotNull
    @ApiModelProperty(notes = "The password of a player")
    private String password;

    /**
     * E-mail of the player
     */
    @Column(name = "EMAIL")
    @NotNull
    @Email
    @ApiModelProperty(notes = "The e-mail of a player")
    private String email;

}
