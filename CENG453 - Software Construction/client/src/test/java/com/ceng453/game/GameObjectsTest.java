package com.ceng453.game;

import com.ceng453.game.gameObjects.Card;
import com.ceng453.game.gameObjects.Deck;
import com.ceng453.game.gameObjects.Hand;
import com.ceng453.game.gameObjects.Pile;
import javafx.scene.layout.HBox;
import org.junit.Test;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;


public class GameObjectsTest extends ApplicationTest {

    private Card card;
    private Deck deck;
    private Pile pile;
    private StackPane pileCards;


    @Override
    public void start(Stage stage) {
        card = new Card(Card.Suit.DIAMONDS, Card.Rank.TEN,3);
        deck = new Deck();

    }


    @Test
    public void createCardTest(){
        Assertions.assertEquals(card.getRank(), Card.Rank.TEN);
    }

    @Test
    public void createDeckTest(){
        Assertions.assertEquals(deck.getDeck().size(),52);
    }

    @Test
    public void cardValuesTest(){
        for(Card c: deck.getDeck()){
            if(c.getRank().equals(Card.Rank.JACK) || c.getRank().equals(Card.Rank.ACE)){
                Assertions.assertEquals(c.getValue(),1);
            }
            if(c.getRank().equals(Card.Rank.TEN) && c.getSuit().equals(Card.Suit.DIAMONDS)){
                Assertions.assertEquals(c.getValue(),3);
            }
            if(c.getRank().equals(Card.Rank.TWO) && c.getSuit().equals(Card.Suit.CLUBS)){
                Assertions.assertEquals(c.getValue(),2);
            }
        }
    }

    @Test
    public void addCardToPileTest(){
        pileCards = new StackPane();
        pile = new Pile(pileCards.getChildren());
        Card card = new Card(Card.Suit.DIAMONDS, Card.Rank.TEN,3);
        Card card1 = new Card(Card.Suit.HEARTS, Card.Rank.FIVE,0);
        Card card2 = new Card(Card.Suit.SPADES,Card.Rank.ACE,1);

        pile.addCard(card);
        Assertions.assertEquals(pile.getValueOfPile(),3);

        pile.addCard(card1);
        Assertions.assertEquals(pile.getValueOfPile(),3);

        pile.addCard(card2);
        Assertions.assertEquals(pile.getValueOfPile(),4);
    }

    @Test
    public void clearPileTest(){
        pileCards = new StackPane();
        pile = new Pile(pileCards.getChildren());
        Card card = new Card(Card.Suit.DIAMONDS, Card.Rank.TEN,3);
        Card card1 = new Card(Card.Suit.HEARTS, Card.Rank.FIVE,0);
        Card card2 = new Card(Card.Suit.SPADES,Card.Rank.ACE,1);
        pile.addCard(card);
        pile.addCard(card1);
        pile.addCard(card2);

        pile.resetPile();
        Assertions.assertEquals(pile.getValueOfPile(),0L);
        Assertions.assertEquals(pile.getCards().size(),0);

    }

    @Test
    public void createHandTest(){
        HBox playerCards = new HBox(10);
        Hand hand = new Hand(playerCards.getChildren());
        Card card = new Card(Card.Suit.DIAMONDS, Card.Rank.TEN,3);
        Card card1 = new Card(Card.Suit.HEARTS, Card.Rank.FIVE,0);
        Card card2 = new Card(Card.Suit.SPADES,Card.Rank.ACE,1);
        Card card3 = new Card(Card.Suit.CLUBS, Card.Rank.EIGHT,0);
        hand.takeCard(card);
        hand.takeCard(card1);
        hand.takeCard(card2);
        hand.takeCard(card3);

        Assertions.assertEquals(hand.getCards().size(),4);
    }

    @Test
    public void takeCardToHandTest(){
        HBox playerCards = new HBox(10);
        Hand hand = new Hand(playerCards.getChildren());
        Card card = new Card(Card.Suit.DIAMONDS, Card.Rank.TEN,3);

        hand.takeCard(card);
        Assertions.assertEquals(hand.getCards().size(),1);
    }

    @Test
    public void putCardFromHandTest(){
        HBox playerCards = new HBox(10);
        Hand hand = new Hand(playerCards.getChildren());
        Card card = new Card(Card.Suit.DIAMONDS, Card.Rank.TEN,3);
        Card card1 = new Card(Card.Suit.HEARTS, Card.Rank.FIVE,0);
        hand.takeCard(card);
        hand.takeCard(card1);

        Assertions.assertEquals(hand.getCards().size(),2);

        hand.putCard((Card) hand.getCards().get(0));
        Assertions.assertEquals(hand.getCards().size(),1);


    }
}
