import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public class Card {
    private String suit;
    private String value;
    private String imageFileName;
    private boolean show;
    private BufferedImage image;
    private Rectangle cardBox;

    public Card(String suit, String value) {
        this.suit = suit;
        this.value = value;
        this.imageFileName = "images/card_" + suit + "_" + value + ".png";
        this.show = true;
        this.cardBox = new Rectangle(-100, -100, 71, 96);
        loadDefaultImage();
    }

    public void loadDefaultImage() {
        image = new BufferedImage(71, 96, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 71, 96);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, 70, 95);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(value, 5, 20);
        g2d.drawString(suit, 5, 40);
        g2d.dispose();
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