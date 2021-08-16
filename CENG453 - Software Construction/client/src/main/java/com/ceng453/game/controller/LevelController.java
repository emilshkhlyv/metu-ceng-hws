package com.ceng453.game.controller;

import com.ceng453.game.gameObjects.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import static com.ceng453.game.constants.Constants.*;

/**
 * Controller class for Level 1, 2 and 3.
 * This class includes commonly used methods for these levels
 */
public class LevelController {
    /**
     * This method deals card to both players
     * @param player Hand instance of player
     * @param deck Deck instance
     * @param opponent Hand instance of opponent
     */
    public static void dealCard(Hand player, Deck deck, Hand opponent){
        for(int i = 0; i < 4; i++){
            player.takeCard(deck.getTopCard());
        }
        for(int i = 0; i < 4; i++){
            Card opponentCard = deck.getTopCard();
            opponentCard.makeClosed();
            opponent.takeCard(opponentCard);
        }
    }

    /**
     * This method selects and puts 3 closed and 1 open card to pile
     * @param deck Deck instance
     * @param random Random instance
     * @param pile Pile instance
     * @param threeCard Hand instance of threeCard
     */
    public static void startGame(Deck deck, Random random, Pile pile, Hand threeCard){
        for(int i = 0; i < 3; i++){
            Card card = deck.getTopCard();
            card.makeClosed();
            card.setRotate(random.nextInt(360));
            pile.addCard(card);
            Card cardTemp = new Card(card.getSuit(), card.getRank(), card.getValue());
            threeCard.takeCard(cardTemp);
        }
        Card openCard = deck.getTopCard();
        openCard.setRotate(random.nextInt(360));
        pile.addCard(openCard);
    }

    /**
     * This method controls the Pişti cases, such as playing Jack or same rank card with the top card of pile
     * @param c Last played Card
     * @param playerCardCount Total number of cards from the turns won by the player
     * @param pile Pile instance
     * @return Last round winner and Player's total Card Count
     */
    public static int[] controlPishtiCases(Node c, int playerCardCount, Pile pile){
        int[] result = new int[2];

        if(((Card) c).getRank() == Card.Rank.JACK){
            playerScore += 20;
        }
        else{
            playerScore += 10;
        }
        int lastRoundWinner = 1;
        playerCardCount += 2;

        result[0] = lastRoundWinner;
        result[1] = playerCardCount;
        pile.getCards().clear();
        return result;
    }

    /**
     * This method shows lose information to the player
     * @param rec Rectangle, which includes Labels and Button
     * @param info1 Label, which includes message about losing
     * @param info2 Label, which includes message about losing
     * @param resultButton Button for going Dashboard
     */
    static void loseInfo(Rectangle rec, Label info1, Label info2, Button resultButton){
        rec.setFill(Color.TOMATO);
        rec.setFill(Color.web("7C0A02"));

        info1.setText("You lost. Try again. ");
        info1.setLayoutX(105);
        info1.setLayoutY(160);
        info1.setFont(Font.font(30));
        info1.setTextFill(Color.WHITE);

        info2.setText("Press the button to return the dashboard.");
        info2.setFont(Font.font(20));
        info2.setTextFill(Color.WHITE);
        info2.setLayoutY(390);
        info2.setLayoutX(65);

        resultButton.setText("Back to Dashboard");
        resultButton.setLayoutX(170);
        resultButton.setLayoutY(350);
        resultButton.setOnMouseClicked(e->{
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Parent dashboard = null;
            try {
                dashboard = FXMLLoader.load(Objects.requireNonNull(LevelController.class.getClassLoader().getResource("fxml/dashboard.fxml")));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            assert dashboard != null;
            Scene scene = new Scene(dashboard, 800, 600);
            stage.setScene(scene);
            stage.show();
        });
    }

    /**
     * This method shows winning information to the player
     * @param rec Rectangle, which includes Labels and Button
     * @param info1 Label, which includes message about winning
     * @param info2 Label, which includes message about winning
     * @param resultButton Button for going next Level
     * @param resource name of fxml file, which is next Level
     * @param routePage name of route Page
     */
    static void winInfo(Rectangle rec, Label info1, Label info2, Button resultButton, String resource, String routePage) {
        rec.setFill(Color.GREEN);

        info1.setText("Congratulations! You Won!");
        info1.setLayoutX(55);
        info1.setLayoutY(160);
        info1.setFont(Font.font(30));
        info1.setTextFill(Color.WHITE);

        info2.setText("Press the button to continue "+routePage+".");
        info2.setFont(Font.font(20));
        info2.setTextFill(Color.WHITE);
        info2.setLayoutY(390);
        info2.setLayoutX(80);

        resultButton.setText("Continue");
        resultButton.setLayoutX(190);
        resultButton.setLayoutY(350);
        resultButton.setOnMouseClicked(e->{
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Parent level2 = null;
            try {
                level2 = FXMLLoader.load(Objects.requireNonNull(LevelController.class.getClassLoader().getResource(resource)));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            assert level2 != null;
            Scene scene = new Scene(level2, 800, 600);
            stage.setScene(scene);
            stage.show();
        });
    }

    /**
     * This method shows first three closed cards to user, if user win first turn
     * @param level1pane Pane instance of Level 1
     * @param threeCards HBox instance for showing 3 cards
     * @return newly created Pane
     */
    static Pane showFirstClosedCards(AnchorPane level1pane, HBox threeCards){
        Pane firstClosedCards = new Pane();
        firstClosedCards.setLayoutX(100);
        firstClosedCards.setLayoutY(75);
        Rectangle rec = new Rectangle(450,450);
        rec.setArcWidth(20);
        rec.setArcHeight(20);
        rec.setFill(Color.GRAY);
        Button continueButton = new Button();
        continueButton.setText("Continue");
        continueButton.setLayoutX(200);
        continueButton.setLayoutY(350);
        continueButton.setOnMouseClicked(e-> level1pane.getChildren().remove(firstClosedCards));
        firstClosedCards.getChildren().addAll(rec, continueButton, threeCards);
        return firstClosedCards;
    }

    /**
     * This method calculates points of players after the last turn in round
     * @param lastRoundWinner Winner of the last turn
     * @param pile Pile instance
     * @param playerCardCount Total number of cards from the turns won by the player
     * @param pileCards StackPane instance of pile
     * @return player score and opponent score
     */
    static Long[] lastTurnCalculation(int lastRoundWinner, Pile pile, int playerCardCount, StackPane pileCards){
        Long[] playerAndOpponent = new Long[2];
        if(lastRoundWinner == 0){
            opponentScore += pile.getValueOfPile();
            pile.getCards().clear();
        }
        else if(lastRoundWinner == 1){
            playerCardCount += pileCards.getChildren().size();
            playerScore += pile.getValueOfPile();
            pile.getCards().clear();
        }
        if(playerCardCount>=26){
            playerScore += 3;
        }
        else{
            opponentScore += 3;
        }
        playerAndOpponent[0] = playerScore;
        playerAndOpponent[1] = opponentScore;
        return playerAndOpponent;
    }

    /**
     *
     * @param random Random instance for selecting card randomly
     * @param opponentCards HBox instance of opponent Cards
     * @param pile Pile instance
     * @return index of opponent card, which will be played
     */
    static int opponentCardSelection(Random random, HBox opponentCards, Pile pile) {
        int opponentCardIndex= random.nextInt(opponentCards.getChildren().size());
        for(int i = 0; i< opponentCards.getChildren().size() && pile.getCards().size()!=0; i++){
            String jackOrEqual = ((Card) opponentCards.getChildren().get(i)).getRank().toString();
            if (jackOrEqual.equals(((Card) pile.getCards().get(pile.getCards().size()-1)).getRank().toString())){
                opponentCardIndex=i;
                break;
            }
            else if(jackOrEqual.equals(Card.Rank.JACK.toString())){
                opponentCardIndex=i;
            }
        }
        return opponentCardIndex;
    }

    /**
     * This method initialized the table graphically
     * @param opponentCards HBox instance of opponent Cards
     * @param playerCards HBox instance of player Cards
     * @param pileCards StackPane instance of pile Cards
     * @param roundLabel Label instance for showing round count
     * @param opponentScoreLabel Label for showing opponent's score
     * @param yourScoreLabel Label for showing player's score
     * @param round Round count
     * @return round count
     */
    static int tableInitializer(HBox opponentCards,
                                 HBox playerCards,
                                 StackPane pileCards,
                                 Label roundLabel,
                                 Label opponentScoreLabel,
                                 Label yourScoreLabel,
                                 int round
                                 ){
        round += 1;
        roundLabel.setText("Round: " + round);
        opponentScoreLabel.setText("Opponent Score: " + opponentScore);
        yourScoreLabel.setText("Your Score: " + playerScore);

        opponentCards.setLayoutX(150);
        opponentCards.setLayoutY(10);

        playerCards.setLayoutX(150);
        playerCards.setLayoutY(440);

        pileCards.setLayoutX(285);
        pileCards.setLayoutY(240);

        return round;
    }

    /**
     * This methods handles the case, where opponent wins the turn
     * @param pile Pile instance
     * @param pileCards StackPane instance for pile cards
     * @param lastRoundWinner winner of last turn (0 for opponent, 1 for player)
     * @param firstRoundWinner winner of first turn (0 for opponent, 1 for player)
     * @return winner of last turn and winner of first turn
     */
    static int[] opponentWinning(Pile pile, StackPane pileCards, int lastRoundWinner, int firstRoundWinner){
        int[] lastAndFirstRound = new int[2];
        String c1 = ((Card) pile.getCards().get(pile.getCards().size()-1)).getRank().toString();
        String c2 = ((Card) pile.getCards().get(pile.getCards().size()-2)).getRank().toString();
        if(((Card) (pileCards.getChildren().get(pileCards.getChildren().size()-1))).getRank() == Card.Rank.JACK || c1.equals(c2)){
            opponentScore += pile.getValueOfPile();
            pile.getCards().clear();
            pile.setValueOfPile(0);
            lastRoundWinner = 0;
            if(firstRoundWinner == -1){
                firstRoundWinner = 0;
            }
        }
        lastAndFirstRound[0] = lastRoundWinner;
        lastAndFirstRound[1] = firstRoundWinner;
        return lastAndFirstRound;
    }

    /**
     * This method handles the case, where player played a card, there are only 2 two cards in pile.
     * Method calls controlPishtiCases method
     * @param playerCards HBox instance for player Cards
     * @param c Player card, which is played last
     * @param pile Pile instance
     * @param lastRoundWinner winner of last turn
     * @param playerCardCount Total number of cards from the turns won by the player
     * @param random Random instance for rotating the last card in pile randomly
     * @return winner of last turn and total number of cards from the turns won by the player
     */
    static int[] playerTwoCards(HBox playerCards,
                               Node c,
                               Pile pile, int lastRoundWinner,
                               int playerCardCount, Random random){
        int[] lastRoundAndPlayerCardCount = new int[2];
        playerCards.getChildren().remove(c);
        pile.addCard((Card) c);
        pile.getCards().get(pile.getCards().size()-1).setRotate(random.nextInt(360));

        if(pile.getCards().size() == 2){
            String rankPlayerCard2 = ((Card) pile.getCards().get(1)).getRank().toString();
            String rankPlayerCard1 = ((Card) pile.getCards().get(0)).getRank().toString();

            if (rankPlayerCard1.equals(rankPlayerCard2)){
                int[] values = LevelController.controlPishtiCases(c, playerCardCount, pile);
                lastRoundWinner = values[0];
                playerCardCount = values[1];
            }
        }
        lastRoundAndPlayerCardCount[0] = lastRoundWinner;
        lastRoundAndPlayerCardCount[1] = playerCardCount;
        return lastRoundAndPlayerCardCount;
    }

    /**
     * This method handles the case opponent played card and there're only 2 cards in pile.
     * @param pile Pile instance
     * @param c Last played card by opponent
     * @param lastRoundWinner winner of last turn
     * @return winner of last turn
     */
    static int opponentWinning(Pile pile, Node c,
                                int lastRoundWinner){
        String rankOppCard2 = ((Card) pile.getCards().get(1)).getRank().toString();
        String rankOppCard1 = ((Card) pile.getCards().get(0)).getRank().toString();
        if (rankOppCard1.equals(rankOppCard2)){
            if(((Card) c).getRank() == Card.Rank.JACK){
                lastRoundWinner = 0;
                opponentScore += 20;
            }
            else{
                lastRoundWinner = 0;
                opponentScore += 10;
            }
            pile.getCards().clear();
        }
        return lastRoundWinner;
    }
}
