package com.ceng453.game.gameObjects;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import java.io.File;
import java.util.Locale;

/**
 * Card game object class that holds card information
 */
@Getter
@Setter
public class Card extends Parent {

    /**
     * Identifies 4 types of Suits
     */
    public enum Suit{
        HEARTS,DIAMONDS,CLUBS,SPADES
    }

    /**
     * Identifies 13 types of Ranks
     */
    public enum Rank{
        TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,TEN,JACK,QUEEN,KING,ACE
    }

    /**
     * Suit of the card
     */
    private final Suit suit;

    /**
     * Rank of the card
     */
    private final Rank rank;

    /**
     * value of the card
     */
    private final int value;

    /**
     * Image of the card
     */
    private Image image;

    /**
     * Constructor the card object.
     * It creates image view for card object and assign it to its own image.
     * @param suit suit of the card
     * @param rank rank of the card
     * @param value value of the card
     */
    public Card(Suit suit, Rank rank, int value) {
        this.suit = suit;
        this.rank = rank;
        this.value = value;
        String fileName = rank.toString().toLowerCase(Locale.ENGLISH)+ "_of_"+suit.toString().toLowerCase(Locale.ENGLISH)+".png";
        File file = new File("src/main/resources/images/cards/"+fileName);
        image = new Image(String.valueOf(file.toURI()));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(120);
        imageView.setFitWidth(80);
        getChildren().addAll(imageView);
    }

    public void makeClosed(){
        File file = new File("src/main/resources/images/cards/backcard.png");
        image = new Image(String.valueOf(file.toURI()));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(120);
        imageView.setFitWidth(80);
        getChildren().clear();
        getChildren().addAll(imageView);
    }

    public void makeOpen(){
        String fileName = rank.toString().toLowerCase(Locale.ENGLISH)+ "_of_"+suit.toString().toLowerCase(Locale.ENGLISH)+".png";
        File file = new File("src/main/resources/images/cards/"+fileName);
        image = new Image(String.valueOf(file.toURI()));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(120);
        imageView.setFitWidth(80);
        getChildren().clear();
        getChildren().addAll(imageView);
    }



}
