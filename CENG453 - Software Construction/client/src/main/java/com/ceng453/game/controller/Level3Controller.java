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
 * Level 3 controller class
 */
public class Level3Controller implements Initializable {

    /**
     * Level 3 Pane from fxml file
     */
    @FXML
    AnchorPane level3pane;

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
     * HBox instance for showing player's cards
     */
    private HBox playerCards ;

    /**
     * Hand instance for player
     */
    private Hand player;

    /**
     * HBox instance for showing opponent's cards
     */
    private HBox opponentCards;

    /**
     * Hand instance for opponent
     */
    private Hand opponent;

    /**
     * StackPane instance for showing cards in pile
     */
    private StackPane pileCards;

    /**
     * Pile instance
     */
    private Pile pile;

    /**
     * Pane instance for showing closed cards in case of player winning in first turn
     */
    private Pane firstClosedCards;

    /**
     * KeyCombination instance for control+9 keys combination for hint
     */
    private KeyCombination controlNine;

    /**
     * Pane instance for showing bluffing options to player
     */
    private Pane bluffButtonsPlayer;

    /**
     * Button instance for Bluffing
     */
    private Button bluffButton;

    /**
     * Button instance for playing card without bluffing
     */
    private Button normalButton;

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
     * Flag for specifying if opponent selected to bluff or not
     * 1 stands for bluff, 0 stands for normal playing
     */
    private int isClosed = 1;

    /**
     * Random variable for computer's moves
     */
    Random random = new Random();

    /**
     * Overridden initialize method for initial setup
     * @param url url
     * @param resourceBundle resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerScore = 0L;
        opponentScore = 0L;
        initialSetup();
    }

    /**
     * This method initializes game before each round
     */
    public void initialSetup(){
        deck  = new Deck();

        playerCards = new HBox(10);
        opponentCards = new HBox(10);
        pileCards = new StackPane();
        bluffButtonsPlayer = new Pane();

        player = new Hand(playerCards.getChildren());
        opponent = new Hand(opponentCards.getChildren());
        pile = new Pile(pileCards.getChildren());

        bluffButton = new Button();
        normalButton = new Button();
        firstRoundWinner = -1;
        isClosed = 1;

        deck.shuffleDeck();
        deckImage.setVisible(true);
        playerCardCount = 0;

        HBox threeCards = new HBox(10);
        Hand threeCard = new Hand(threeCards.getChildren());
        threeCards.setLayoutX(100);
        threeCards.setLayoutY(100);

        LevelController.startGame(deck, random, pile, threeCard);
        round = LevelController.tableInitializer(opponentCards, playerCards, pileCards, roundLabel, opponentScoreLabel, yourScoreLabel, round);

        level3pane.getChildren().addAll(playerCards, opponentCards, pileCards);
        firstClosedCards = LevelController.showFirstClosedCards(level3pane, threeCards);
        makeRoundLevel3();

        controlNine = new KeyCodeCombination(KeyCode.DIGIT9, KeyCombination.CONTROL_DOWN);
        level3pane.setOnKeyPressed(keyEvent -> {
            if(controlNine.match(keyEvent)){
                playerScore = 151L;
                showLevel3Result(1);
            }
        });
    }

    /**
     * This method calls makeRound method, when the Deal Card button on deck is pressed
     */
    @FXML
    public void onClickDealCardButton(){
        makeRoundLevel3();
    }

    /**
     * This method handles dealing cards and assign event handler to each player's card and
     * sets deckImage to invisible id there's no card in deck
     */
    public void makeRoundLevel3(){
        if (deck.getDeck().size() >= 4 && playerCards.getChildren().size() == 0 && opponentCards.getChildren().size() == 0){
            LevelController.dealCard(player, deck, opponent);
            for (Node c : playerCards.getChildren()) {
                c.setOnMouseClicked(event -> {
                    if(pile.getCards().size() == 1){
                        level3pane.getChildren().removeAll(bluffButtonsPlayer);
                        showBluffButtonsPlayer(c);
                    }
                    else {
                        int[] lastRoundAndPlayerCardCount = LevelController.playerTwoCards(playerCards, c, pile, lastRoundWinner, playerCardCount, random);
                        lastRoundWinner = lastRoundAndPlayerCardCount[0];
                        playerCardCount = lastRoundAndPlayerCardCount[1];

                        if (pile.getCards().size() >= 2){
                            String c2 = ((Card) pile.getCards().get(pile.getCards().size()-2)).getRank().toString();
                            String c1 = ((Card) pile.getCards().get(pile.getCards().size()-1)).getRank().toString();
                            if(((Card) (pileCards.getChildren().get(pileCards.getChildren().size()-1))).getRank() == Card.Rank.JACK || c1.equals(c2)){
                                playerCardCount += pile.getCards().size();
                                playerScore += pile.getValueOfPile();
                                pile.setValueOfPile(0);
                                pile.getCards().clear();
                                lastRoundWinner = 1;
                                if(firstRoundWinner == -1){
                                    firstRoundWinner = 1;
                                    level3pane.getChildren().addAll(firstClosedCards);
                                }
                            }
                        }

                        opponentPlaysNormal((Card) c);
                    }
                });
            }
            if(deck.getDeck().size()==0){
                deckImage.setVisible(false);
            }
        }
    }

    /**
     * This method handles the cases if opponent decided to play normal without bluffing
     * @param c Opponent's card, which is selected by opponent
     */
    private void opponentPlaysNormal(Card c) {
        opponentBluffingChoice();
        if(pile.getCards().size() == 2 && isClosed == 0){
            String rankOppCard1 = ((Card) pile.getCards().get(0)).getRank().toString();
            String rankOppCard2 = ((Card) pile.getCards().get(1)).getRank().toString();
            if (rankOppCard1.equals(rankOppCard2)){
                if(c.getRank() == Card.Rank.JACK){
                    opponentScore += 20;
                }
                else{
                    opponentScore += 10;
                }
                lastRoundWinner = 0;
                pile.getCards().clear();
            }
        }
        if (pile.getCards().size() >= 2 && isClosed == 0){
            String c1 = ((Card) pile.getCards().get(pile.getCards().size()-1)).getRank().toString();
            String c2 = ((Card) pile.getCards().get(pile.getCards().size()-2)).getRank().toString();
            if(((Card) (pileCards.getChildren().get(pileCards.getChildren().size()-1))).getRank() == Card.Rank.JACK || c1.equals(c2)){
                opponentScore += pile.getValueOfPile();
                opponentWinnerCase();
            }
        }
        if(deck.getDeck().size()==0){
            if(playerCards.getChildren().size() == 0 && opponentCards.getChildren().size() == 0){
                levelEnd();
            }
        }
    }

    /**
     * This method resets pile and make appropriate variables 0 in case of turn winning of opponent
     */
    private void opponentWinnerCase() {
        pile.getCards().clear();
        pile.setValueOfPile(0);
        lastRoundWinner = 0;
        if(firstRoundWinner == -1){
            firstRoundWinner = 0;
        }
    }

    /**
     * This method decides opponent will bluff or not in case of there's only one card in pile
     * There's %30 probability computer will bluff
     */
    private void opponentBluffingChoice() {
        int opponentCardIndex = LevelController.opponentCardSelection(random, opponentCards, pile);
        if(pile.getCards().size() == 1){
            if(random.nextFloat() <= 0.7f){
                ((Card)opponentCards.getChildren().get(opponentCardIndex)).makeOpen();
                isClosed = 0;
            }
            else{
                ((Card)opponentCards.getChildren().get(opponentCardIndex)).makeClosed();
                showBluffOrNotButtons(((Card)opponentCards.getChildren().get(opponentCardIndex)));
                isClosed = 1;
            }
        }
        else if(pile.getCards().size() != 1){
            ((Card)opponentCards.getChildren().get(opponentCardIndex)).makeOpen();
            isClosed = 0;
        }
        pile.addCard((Card) opponentCards.getChildren().get(opponentCardIndex));
    }

    /**
     * This method calls lastRoundCalculation method and updates Player Score and Opponent Score at the end of each level
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
        if(playerScore>=151){
            if(opponentScore<151){
                showLevel3Result(1);
            }
            else {
                if(playerScore>=opponentScore){
                    showLevel3Result(1);
                }
                else{
                    showLevel3Result(0);
                }
            }
        }
        else{
            if(opponentScore>=151){
                showLevel3Result(0);
            }
            else initialSetup();
        }
    }

    /**
     * This method handles showing bluffing buttons for player and
     * assigning event handlers to these buttons
     * @param c Player's Card, which will be pressed on Screen
     */
    public void showBluffButtonsPlayer(Node c){
        bluffButton.setText("Bluff!");
        bluffButton.setLayoutX(270);
        bluffButton.setLayoutY(400);

        normalButton.setText("Normal");
        normalButton.setLayoutX(330);
        normalButton.setLayoutY(400);

        Rectangle reo = new Rectangle((player.getCards().size()) *80 + (player.getCards().size() == 1 ? 0 : player.getCards().size()-1)*10, 120);
        reo.setArcWidth(5);
        reo.setArcHeight(5);
        reo.setFill(Color.rgb(200, 200, 200, 0.5));

        reo.setLayoutX(150);
        reo.setLayoutY(440);

        bluffButton.setOnMouseClicked((e) -> {
            ((Card) c).makeClosed();
            bluffButtonsPlayer.getChildren().removeAll(bluffButton, normalButton, reo);
            playerCards.getChildren().remove(c);
            pile.addCard((Card) c);
            pile.getCards().get(pile.getCards().size()-1).setRotate(random.nextInt(360));
            int opponentDecision = random.nextInt(2);
            // TRUST
            if(opponentDecision == 0) {
                opponentTrusted(c);
            }
            // CHALLENGE
            else {
                opponentThinksBluff(c);
            }
        });
        normalButton.setOnMouseClicked((e) -> {
            ((Card) c).makeOpen();
            bluffButtonsPlayer.getChildren().removeAll(bluffButton, normalButton, reo);

            playerCards.getChildren().remove(c);
            pile.addCard((Card) c);
            pile.getCards().get(pile.getCards().size()-1).setRotate(random.nextInt(360));

            if(pile.getCards().size() == 2){
                String rankOppCard1 = ((Card) pile.getCards().get(0)).getRank().toString();
                String rankOppCard2 = ((Card) pile.getCards().get(1)).getRank().toString();
                if (rankOppCard1.equals(rankOppCard2)){
                    if(((Card) c).getRank() == Card.Rank.JACK){
                        playerScore += 20;
                    }
                    else{
                        playerScore += 10;
                    }
                    lastRoundWinner = 1;
                    playerCardCount += pile.getCards().size();
                    pile.getCards().clear();
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
                    }
                }
            }

            opponentBluffingChoice();

            if(pile.getCards().size() == 2 && isClosed == 0){
                String oppCard2 = ((Card) pile.getCards().get(1)).getRank().toString();
                String oppCard1 = ((Card) pile.getCards().get(0)).getRank().toString();
                if (oppCard1.equals(oppCard2)){
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
            if (pile.getCards().size() >= 2 && isClosed == 0){
                String c2 = ((Card) pile.getCards().get(pile.getCards().size()-2)).getRank().toString();
                String c1 = ((Card) pile.getCards().get(pile.getCards().size()-1)).getRank().toString();
                if(((Card) (pileCards.getChildren().get(pileCards.getChildren().size()-1))).getRank() == Card.Rank.JACK || c1.equals(c2)){
                    opponentScore += pile.getValueOfPile();
                    opponentWinnerCase();
                }
            }
            if(deck.getDeck().size()==0){
                if(playerCards.getChildren().size() == 0 && opponentCards.getChildren().size() == 0){
                    levelEnd();
                }
            }
        });
        bluffButtonsPlayer.getChildren().addAll(bluffButton, normalButton, reo);
        level3pane.getChildren().addAll(bluffButtonsPlayer);
    }

    /**
     * This method handles showing buttons for selecting opponent is bluffing or not
     * Trust button stands for opponent is not bluffing
     * Challenge button stands for make opponent exposes bluffing card
     * @param c Card which is selected by opponent to be bluffed
     */
    public void showBluffOrNotButtons(Card c){
        Pane buttons = new Pane();
        Button trust = new Button();
        Button challenge = new Button();

        Rectangle rec = showBluffingButtons();

        challenge.setText("Challenge!");
        challenge.setLayoutX(330);
        challenge.setLayoutY(400);

        trust.setText("Trust!");
        trust.setLayoutX(270);
        trust.setLayoutY(400);

        trust.setOnMouseClicked((e) -> {
            if(((Card) (pile.getCards().get(0))).getRank() == Card.Rank.JACK){
                opponentScore += 20;
            }
            else{
                opponentScore += 10;
            }
            buttons.getChildren().removeAll(trust, challenge, rec);
            pile.getCards().clear();
        });
        challenge.setOnMouseClicked((e) -> {
            c.makeOpen();
            String cardInPile = ((Card) pile.getCards().get(0)).getRank().toString();
            String cardClosed = c.getRank().toString();
            if(((Card) pile.getCards().get(0)).getRank() == Card.Rank.JACK){
                if(c.getRank() == Card.Rank.JACK){
                    opponentScore += 40;
                    pile.getCards().clear();
                }
                else{
                    playerScore += 40;
                }
            }
            else {
                if(c.getRank() == Card.Rank.JACK){
                    opponentScore += pile.getValueOfPile();
                    pile.getCards().clear();
                }
                else if(cardClosed.equals(cardInPile)){
                    opponentScore += 20;
                    pile.getCards().clear();
                }
                else{
                    playerScore += 20;
                }
            }
            buttons.getChildren().removeAll(trust, challenge, rec);
        });
        buttons.getChildren().addAll(trust, challenge, rec);
        level3pane.getChildren().addAll(buttons);
    }

    /**
     * This method deactivate player's cards with putting rectangle on them
     * @return Rectangle instance, which is used for deactivating
     */
    private Rectangle showBluffingButtons() {
        Rectangle rec = new Rectangle((opponent.getCards().size()-1) *80 + (opponent.getCards().size() == 1 ? 0 : opponent.getCards().size()-2)*10, 120);
        rec.setArcWidth(5);
        rec.setArcHeight(5);
        rec.setFill(Color.rgb(200, 200, 200, 0.5));

        rec.setLayoutX(150);
        rec.setLayoutY(440);
        return rec;
    }

    /**
     * This method shows the result of Level 3 to Player
     * if Player wins, there will be green background
     * if Player loses, there will be red background
     * Since we didn't implement Level 4 There will be same Back to Dashboard button for going Dashboard for both cases
     * @param winner This argument is for specifying winner as 0 stands for opponent, 1 stands for Player
     */
    public void showLevel3Result(int winner){
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
            LevelController.winInfo(rec, info1, info2, resultButton, "fxml/level4.fxml","level4");
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
        level3pane.getChildren().addAll(result);
    }

    /**
     * This method handles showing of opponent's decision about player's bluffing try
     * @param rec Rectangle for showing Button cont and Label info
     * @param info Label for opponent's decision
     * @param cont Button for continuing playing
     * @return Rectangle instance, which is used for deactivating player's cards
     */
    public Rectangle showOpponentBluffingDecision(Rectangle rec, Label info, Button cont, int bluff){
        rec.setFill(Color.rgb(200, 200, 200, 0.9));
        rec.setArcWidth(20);
        rec.setArcHeight(20);
        rec.setLayoutX(100);
        rec.setLayoutY(75);

        Rectangle deactivated = showBluffingButtons();
        if(bluff == 1){
            info.setText("Opponent thought you bluffed");
        }
        else{
            info.setText("        Opponent trusted you");
        }
        info.setLayoutX(130);
        info.setLayoutY(160);
        info.setFont(Font.font(30));
        info.setTextFill(Color.BLACK);

        cont.setText("Continue");
        cont.setLayoutX(280);
        cont.setLayoutY(400);

        return deactivated;
    }


    /**
     * This method executes commands, where opponent thinks player is bluffing
     * @param c Card, which is selected by player to be bluffed
     */
    public void opponentThinksBluff(Node c){
        Pane show = new Pane();
        Rectangle rec = new Rectangle(450,450);
        Label info = new Label();
        Button cont = new Button();

        Rectangle deactivated = showOpponentBluffingDecision(rec, info, cont, 1);

        cont.setOnMouseClicked((e) -> {
            show.getChildren().removeAll(info, cont, rec, deactivated);
            if(((Card) pile.getCards().get(0)).getRank() == Card.Rank.JACK){
                if(((Card) c).getRank() == Card.Rank.JACK){
                    playerScore += 40;
                    playerCardCount += pile.getCards().size();
                    pile.getCards().clear();
                }
                else {
                    ((Card) c).makeOpen();
                    opponentScore += 40;
                }
            }
            else {
                if(((Card) c).getRank() == Card.Rank.JACK){
                    playerScore += pile.getValueOfPile();
                    playerCardCount += pile.getCards().size();
                    pile.getCards().clear();
                }
                String cardInPile = ((Card) pile.getCards().get(0)).getRank().toString();
                String playerCard = ((Card) c).getRank().toString();
                if(cardInPile.equals(playerCard)){
                    playerScore += 20;
                    playerCardCount += pile.getCards().size();
                    pile.getCards().clear();
                }
                else{
                    ((Card) c).makeOpen();
                    opponentScore += 20;
                }
            }
            opponentPlaysNormal((Card) c);
        });
        show.getChildren().addAll(rec, info, cont, deactivated);
        level3pane.getChildren().addAll(show);
    }


    /**
     * This method is called, when opponent trusted player' bluff
     * @param c Card, which is selected by player to be bluffed
     */
    public void opponentTrusted(Node c){
        Pane show = new Pane();
        Rectangle rec = new Rectangle(450,450);
        Label info = new Label();
        Button cont = new Button();

        Rectangle deactivated = showOpponentBluffingDecision(rec, info, cont, 0);
        cont.setOnMouseClicked((e) -> {
            show.getChildren().removeAll(info, cont, rec, deactivated);
            if(((Card) pile.getCards().get(0)).getRank() == Card.Rank.JACK){
                playerScore += 20;
            }
            else {
                playerScore += 10;
            }
            playerCardCount += pile.getCards().size();
            pile.getCards().clear();

            opponentBluffingChoice();

            if(pile.getCards().size() == 2 && isClosed == 0){
                lastRoundWinner = LevelController.opponentWinning(pile, c, lastRoundWinner);
            }

            if (pile.getCards().size() >= 2 && isClosed == 0){
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
        show.getChildren().addAll(rec, info, cont, deactivated);
        level3pane.getChildren().addAll(show);
    }
}
