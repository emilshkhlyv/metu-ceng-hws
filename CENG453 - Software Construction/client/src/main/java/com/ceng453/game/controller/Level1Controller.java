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
 * Level 1 controller class
 */
public class Level1Controller implements Initializable {

    /**
     * Level 1 Pane from fxml file
     */
    @FXML
    AnchorPane level1pane;

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
     * Pane instance for showing closed cards in case of player winning in first turn
     */
    private Pane firstClosedCards;

    /**
     * KeyCombination instance for control+9 keys combination for hint
     */
    private KeyCombination controlNine;

    /**
     * Total number of cards from the turns won by the player
     */
    private int playerCardCount = 0;

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
     * Round count in Level 1
     */
    private int round = 0;

    /**
     * Random variable for computer's moves
     */
    Random random = new Random();

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
     * Overridden initialize method for initial setup
     * @param location location
     * @param resources resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerScore = 0L;
        opponentScore = 0L;
        GameService.startGame();
        initialSetup();
    }

    /**
     * This method initializes game before each round
     */
    public void initialSetup(){
        firstRoundWinner = -1;

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

        HBox threeCards = new HBox(10);
        Hand threeCard = new Hand(threeCards.getChildren());
        threeCards.setLayoutX(100);
        threeCards.setLayoutY(100);

        round += 1;
        roundLabel.setText("Round: " + round);
        opponentScoreLabel.setText("Opponent Score: " + opponentScore);
        yourScoreLabel.setText("Your Score: " + playerScore);

        level1pane.getChildren().addAll(playerCards, opponentCards, pileCards);

        LevelController.startGame(deck, random, pile, threeCard);
        firstClosedCards = LevelController.showFirstClosedCards(level1pane, threeCards);
        makeRound();

        controlNine = new KeyCodeCombination(KeyCode.DIGIT9, KeyCombination.CONTROL_DOWN);
        level1pane.setOnKeyPressed(keyEvent -> {
            if(controlNine.match(keyEvent)){
                playerScore = 151L;
                showLevel1Result(1);
            }
        });
    }

    /**
     * This method handles dealing cards and assign event handler to each player's card
     */
    public void makeRound(){
        if (deck.getDeck().size() >= 4 && playerCards.getChildren().size() == 0 && opponentCards.getChildren().size() == 0){
            LevelController.dealCard(player, deck, opponent);
            for (Node c : playerCards.getChildren()) {
                c.setOnMouseClicked(event -> {
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

                    if (pile.getCards().size() >= 2){
                        String c1 = ((Card) pile.getCards().get(pile.getCards().size()-1)).getRank().toString();
                        String c2 = ((Card) pile.getCards().get(pile.getCards().size()-2)).getRank().toString();
                        if(((Card) (pileCards.getChildren().get(pileCards.getChildren().size()-1))).getRank() == Card.Rank.JACK || c1.equals(c2)){
                            playerScore += pile.getValueOfPile();
                            playerCardCount += pile.getCards().size();
                            pile.getCards().clear();
                            pile.setValueOfPile(0);
                            lastRoundWinner = 1;
                            if(firstRoundWinner == -1){
                                firstRoundWinner = 1;
                                level1pane.getChildren().addAll(firstClosedCards);
                            }
                        }
                    }

                    int opponentCardIndex = random.nextInt(opponentCards.getChildren().size());
                    ((Card)opponentCards.getChildren().get(opponentCardIndex)).makeOpen();
                    pile.addCard((Card) opponentCards.getChildren().get(opponentCardIndex));

                    if(pile.getCards().size() == 2){
                        String rankOppCard1 = ((Card) pile.getCards().get(0)).getRank().toString();
                        String rankOppCard2 = ((Card) pile.getCards().get(1)).getRank().toString();
                        if (rankOppCard1.equals(rankOppCard2)){
                            if(((Card) c).getRank() == Card.Rank.JACK){
                                opponentScore += 20;
                            }
                            else{
                                opponentScore += 10;
                            }
                            lastRoundWinner = 0;
                            pile.getCards().clear();
                        }
                    }

                    if (pile.getCards().size() >= 2){
                        String c1 = ((Card) pile.getCards().get(pile.getCards().size()-1)).getRank().toString();
                        String c2 = ((Card) pile.getCards().get(pile.getCards().size()-2)).getRank().toString();
                        if(((Card) (pileCards.getChildren().get(pileCards.getChildren().size()-1))).getRank() == Card.Rank.JACK || c1.equals(c2)){
                            opponentScore += pile.getValueOfPile();
                            playerCardCount += pile.getCards().size();
                            pile.getCards().clear();
                            pile.setValueOfPile(0);
                            lastRoundWinner = 0;
                            if(firstRoundWinner == -1){
                                firstRoundWinner = 0;
                            }
                        }
                    }

                    if(deck.getDeck().size()==0){
                        if(playerCards.getChildren().size() == 0 && opponentCards.getChildren().size() == 0){
                            Long[] playerAndOpponent = LevelController.lastTurnCalculation(lastRoundWinner, pile, playerCardCount, pileCards);
                            playerScore = playerAndOpponent[0];
                            opponentScore = playerAndOpponent[1];
                            onLevelEnd();
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
     * This method decides match will continue or end
     */
    public void onLevelEnd(){
        if(playerScore >= 151){
            if(opponentScore < 151){
                showLevel1Result(1);
            }
            else {
                if(playerScore>=opponentScore){
                    showLevel1Result(1);
                }
                else{
                    showLevel1Result(0);
                }
            }
        }
        else{
            if(opponentScore >= 151){
                showLevel1Result(0);
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
     * This method shows the result of Level 1 to Player
     * if Player wins, there will be green background and Continue button for going Level 2
     * if Player loses, there will be red background and Back to Dashboard button for going Dashboard
     * @param winner This argument is for specifying winner as 0 stands for opponent, 1 stands for Player
     */
    public void showLevel1Result(int winner){
        Pane result;
        roundLabel.setText("Round: "+round);
        opponentScoreLabel.setText("Opponent Score: "+opponentScore);
        yourScoreLabel.setText("Your Score: "+playerScore);
        result = new Pane();
        result.setLayoutX(100);
        result.setLayoutY(75);
        Rectangle rec = new Rectangle(450,450);
        rec.setArcWidth(20);
        rec.setArcHeight(20);
        Label info1 = new Label();
        Label info2 = new Label();
        Label info3 = new Label();
        Button resultButton = new Button();
        if(winner == 1){
            LevelController.winInfo(rec, info1, info2, resultButton, "fxml/level2.fxml","Level 2");
            overallScore += (playerScore-opponentScore);
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
        level1pane.getChildren().addAll(result);
    }
}
