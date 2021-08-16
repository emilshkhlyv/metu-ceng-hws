package com.ceng453.game.controller;

import com.ceng453.game.constants.Constants;
import com.ceng453.game.gameObjects.Card;
import com.ceng453.game.gameObjects.Deck;
import com.ceng453.game.gameObjects.Hand;
import com.ceng453.game.gameObjects.Pile;
import com.ceng453.game.service.GameService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static com.ceng453.game.constants.Constants.*;
import static com.ceng453.game.constants.Constants.overallScore;

/**
 * Level 4 controller class
 */
public class Level4Controller implements Initializable {
    @FXML
    AnchorPane level4pane;

    @FXML
    ImageView deckImage;

    @FXML
    Label roundLabel;

    @FXML
    Label opponentScoreLabel;

    @FXML
    Label yourScoreLabel;

    // Streams
    private DataInputStream  sockInputStream;
    private DataOutputStream sockOutputStream;

    private KeyCombination controlNine;

    // Table variables
    private Deck deck;
    private HBox playerCards;
    private HBox opponentCards;
    private StackPane pileCards;
    private Pane firstClosedCards;
    private Rectangle reo;

    private Hand threeCard;
    private HBox threeCards;

    private Hand player;
    private Hand opponent;
    private Pile pile;
    private int playerCardCount = 0;
    private int lastRoundWinner = -1;
    private int firstRoundWinner = -1;
    Random random = new Random();
    Pane result = new Pane();
    private int round = -1;

    private int clearedPile = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String oppIP = GameService.matchMake();
        playerScore = 0L;
        opponentScore = 0L;
        if (oppIP.equals("Waiting for matchmaking...")) {
            // I'm User 1
            // I should wait for Opponent
            user1Initializer();
            makeRoundForUser1();
            userWaitsForOpponent();
        }
        else {
            user2Initializer();
            connectToOpponent(oppIP);
        }
    }

    private void user1Initializer() {
        deck = new Deck();
        deck.shuffleDeck();
        deckImage.setVisible(true);

        playerCards = new HBox(10);
        player = new Hand(playerCards.getChildren());
        playerCards.setLayoutX(150);
        playerCards.setLayoutY(440);

        opponentCards = new HBox(10);
        opponent = new Hand(opponentCards.getChildren());
        opponentCards.setLayoutX(150);
        opponentCards.setLayoutY(10);

        pileCards = new StackPane();
        pile = new Pile(pileCards.getChildren());
        pileCards.setLayoutX(285);
        pileCards.setLayoutY(240);

        threeCards = new HBox(10);
        threeCard = new Hand(threeCards.getChildren());
        threeCards.setLayoutX(100);
        threeCards.setLayoutY(100);

        level4pane.getChildren().addAll(playerCards, opponentCards, pileCards);
        LevelController.startGame(deck, random, pile, threeCard);

        firstClosedCards = LevelController.showFirstClosedCards(level4pane, threeCards);

        controlNine = new KeyCodeCombination(KeyCode.DIGIT9, KeyCombination.CONTROL_DOWN);
        level4pane.setOnKeyPressed(keyEvent -> {
            if(controlNine.match(keyEvent)){
                playerScore = 151L;
                showLevel4Result(1);
            }
        });
    }

    private void user2Initializer() {
        playerCards = new HBox(10);
        player = new Hand(playerCards.getChildren());
        playerCards.setLayoutX(150);
        playerCards.setLayoutY(440);

        opponentCards = new HBox(10);
        opponent = new Hand(opponentCards.getChildren());
        opponentCards.setLayoutX(150);
        opponentCards.setLayoutY(10);

        pileCards = new StackPane();
        pile = new Pile(pileCards.getChildren());
        pileCards.setLayoutX(285);
        pileCards.setLayoutY(240);

        threeCards = new HBox(10);
        threeCard = new Hand(threeCards.getChildren());
        threeCards.setLayoutX(100);
        threeCards.setLayoutY(100);

        level4pane.getChildren().addAll(playerCards, opponentCards, pileCards);

        firstClosedCards = LevelController.showFirstClosedCards(level4pane, threeCards);

        controlNine = new KeyCodeCombination(KeyCode.DIGIT9, KeyCombination.CONTROL_DOWN);
        level4pane.setOnKeyPressed(keyEvent -> {
            if(controlNine.match(keyEvent)){
                playerScore = 151L;
                showLevel4Result(1);
            }
        });
    }
    public void waitingInfo() {
        result.setLayoutX(100);
        result.setLayoutY(75);
        Rectangle rec = new Rectangle(450,450);
        rec.setArcWidth(20);
        rec.setArcHeight(20);
        rec.setFill(Color.LIGHTGRAY);
        Label info1 = new Label();
        info1.setText("Waiting for another player...");
        info1.setLayoutX(55);
        info1.setLayoutY(160);
        info1.setFont(Font.font(30));
        info1.setTextFill(Color.DARKRED);
        result.getChildren().addAll(rec, info1);
        level4pane.getChildren().addAll(result);
    }

    public void matchFoundInfo(String username) {
        result.setLayoutX(100);
        result.setLayoutY(75);
        Rectangle rec = new Rectangle(450,450);
        rec.setArcWidth(20);
        rec.setArcHeight(20);
        rec.setFill(Color.LIGHTGRAY);
        Label info1 = new Label();
        info1.setText("A player is arrived!");
        info1.setLayoutX(55);
        info1.setLayoutY(100);
        info1.setFont(Font.font(30));
        info1.setTextFill(Color.DARKRED);
        Label info2 = new Label();
        info2.setText("You are matched with " +username+".");
        info2.setLayoutX(55);
        info2.setLayoutY(200);
        info2.setFont(Font.font(20));
        info2.setTextFill(Color.DARKRED);
        Label info3 = new Label();
        info3.setText("Game will start soon.");
        info3.setLayoutX(55);
        info3.setLayoutY(250);
        info3.setFont(Font.font(20));
        info3.setTextFill(Color.DARKRED);
        result.getChildren().addAll(rec, info1, info2, info3);
        level4pane.getChildren().addAll(result);
    }

    private void makeRoundForUser2() {
        for (Node c : playerCards.getChildren()) {
            c.setOnMouseClicked(event -> {
                clearedPile = 0;

                playerCards.getChildren().remove(c);
                pile.addCard((Card) c);


                pile.getCards().get(pile.getCards().size()-1).setRotate(random.nextInt(360));

                if(pile.getCards().size() == 2){
                    String rankPlayerCard1 = ((Card) pile.getCards().get(0)).getRank().toString();
                    String rankPlayerCard2 = ((Card) pile.getCards().get(1)).getRank().toString();

                    if (rankPlayerCard1.equals(rankPlayerCard2)){
                        int[] values = LevelController.controlPishtiCases(c, playerCardCount, pile);
                        lastRoundWinner = values[0];
                        playerCardCount = values[1];
                    }
                }

                reo = new Rectangle((player.getCards().size()) *80 + (player.getCards().size() == 1 ? 0 : player.getCards().size()-1)*10, 120);
                reo.setArcWidth(5);
                reo.setArcHeight(5);
                reo.setFill(Color.rgb(200, 200, 200, 0.5));

                reo.setLayoutX(150);
                reo.setLayoutY(440);
                level4pane.getChildren().addAll(reo);

                if (pile.getCards().size() >= 2){
                    String c1 = ((Card) pile.getCards().get(pile.getCards().size()-1)).getRank().toString();
                    String c2 = ((Card) pile.getCards().get(pile.getCards().size()-2)).getRank().toString();
                    if(((Card) (pileCards.getChildren().get(pileCards.getChildren().size()-1))).getRank() == Card.Rank.JACK || c1.equals(c2)){
                        playerScore += pile.getValueOfPile();
                        playerCardCount += pile.getCards().size();
                        pile.getCards().clear();
                        clearedPile = 1;
                        pile.setValueOfPile(0);
                        lastRoundWinner = 1;
                        if(firstRoundWinner == -1){
                            firstRoundWinner = 1;
                            level4pane.getChildren().addAll(firstClosedCards);
                        }
                    }
                }

                String user2Message = "PLAY_CARD" + ":" +   ((Card) c).getSuit() + ":" + ((Card) c).getRank() + ":" +
                        ((Card) c).getValue() + ":" + lastRoundWinner + ":" +
                        firstRoundWinner + ":" + clearedPile + ":" + playerScore + ":" + player.getCards().size() + ":";

                try {
                    sockOutputStream.writeUTF(user2Message);
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void makeRoundForUser1() {
        if (deck.getDeck().size() >= 4 && opponentCards.getChildren().size() == 0 && playerCards.getChildren().size() == 0) {
            LevelController.dealCard(player, deck, opponent);
            for (Node c : playerCards.getChildren()) {
                c.setOnMouseClicked(event -> {
                    clearedPile = 0;

                    playerCards.getChildren().remove(c);
                    pile.addCard((Card) c);

                    pile.getCards().get(pile.getCards().size()-1).setRotate(random.nextInt(360));

                    if(pile.getCards().size() == 2){
                        String rankPlayerCard1 = ((Card) pile.getCards().get(0)).getRank().toString();
                        String rankPlayerCard2 = ((Card) pile.getCards().get(1)).getRank().toString();

                        if (rankPlayerCard1.equals(rankPlayerCard2)){
                            int[] values = LevelController.controlPishtiCases(c, playerCardCount, pile);
                            lastRoundWinner = values[0];
                            playerCardCount = values[1];
                        }
                    }
                    reo = new Rectangle((player.getCards().size()) *80 + (player.getCards().size() == 1 ? 0 : player.getCards().size()-1)*10, 120);
                    reo.setArcWidth(5);
                    reo.setArcHeight(5);
                    reo.setFill(Color.rgb(200, 200, 200, 0.5));

                    reo.setLayoutX(150);
                    reo.setLayoutY(440);
                    level4pane.getChildren().addAll(reo);

                    if (pile.getCards().size() >= 2){
                        String c1 = ((Card) pile.getCards().get(pile.getCards().size()-1)).getRank().toString();
                        String c2 = ((Card) pile.getCards().get(pile.getCards().size()-2)).getRank().toString();
                        if(((Card) (pileCards.getChildren().get(pileCards.getChildren().size()-1))).getRank() == Card.Rank.JACK || c1.equals(c2)){
                            playerScore += pile.getValueOfPile();
                            playerCardCount += pile.getCards().size();
                            pile.getCards().clear();
                            clearedPile = 1;
                            pile.setValueOfPile(0);
                            lastRoundWinner = 1;
                            if(firstRoundWinner == -1){
                                firstRoundWinner = 1;
                                level4pane.getChildren().addAll(firstClosedCards);
                            }
                        }
                    }

                    String user1Message = "PLAY_CARD" + ":" +   ((Card) c).getSuit() + ":" + ((Card) c).getRank() + ":" +
                            ((Card) c).getValue() + ":" + lastRoundWinner + ":" +
                            firstRoundWinner + ":" + clearedPile + ":" + playerScore + ":" + deck.getDeck().size() + ":";

                    try {
                        sockOutputStream.writeUTF(user1Message);
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
            if(deck.getDeck().size()==0){
                deckImage.setVisible(false);
            }
        }
    }

    private void showLevel4Result(int winner) {
        Pane result = new Pane();
        Label info1 = new Label();
        Label info2 = new Label();
        Label info3 = new Label();
        roundLabel.setText("Round: "+round);
        opponentScoreLabel.setText("Opponent Score: "+opponentScore);
        yourScoreLabel.setText("Your Score: "+playerScore);
        result.setLayoutX(100);
        result.setLayoutY(75);
        Button resultButton = new Button();
        Rectangle rec = new Rectangle(450,450);
        rec.setArcWidth(20);
        rec.setArcHeight(20);
        if(winner == 1){
            LevelController.winInfo(rec, info1, info2, resultButton, "fxml/dashboard.fxml","dashboard");
            overallScore += 3*(playerScore-opponentScore);
            GameService.updateScore(overallScore);
            info3.setText("Your Score: " + (playerScore - opponentScore));
            info3.setFont(Font.font(30));
            info3.setTextFill(Color.WHITE);
            info3.setLayoutY(200);
            info3.setLayoutX(140);
        }
        else if (winner == 0){
            LevelController.loseInfo(rec, info1, info2, resultButton);
        }
        result.getChildren().addAll(rec, info1, info2, info3, resultButton);
        level4pane.getChildren().addAll(result);
    }

    public void onLevelEnd(){
        if(playerScore>=151){
            if(opponentScore<151){
                showLevel4Result(1);
            }
            else {
                if(playerScore>=opponentScore){
                    showLevel4Result(1);
                }
                else{
                    showLevel4Result(0);
                }
            }
        }
        else{
            if(opponentScore>=151){
                showLevel4Result(0);
            }
        }
    }

    private void userWaitsForOpponent() {
        Thread acceptSocketRequestThread = new Thread(() -> {
            try {
                waitingInfo();
                ServerSocket servSock = new ServerSocket(Constants.PORT);

                Socket opponentSock = servSock.accept();

                sockInputStream  = new DataInputStream( opponentSock.getInputStream());
                sockOutputStream = new DataOutputStream(opponentSock.getOutputStream());

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        level4pane.getChildren().remove(result);
                        communicationForUser1();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        acceptSocketRequestThread.start();
    }

    private void connectToOpponent(String opponentIP){
        Socket sock = new Socket();
        try {
            sock.connect(new InetSocketAddress(opponentIP, Constants.PORT));
            sockInputStream = new DataInputStream(sock.getInputStream());
            sockOutputStream = new DataOutputStream(sock.getOutputStream());
            // TRY Communication
            communicationForUser2();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void communicationForUser1() {
        Thread listeningThreadForUser1 = new Thread(() -> {
            String[] messageOfUser2;
            try {
                while(true) {
                    messageOfUser2 = sockInputStream.readUTF().split(":");
                    switch (messageOfUser2[0]) {
                        case "INIT_GAME":
                            round += 1;
                            String[] userName = messageOfUser2;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    matchFoundInfo(userName[1]);
                                }
                            });
                            TimeUnit.MILLISECONDS.sleep(2000);
                            Platform.runLater(new Runnable() {
                                @SneakyThrows
                                @Override
                                public void run() {
                                    level4pane.getChildren().remove(result);
                                    roundLabel.setText("Round: " + round);
                                    opponentScoreLabel.setText("Opponent Score: " + opponentScore);
                                    yourScoreLabel.setText("Your Score: " + playerScore);
//                                level4pane.getChildren().addAll(roundLabel, opponentScoreLabel, yourScoreLabel);
                                }
                            });
                            break;
                        case "PLAY_CARD":
                            Card opponentCard = new Card(Card.Suit.valueOf(messageOfUser2[1]),
                                    Card.Rank.valueOf(messageOfUser2[2]),
                                    Integer.parseInt(messageOfUser2[3]));
                            opponentCard.makeOpen();
                            opponentCard.setRotate(random.nextInt(360));
                            String[] finalMessageOfUser2 = messageOfUser2;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    opponent.getCards().remove(0);
                                    pile.addCard(opponentCard);
                                    if (Integer.parseInt(finalMessageOfUser2[6]) == 1) {
                                        pile.getCards().clear();
                                    }
                                    if (Integer.parseInt(finalMessageOfUser2[4]) != -1) {
                                        lastRoundWinner = Integer.parseInt(finalMessageOfUser2[4]);
                                    }
                                    if (Integer.parseInt(finalMessageOfUser2[5]) != -1) {
                                        firstRoundWinner = Integer.parseInt(finalMessageOfUser2[5]);
                                    }
                                    if (opponent.getCards().size() == 0 && player.getCards().size() == 0 && deck.getDeck().size() != 0) {
                                        makeRoundForUser1();
                                        StringBuilder user1Message = new StringBuilder("DEAL_CARD" + ":");
                                        for (int i = 0; i < playerCards.getChildren().size(); ++i) {
                                            user1Message.append(((Card) playerCards.getChildren().get(i)).getSuit()).append(":").append(((Card) playerCards.getChildren().get(i)).getRank()).append(":").append(((Card) playerCards.getChildren().get(i)).getValue()).append(":");
                                        }
                                        for (int i = 0; i < opponentCards.getChildren().size(); ++i) {
                                            user1Message.append(((Card) opponentCards.getChildren().get(i)).getSuit()).append(":").append(((Card) opponentCards.getChildren().get(i)).getRank()).append(":").append(((Card) opponentCards.getChildren().get(i)).getValue()).append(":");
                                        }
                                        user1Message.append(deck.getDeck().size()).append(":");
                                        try {
                                            sockOutputStream.writeUTF(user1Message.toString());
                                            TimeUnit.MILLISECONDS.sleep(50);
                                        } catch (IOException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (opponent.getCards().size() == 0 && player.getCards().size() == 0 && deck.getDeck().size() == 0) {
                                        Long[] playerAndOpponent = LevelController.lastTurnCalculation(lastRoundWinner, pile, playerCardCount, pileCards);
                                        playerScore = playerAndOpponent[0];
                                        opponentScore = playerAndOpponent[1];
                                        round += 1;
                                        roundLabel.setText("Round: " + round);
                                        opponentScoreLabel.setText("Opponent Score: " + opponentScore);
                                        yourScoreLabel.setText("Your Score: " + playerScore);
                                        String user1Message = "ROUND_END" + ":" + round + ":" + opponentScore + ":" + playerScore + ":";
                                        try {
                                            sockOutputStream.writeUTF(user1Message);
                                            TimeUnit.MILLISECONDS.sleep(50);
                                        } catch (IOException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        onLevelEnd();
                                        return;
                                    }
                                    level4pane.getChildren().remove(reo);
                                }
                            });
                            break;
                        case "DEAL_ME":
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    makeRoundForUser1();
                                    StringBuilder user1Message = new StringBuilder("DEAL_CARD_ID" + ":");
                                    for (int i = 0; i < playerCards.getChildren().size(); ++i) {
                                        user1Message.append(((Card) playerCards.getChildren().get(i)).getSuit()).append(":").append(((Card) playerCards.getChildren().get(i)).getRank()).append(":").append(((Card) playerCards.getChildren().get(i)).getValue()).append(":");
                                    }
                                    for (int i = 0; i < opponentCards.getChildren().size(); ++i) {
                                        user1Message.append(((Card) opponentCards.getChildren().get(i)).getSuit()).append(":").append(((Card) opponentCards.getChildren().get(i)).getRank()).append(":").append(((Card) opponentCards.getChildren().get(i)).getValue()).append(":");
                                    }
                                    user1Message.append(deck.getDeck().size()).append(":");
                                    reo = new Rectangle((player.getCards().size()) * 80 + (player.getCards().size() == 1 ? 0 : player.getCards().size() - 1) * 10, 120);
                                    reo.setArcWidth(5);
                                    reo.setArcHeight(5);
                                    reo.setFill(Color.rgb(200, 200, 200, 0.5));

                                    reo.setLayoutX(150);
                                    reo.setLayoutY(440);
                                    level4pane.getChildren().addAll(reo);

                                    try {
                                        sockOutputStream.writeUTF(user1Message.toString());
                                        TimeUnit.MILLISECONDS.sleep(50);
                                    } catch (IOException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;
                        case "ROUND_END": {
                            String[] finalMessageOfUser = messageOfUser2;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    round = Integer.parseInt(finalMessageOfUser[1]);
                                    opponentScore = (long) Integer.parseInt(finalMessageOfUser[3]);
                                    playerScore = (long) Integer.parseInt(finalMessageOfUser[2]);
                                    roundLabel.setText("Round: " + round);
                                    opponentScoreLabel.setText("Opponent Score: " + opponentScore);
                                    yourScoreLabel.setText("Your Score: " + playerScore);
                                    pile.getCards().clear();
                                }
                            });
                            StringBuilder messageOfUser1 = new StringBuilder();
                            messageOfUser1.append("NEW_ROUND" + ":");
                            for (int i = 0; i < pileCards.getChildren().size(); ++i) {
                                messageOfUser1.append(((Card) pileCards.getChildren().get(i)).getSuit()).append(":").append(((Card) pileCards.getChildren().get(i)).getRank()).append(":").append(((Card) pileCards.getChildren().get(i)).getValue()).append(":");
                            }
                            for (int i = 0; i < playerCards.getChildren().size(); ++i) {
                                messageOfUser1.append(((Card) playerCards.getChildren().get(i)).getSuit()).append(":").append(((Card) playerCards.getChildren().get(i)).getRank()).append(":").append(((Card) playerCards.getChildren().get(i)).getValue()).append(":");
                            }
                            for (int i = 0; i < opponentCards.getChildren().size(); ++i) {
                                messageOfUser1.append(((Card) opponentCards.getChildren().get(i)).getSuit()).append(":").append(((Card) opponentCards.getChildren().get(i)).getRank()).append(":").append(((Card) opponentCards.getChildren().get(i)).getValue()).append(":");
                            }
                            messageOfUser1.append(round).append(":").append(playerScore).append(":").append(opponentScore).append(":");
                            try {
                                sockOutputStream.writeUTF(messageOfUser1.toString());
                                TimeUnit.MILLISECONDS.sleep(50);
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case "HAS_WINNER":
//                        SHOW MAGLUBIYET
                            break;
                        case "NEW_ROUND": {
                            StringBuilder messageOfUser1 = new StringBuilder();
                            messageOfUser1.append("NEW_ROUND" + ":");
                            for (int i = 0; i < pileCards.getChildren().size(); ++i) {
                                messageOfUser1.append(((Card) pileCards.getChildren().get(i)).getSuit()).append(":").append(((Card) pileCards.getChildren().get(i)).getRank()).append(":").append(((Card) pileCards.getChildren().get(i)).getValue()).append(":");
                            }
                            for (int i = 0; i < playerCards.getChildren().size(); ++i) {
                                messageOfUser1.append(((Card) playerCards.getChildren().get(i)).getSuit()).append(":").append(((Card) playerCards.getChildren().get(i)).getRank()).append(":").append(((Card) playerCards.getChildren().get(i)).getValue()).append(":");
                            }
                            for (int i = 0; i < opponentCards.getChildren().size(); ++i) {
                                messageOfUser1.append(((Card) opponentCards.getChildren().get(i)).getSuit()).append(":").append(((Card) opponentCards.getChildren().get(i)).getRank()).append(":").append(((Card) opponentCards.getChildren().get(i)).getValue()).append(":");
                            }
                            messageOfUser1.append(round).append(":").append(playerScore).append(":").append(opponentScore).append(":");
                            try {
                                sockOutputStream.writeUTF(messageOfUser1.toString());
                                TimeUnit.MILLISECONDS.sleep(50);
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        listeningThreadForUser1.start();

        Thread initialInfoThreadForUser1 = new Thread(() -> {
            StringBuilder messageOfUser1 = new StringBuilder();
            messageOfUser1.append("INIT_GAME" + ":").append(Constants.player.getUsername()).append(":");
            for (int i = 0; i < pileCards.getChildren().size(); ++i) {
                messageOfUser1.append(((Card) pileCards.getChildren().get(i)).getSuit()).append(":").append(((Card) pileCards.getChildren().get(i)).getRank()).append(":").append(((Card) pileCards.getChildren().get(i)).getValue()).append(":");
            }
            for (int i = 0; i < playerCards.getChildren().size(); ++i) {
                messageOfUser1.append(((Card) playerCards.getChildren().get(i)).getSuit()).append(":").append(((Card) playerCards.getChildren().get(i)).getRank()).append(":").append(((Card) playerCards.getChildren().get(i)).getValue()).append(":");
            }
            for(int i = 0; i < opponentCards.getChildren().size(); ++i) {
                messageOfUser1.append(((Card) opponentCards.getChildren().get(i)).getSuit()).append(":").append(((Card) opponentCards.getChildren().get(i)).getRank()).append(":").append(((Card) opponentCards.getChildren().get(i)).getValue()).append(":");
            }
            messageOfUser1.append(round).append(":").append(playerScore).append(":").append(opponentScore).append(":");
            try {
                sockOutputStream.writeUTF(messageOfUser1.toString());
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        initialInfoThreadForUser1.start();
    }

    private void communicationForUser2() {
        Thread listeningThreadForUser2 = new Thread(() -> {
            String[] messageOfUser1;
            while (true) {
                try {
                    messageOfUser1 = sockInputStream.readUTF().split(":");
                    switch (messageOfUser1[0]) {
                        case "INIT_GAME" -> {
                            String[] userName = messageOfUser1;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    matchFoundInfo(userName[1]);
                                }
                            });
                            TimeUnit.MILLISECONDS.sleep(3000);
                            Platform.runLater(new Runnable() {
                                @SneakyThrows
                                @Override
                                public void run() {
                                    level4pane.getChildren().remove(result);
                                    roundLabel.setText("Round: " + userName[38]);
                                    opponentScoreLabel.setText("Opponent Score: " + userName[40]);
                                    yourScoreLabel.setText("Your Score: " + userName[39]);
                                    level4pane.getChildren().remove(result);
                                }
                            });
                            String[] initialMessageOfUser1 = messageOfUser1;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < 3; ++i) {
                                        Card pileCard = new Card(Card.Suit.valueOf(initialMessageOfUser1[3 * i + 2]),
                                                Card.Rank.valueOf(initialMessageOfUser1[3 * i + 3]),
                                                Integer.parseInt(initialMessageOfUser1[3 * i + 4]));
                                        pileCard.makeClosed();
                                        pileCard.setRotate(random.nextInt(360));
                                        pile.addCard(pileCard);
                                        Card cardTemp = new Card(pileCard.getSuit(), pileCard.getRank(), pileCard.getValue());
                                        threeCard.takeCard(cardTemp);
                                    }
                                    Card pileCard = new Card(Card.Suit.valueOf(initialMessageOfUser1[11]),
                                            Card.Rank.valueOf(initialMessageOfUser1[12]),
                                            Integer.parseInt(initialMessageOfUser1[13]));
                                    pileCard.makeOpen();
                                    pileCard.setRotate(random.nextInt(360));
                                    pile.addCard(pileCard);

                                    for (int i = 4; i < 8; ++i) {
                                        Card opponentCard = new Card(Card.Suit.valueOf(initialMessageOfUser1[3 * i + 2]),
                                                Card.Rank.valueOf(initialMessageOfUser1[3 * i + 3]),
                                                Integer.parseInt(initialMessageOfUser1[3 * i + 4]));
                                        opponentCard.makeClosed();
                                        opponent.takeCard(opponentCard);
                                    }

                                    for (int i = 8; i < 12; ++i) {
                                        Card playerCard = new Card(Card.Suit.valueOf(initialMessageOfUser1[3 * i + 2]),
                                                Card.Rank.valueOf(initialMessageOfUser1[3 * i + 3]),
                                                Integer.parseInt(initialMessageOfUser1[3 * i + 4]));
                                        playerCard.makeOpen();
                                        player.takeCard(playerCard);
                                    }
                                    makeRoundForUser2();
                                }
                            });
                        }
                        case "PLAY_CARD" -> {
                            Card opponentCard = new Card(Card.Suit.valueOf(messageOfUser1[1]),
                                    Card.Rank.valueOf(messageOfUser1[2]),
                                    Integer.parseInt(messageOfUser1[3]));
                            opponentCard.makeOpen();
                            opponentCard.setRotate(random.nextInt(360));
                            String[] finalMessageOfUser1 = messageOfUser1;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    opponent.getCards().remove(0);
                                    pile.addCard(opponentCard);
                                    if (Integer.parseInt(finalMessageOfUser1[6]) == 1) {
                                        pile.getCards().clear();
                                    }
                                    if (Integer.parseInt(finalMessageOfUser1[4]) != -1) {
                                        lastRoundWinner = Integer.parseInt(finalMessageOfUser1[4]);
                                    }
                                    if (Integer.parseInt(finalMessageOfUser1[5]) != -1) {
                                        firstRoundWinner = Integer.parseInt(finalMessageOfUser1[5]);
                                    }
                                    if (player.getCards().size() == 0 && opponent.getCards().size() == 0 && Integer.parseInt(finalMessageOfUser1[8]) != 0) {
                                        String messageOfUser2 = "DEAL_ME" + ":";
                                        try {
                                            sockOutputStream.writeUTF(messageOfUser2);
                                            TimeUnit.MILLISECONDS.sleep(50);
                                        } catch (IOException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (opponent.getCards().size() == 0 && player.getCards().size() == 0 && Integer.parseInt(finalMessageOfUser1[8]) == 0) {
                                        Long[] playerAndOpponent = LevelController.lastTurnCalculation(lastRoundWinner, pile, playerCardCount, pileCards);
                                        playerScore = playerAndOpponent[0];
                                        opponentScore = playerAndOpponent[1];
                                        round += 1;
                                        roundLabel.setText("Round: " + round);
                                        opponentScoreLabel.setText("Opponent Score: " + opponentScore);
                                        yourScoreLabel.setText("Your Score: " + playerScore);
                                        String user2Message = "ROUND_END" + ":" + round + ":" + opponentScore + ":" + playerScore + ":";
                                        try {
                                            sockOutputStream.writeUTF(user2Message);
                                            TimeUnit.MILLISECONDS.sleep(50);
                                        } catch (IOException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        onLevelEnd();
                                        return;
                                    }
                                    level4pane.getChildren().remove(reo);
                                }
                            });
                        }
                        case "DEAL_CARD" -> {
                            String[] initialMessageOfUser1 = messageOfUser1;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < 4; ++i) {
                                        Card opponentCard = new Card(Card.Suit.valueOf(initialMessageOfUser1[3 * i + 1]),
                                                Card.Rank.valueOf(initialMessageOfUser1[3 * i + 2]),
                                                Integer.parseInt(initialMessageOfUser1[3 * i + 3]));
                                        opponentCard.makeClosed();
                                        opponent.takeCard(opponentCard);
                                    }

                                    for (int i = 4; i < 8; ++i) {
                                        Card playerCard = new Card(Card.Suit.valueOf(initialMessageOfUser1[3 * i + 1]),
                                                Card.Rank.valueOf(initialMessageOfUser1[3 * i + 2]),
                                                Integer.parseInt(initialMessageOfUser1[3 * i + 3]));
                                        playerCard.makeOpen();
                                        player.takeCard(playerCard);
                                    }
                                    if (Integer.parseInt(initialMessageOfUser1[25]) == 0) {
                                        deckImage.setVisible(false);
                                    }
                                    reo = new Rectangle((player.getCards().size()) * 80 + (player.getCards().size() == 1 ? 0 : player.getCards().size() - 1) * 10, 120);
                                    reo.setArcWidth(5);
                                    reo.setArcHeight(5);
                                    reo.setFill(Color.rgb(200, 200, 200, 0.5));

                                    reo.setLayoutX(150);
                                    reo.setLayoutY(440);
                                    level4pane.getChildren().addAll(reo);
                                    makeRoundForUser2();
                                }
                            });
                        }
                        case "DEAL_CARD_ID" -> {
                            String[] initialMessageOfUser1 = messageOfUser1;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < 4; ++i) {
                                        Card opponentCard = new Card(Card.Suit.valueOf(initialMessageOfUser1[3 * i + 1]),
                                                Card.Rank.valueOf(initialMessageOfUser1[3 * i + 2]),
                                                Integer.parseInt(initialMessageOfUser1[3 * i + 3]));
                                        opponentCard.makeClosed();
                                        opponent.takeCard(opponentCard);
                                    }

                                    for (int i = 4; i < 8; ++i) {
                                        Card playerCard = new Card(Card.Suit.valueOf(initialMessageOfUser1[3 * i + 1]),
                                                Card.Rank.valueOf(initialMessageOfUser1[3 * i + 2]),
                                                Integer.parseInt(initialMessageOfUser1[3 * i + 3]));
                                        playerCard.makeOpen();
                                        player.takeCard(playerCard);
                                    }
                                    if (Integer.parseInt(initialMessageOfUser1[25]) == 0) {
                                        deckImage.setVisible(false);
                                    }
                                    reo = new Rectangle((player.getCards().size()) * 80 + (player.getCards().size() == 1 ? 0 : player.getCards().size() - 1) * 10, 120);
                                    makeRoundForUser2();
                                }
                            });
                        }
                        case "ROUND_END" -> {
                            String[] finalMessageOfUser = messageOfUser1;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    round = Integer.parseInt(finalMessageOfUser[1]);
                                    opponentScore = (long) Integer.parseInt(finalMessageOfUser[3]);
                                    playerScore = (long) Integer.parseInt(finalMessageOfUser[2]);
                                    roundLabel.setText("Round: " + round);
                                    opponentScoreLabel.setText("Opponent Score: " + opponentScore);
                                    yourScoreLabel.setText("Your Score: " + playerScore);
                                    pile.getCards().clear();
                                }
                            });
                            break;
                        }
                        case "NEW_ROUND" -> {
                            String[] initialMessageOfUser1 = messageOfUser1;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < 3; ++i) {
                                        Card pileCard = new Card(Card.Suit.valueOf(initialMessageOfUser1[3 * i + 1]),
                                                Card.Rank.valueOf(initialMessageOfUser1[3 * i + 2]),
                                                Integer.parseInt(initialMessageOfUser1[3 * i + 3]));
                                        pileCard.makeClosed();
                                        pileCard.setRotate(random.nextInt(360));
                                        pile.addCard(pileCard);
                                        Card cardTemp = new Card(pileCard.getSuit(), pileCard.getRank(), pileCard.getValue());
                                        threeCard.takeCard(cardTemp);
                                    }
                                    Card pileCard = new Card(Card.Suit.valueOf(initialMessageOfUser1[10]),
                                            Card.Rank.valueOf(initialMessageOfUser1[11]),
                                            Integer.parseInt(initialMessageOfUser1[12]));
                                    pileCard.makeOpen();
                                    pileCard.setRotate(random.nextInt(360));
                                    pile.addCard(pileCard);

                                    for (int i = 4; i < 8; ++i) {
                                        Card opponentCard = new Card(Card.Suit.valueOf(initialMessageOfUser1[3 * i + 1]),
                                                Card.Rank.valueOf(initialMessageOfUser1[3 * i + 2]),
                                                Integer.parseInt(initialMessageOfUser1[3 * i + 3]));
                                        opponentCard.makeClosed();
                                        opponent.takeCard(opponentCard);
                                    }

                                    for (int i = 8; i < 12; ++i) {
                                        Card playerCard = new Card(Card.Suit.valueOf(initialMessageOfUser1[3 * i + 1]),
                                                Card.Rank.valueOf(initialMessageOfUser1[3 * i + 2]),
                                                Integer.parseInt(initialMessageOfUser1[3 * i + 3]));
                                        playerCard.makeOpen();
                                        player.takeCard(playerCard);
                                    }
                                    makeRoundForUser2();
                                }
                            });
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        listeningThreadForUser2.start();

        Thread initialInfoThreadForUser2 = new Thread(() -> {
            String messageOfUser2 = "INIT_GAME" + ":" + Constants.player.getUsername();
            try {
                sockOutputStream.writeUTF(messageOfUser2);
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        initialInfoThreadForUser2.start();
    }
}
