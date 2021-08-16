package com.ceng453.game.controller;

import com.ceng453.game.gameObjects.Card;
import com.ceng453.game.gameObjects.Deck;
import com.ceng453.game.gameObjects.Hand;
import com.ceng453.game.gameObjects.Pile;
import com.ceng453.game.service.GameService;
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

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import static com.ceng453.game.constants.Constants.*;

/**
 * Level 2 controller class
 */
public class Level2Controller implements Initializable {

    /**
     * Level 2 Pane from fxml file
     */
    @FXML
    AnchorPane level2pane;

    /**
     * deck Image from fxml file
     */
    @FXML
    ImageView deckImage;

    /**
     * Round label for round count
     */
    @FXML
    Label roundLabel;

    /**
     * Opponent Score Label for showing opponent's score
     */
    @FXML
    Label opponentScoreLabel;

    /**
     * Score Label for showing player's score
     */
    @FXML
    Label yourScoreLabel;

    /**
     * Deck instance
     */
    private Deck deck;

    /**
     * Hand instance for player
     */
    private Hand player;

    /**
     * Hand instance for opponent
     */
    private Hand opponent;

    /**
     * Pile instance
     */
    private Pile pile;

    /**
     * HBox instance for showing player's cards
     */
    private HBox playerCards;

    /**
     * HBox instance for showing opponent's cards
     */
    private HBox opponentCards;

    /**
     * StackPane instance for showing cards in pile
     */
    private StackPane pileCards;

    /**
     * Last round winner flag for remained pile
     * 0 for opponent, 1 for player
     */
    private int lastRoundWinner = -1;

    /**
     * First round winner flag for showing 3 closed cards
     * 0 for opponent, 1 for player
     */
    private int firstRoundWinner = -1;

    /**
     * Total number of cards from the turns won by the player
     */
    private int playerCardCount = 0;

    /**
     * Round count in Level 2
     */
    private int round = 0;

    /**
     * Random variable for computer's moves
     */
    Random random = new Random();

    /**
     * Pane instance for showing closed cards in case of player winning in first turn
     */
    private Pane firstClosedCards;

    /**
     * Overridden initialize method for initial setup
     * @param location location
     * @param resources resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerScore = 0L;
        opponentScore = 0L;
        initialSetup();
    }

    /**
     * This method initializes game before each round
     */
    public void initialSetup(){
        deck = new Deck();

        playerCards = new HBox(10);
        player = new Hand(playerCards.getChildren());

        opponentCards = new HBox(10);
        opponent = new Hand(opponentCards.getChildren());

        pileCards = new StackPane();
        pile = new Pile(pileCards.getChildren());

        HBox threeCards = new HBox(10);
        Hand threeCard = new Hand(threeCards.getChildren());
        threeCards.setLayoutX(100);
        threeCards.setLayoutY(100);

        deck.shuffleDeck();
        firstRoundWinner = -1;
        deckImage.setVisible(true);
        LevelController.startGame(deck, random, pile, threeCard);
        round = LevelController.tableInitializer(opponentCards, playerCards, pileCards, roundLabel, opponentScoreLabel, yourScoreLabel, round);
        level2pane.getChildren().addAll(playerCards, opponentCards, pileCards);
        firstClosedCards = LevelController.showFirstClosedCards(level2pane, threeCards);
        makeRound();

        KeyCombination controlNine = new KeyCodeCombination(KeyCode.DIGIT9, KeyCombination.CONTROL_DOWN);
        level2pane.setOnKeyPressed(keyEvent -> {
            if(controlNine.match(keyEvent)){
                playerScore = 151L;
                showLevel2Result(1);
            }
        });
    }

    /**
     * This method handles dealing cards and assign event handler to each player's card and
     * sets deckImage to invisible id there's no card in deck
     */
    public void makeRound(){
        if (deck.getDeck().size() >= 4 && playerCards.getChildren().size() == 0 && opponentCards.getChildren().size() == 0){
            LevelController.dealCard(player, deck, opponent);
            for (Node c : playerCards.getChildren()) {
                c.setOnMouseClicked(event -> {
                    int[] lastRoundAndPlayerCardCount = LevelController.playerTwoCards(playerCards, c, pile, lastRoundWinner, playerCardCount, random);
                    lastRoundWinner = lastRoundAndPlayerCardCount[0];
                    playerCardCount = lastRoundAndPlayerCardCount[1];


                    if (pile.getCards().size() >= 2){
                        String c1 = ((Card) pile.getCards().get(pile.getCards().size()-1)).getRank().toString();
                        String c2 = ((Card) pile.getCards().get(pile.getCards().size()-2)).getRank().toString();
                        if(((Card) (pileCards.getChildren().get(pileCards.getChildren().size()-1))).getRank() == Card.Rank.JACK || c1.equals(c2)){
                            playerScore += pile.getValueOfPile();
                            playerCardCount += pile.getCards().size();
                            pile.getCards().clear();
                            pile.setValueOfPile(0);
                            lastRoundWinner = 1;
                            //
                            if(firstRoundWinner == -1){
                                firstRoundWinner = 1;
//                                SHOW CLOSED CARDS
                                level2pane.getChildren().addAll(firstClosedCards);
                            }
                        }
                    }
                    int opponentCardIndex = LevelController.opponentCardSelection(random, opponentCards, pile);

                    ((Card)opponentCards.getChildren().get(opponentCardIndex)).makeOpen();
                    pile.addCard((Card) opponentCards.getChildren().get(opponentCardIndex));


                    if(pile.getCards().size() == 2){
                        lastRoundWinner = LevelController.opponentWinning(pile, c, lastRoundWinner);
                    }

                    if (pile.getCards().size() >= 2){
                        int[] lastAndFirstRound = LevelController.opponentWinning(pile, pileCards, lastRoundWinner, firstRoundWinner);
                        lastRoundWinner = lastAndFirstRound[0];
                        firstRoundWinner = lastAndFirstRound[1];
                    }
                    if(deck.getDeck().size()==0){
                        if(playerCards.getChildren().size() == 0 && opponentCards.getChildren().size() == 0){
                            levelEnd();
                        }
                    }

                });
            }
            if(deck.getDeck().size()==0){
                deckImage.setVisible(false);
            }
        }
    }


    /**
     * This method calls lastRoundCalculation method and send player Score and opponent Score to onLevelEnd method
     */
    public void levelEnd(){
        Long[] playerAndOpponent = LevelController.lastTurnCalculation(lastRoundWinner, pile, playerCardCount, pileCards);
        playerScore = playerAndOpponent[0];
        opponentScore = playerAndOpponent[1];
        onLevelEnd();
    }

    /**
     * This method decides match will continue or end
     */
    public void onLevelEnd(){
        if(playerScore >= 151){
            if(opponentScore < 151){
                showLevel2Result(1);
            }
            else {
                if(playerScore >= opponentScore){
                    showLevel2Result(1);
                }
                else{
                    showLevel2Result(0);
                }
            }
        }
        else{
            if(opponentScore>=151){
                showLevel2Result(0);
            }
            else initialSetup();
        }
    }

    /**
     * This method calls makeRound method, when the Deal Card button on deck is pressed
     */
    @FXML
    public void onClickDealCardButton(){
        makeRound();
    }

    /**
     * This method shows the result of Level 2 to Player
     * if Player wins, there will be green background and Continue button for going Level 3
     * if Player loses, there will be red background and Back to Dashboard button for going Dashboard
     * @param winner This argument is for specifying winner as 0 stands for opponent, 1 stands for Player
     */
    public void showLevel2Result(int winner){
        Pane result = new Pane();
        result.setLayoutX(100);
        Label info1 = new Label();
        result.setLayoutY(75);
        Label info2 = new Label();
        roundLabel.setText("Round: "+round);
        opponentScoreLabel.setText("Opponent Score: "+opponentScore);
        yourScoreLabel.setText("Your Score: "+playerScore);
        Rectangle rec = new Rectangle(450,450);
        rec.setArcWidth(20);
        rec.setArcHeight(20);
        Label info3 = new Label();
        Button resultButton = new Button();
        if(winner == 1){
            LevelController.winInfo(rec, info1, info2, resultButton, "fxml/level3.fxml","Level 3");
            overallScore += 2 * (playerScore-opponentScore);
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
        level2pane.getChildren().addAll(result);
    }
}
