package com.ceng453.game.gameObjects;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pile {
    private int valueOfPile = 0;
    private ObservableList<Node> cards;

    public Pile(ObservableList<Node> cards){
        this.cards = cards;
    }

    public void addCard(Card card){
        this.valueOfPile += card.getValue();
        this.cards.add(card);
    }

    public void resetPile(){
        this.cards.clear();
        this.valueOfPile = 0;
    }
}
