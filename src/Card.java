import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Card {
    private String suit;
    private String value;
    private String imageFileName;
    private BufferedImage image;
    private Rectangle cardBox;

    public Card(String suit, String value) {
        this.suit = suit;
        this.value = value;
        this.imageFileName = "images/card_"+suit+"_"+value+".png";
        this.cardBox = new Rectangle(-100, -100, 71, 96);
        this.image = readImage();
    }


    public BufferedImage readImage() {
        try {
            BufferedImage image;
            image = ImageIO.read(new File(imageFileName));
            return image;
        }
        catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    public String getSuit() { return suit; }
    public String getValue() { return value; }
    public BufferedImage getImage() { return image; }
    public Rectangle getCardBox() { return cardBox; }
    public void setRectangleLocation(int x, int y) { cardBox.setLocation(x, y); }

    public int getNumericalValue() {
        if (value.equals("A")) return 14;
        if (value.equals("K")) return 13;
        if (value.equals("Q")) return 12;
        if (value.equals("J")) return 11;
        return Integer.parseInt(value);
    }

    public static ArrayList<Card> buildDeck() {
        ArrayList<Card> deck = new ArrayList<Card>();
        String[] suits = {"clubs", "diamonds", "hearts", "spades"};
        String[] values = {"02", "03", "04", "05", "06", "07", "08", "09", "10", "J", "Q", "K", "A"};
        for (String s : suits) {
            for (String v : values) {
                deck.add(new Card(s, v));
            }
        }
        Collections.shuffle(deck);
        return deck;
    }
}