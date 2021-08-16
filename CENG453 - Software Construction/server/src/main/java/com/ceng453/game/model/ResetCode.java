package com.ceng453.game.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Model of ResetCode
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ApiModel(description = "Reset code table")
@Table(name="RESETCODE")
public class ResetCode {
    /**
     * Unique generated id for each reset code
     */
    @Id
    @GeneratedValue
    @Column(name = "ID")
    @ApiModelProperty(notes = "The unique generated id of each reset code")
    private Long id;

    /**
     * Code that will be used to reset password
     */
    @Column(name = "CODE")
    @ApiModelProperty(notes = "The unique code used for reset password")
    private String code;

    /**
     * Player of the reset code
     */
    @OneToOne
    @JoinColumn(name = "USERNAME", nullable = false)
    @ApiModelProperty(notes = "The player of the session")
    Player player;
}
