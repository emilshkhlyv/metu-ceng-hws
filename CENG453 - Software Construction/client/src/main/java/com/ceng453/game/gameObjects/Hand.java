package com.ceng453.game.gameObjects;

import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import javafx.scene.Node;

@Getter
@Setter
public class Hand {
    private ObservableList<Node> cards;

    public Hand(ObservableList<Node> cards){
        this.cards = cards;
    }

    public void takeCard(Card card){
        cards.add(card);
    }

    public void putCard(Card card){
        cards.remove(card);
    }
}
