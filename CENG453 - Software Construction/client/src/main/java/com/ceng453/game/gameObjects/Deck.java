package com.ceng453.game.gameObjects;

import javafx.scene.Parent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Deck of cards game object class
 */
@Getter
@Setter
public class Deck extends Parent {
    /**
     * deck of cards
     */
    private ArrayList<Card> deck;

    /**
     * Constructor
     */
    public Deck(){
        deck = new ArrayList<>();
        for(Card.Suit suit: Card.Suit.values()){
            for(Card.Rank rank: Card.Rank.values()){
                if(rank == Card.Rank.JACK){
                    deck.add(new Card(suit, rank, 1));
                }
                else if(rank == Card.Rank.ACE){
                    deck.add(new Card(suit, rank, 1));
                }
                else if(suit == Card.Suit.DIAMONDS && rank == Card.Rank.TEN){
                    deck.add(new Card(suit, rank, 3));
                }
                else if(suit == Card.Suit.CLUBS && rank == Card.Rank.TWO){
                    deck.add(new Card(suit, rank, 2));
                }
                else {
                    deck.add(new Card(suit, rank, 0));
                }
            }
        }
    }

    /**
     * finds size of the array list deck
     * @return size of the deck
     */
    protected int size(){
        return deck.size();
    }

    /**
     * It shuffles the cards in deck
     */
    public void shuffleDeck(){
        Collections.shuffle(deck);
    }

    /**
     * It returns the card in left hand side of the array list.
     * If arraylist is empty, it returns null
     * @return card or null
     */
    public Card getTopCard(){
        if (deck.size() > 0){
            return deck.remove(0);
        }
        else return null;
    }
}
