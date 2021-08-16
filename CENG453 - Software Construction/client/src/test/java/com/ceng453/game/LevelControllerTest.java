package com.ceng453.game;

import com.ceng453.game.constants.Constants;
import com.ceng453.game.controller.LevelController;
import com.ceng453.game.gameObjects.Card;
import com.ceng453.game.gameObjects.Deck;
import com.ceng453.game.gameObjects.Hand;
import com.ceng453.game.gameObjects.Pile;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Random;

@RequiredArgsConstructor
public class LevelControllerTest extends ApplicationTest {
    private Deck deck;
    private Pile pile;
    private final Random random = new Random();

    @Override
    public void start(Stage stage) {
        deck = new Deck();
        StackPane pileCards = new StackPane();
        pile = new Pile(pileCards.getChildren());

    }
    @Test
    public void dealCardTest(){
        HBox playerCards = new HBox(10);
        Hand player = new Hand(playerCards.getChildren());
        HBox opponentCards = new HBox(10);
        Hand opponent = new Hand(opponentCards.getChildren());
        LevelController.dealCard(player,deck,opponent);

        Assertions.assertEquals(player.getCards().size(),4);
        Assertions.assertEquals(opponent.getCards().size(),4);
    }

    @Test
    public void startGameTest(){
        HBox playerCards = new HBox(10);
        Hand threeCard = new Hand(playerCards.getChildren());
        LevelController.startGame(deck,random,pile,threeCard);

        Assertions.assertEquals(pile.getCards().size(),4);
        Assertions.assertEquals(deck.getDeck().size(),48);
    }

    @Test
    public void controlPishtiCasesJackTest(){
        Card card = new Card(Card.Suit.CLUBS,Card.Rank.JACK,1);
        pile.addCard(card);
        Card playerCard = new Card(Card.Suit.SPADES,Card.Rank.JACK,1);
        Constants.playerScore = 0L;

        LevelController.controlPishtiCases(playerCard,2,pile);

        Assertions.assertEquals(pile.getCards().size(),0);
        Assertions.assertEquals(Constants.playerScore,20);

    }

    @Test
    public void controlPishtiCasesTest(){
        Card card = new Card(Card.Suit.CLUBS,Card.Rank.ACE,1);
        pile.addCard(card);
        Card playerCard = new Card(Card.Suit.SPADES,Card.Rank.ACE,1);
        Constants.playerScore = 0L;

        LevelController.controlPishtiCases(playerCard,2,pile);

        Assertions.assertEquals(pile.getCards().size(),0);
        Assertions.assertEquals(Constants.playerScore,10);
        Constants.playerScore = 0L;

    }


}
