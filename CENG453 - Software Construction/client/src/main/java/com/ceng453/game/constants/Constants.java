package com.ceng453.game.constants;

import com.ceng453.game.gameObjects.Player;

public class Constants {
    /**
     * API url
     */
    public static final String API = "http://144.122.71.168:8083/server14/api/";

    /**
     * jwtToken
     */
    public static String jwtToken;

    /**
     * Player instance
     */
    public static Player player;

    /**
     * sessionId for storing the game
     */
    public static Long sessionId;

    /**
     * Score of player
     */
    public static Long playerScore=0L;

    /**
     * Overall score all over game
     */
    public static Long overallScore = 0L;

    /**
     * score of opponent
     */
    public static Long opponentScore = 0L;

    /**
     * error message
     */
    public static final String CON_ERROR = "Unable to connect. Please check your internet connection.";

    public static final Integer PORT = 12345;
}
