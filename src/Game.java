import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.Collections;

public class Game extends JPanel implements MouseListener {

    private ArrayList<Card> hand;
    private ArrayList<Card> deck;
    private ArrayList<Card> field;
    private ArrayList<Card> allCards;
    private Rectangle button;
    private Rectangle button2;
    private Rectangle button3;
    private Rectangle button4;
    private ArrayList<Chips> chips;
    private Ellipse2D chip1;
    private Ellipse2D chip2;
    private Ellipse2D chip3;
    private Ellipse2D chip4;
    private Ellipse2D chip5;
    private Ellipse2D chip6;
    private Ellipse2D chip7;

    public Game(){
        button = new Rectangle(50, 400, 80, 26);
        button2 = new Rectangle(150, 400, 80,26);
        button3 = new Rectangle(250, 400, 80,26);
        button4 = new Rectangle(350, 400, 80,26);
        chip1 = new Ellipse2D.Double(100,300,30,30);
        chip2 = new Ellipse2D.Double(140,300,30,30);
        chip3 = new Ellipse2D.Double(180,300,30,30);
        chip4 = new Ellipse2D.Double(220,300,30,30);
        chip5 = new Ellipse2D.Double(260,300,30,30);
        chip6 = new Ellipse2D.Double(300,300,30,30);
        chip7 = new Ellipse2D.Double(340,300,30,30);
        chips = new ArrayList<>();
        this.addMouseListener(this);
//        deck = Card.buildDeck();
        hand = new ArrayList<>();
        allCards = new ArrayList<>();
        field = new ArrayList<>();
        placeField();
        JButton jb1 = new JButton();
        JButton jb2 = new JButton();
        JButton jb3 = new JButton();
        JButton jb4 = new JButton();
        JButton jb5 = new JButton();
        JButton jb6 = new JButton();
        JButton jb7 = new JButton();
    }

    public void placeField(){
        field.clear();
        chips.clear();
        for (int i = 0; i < 5; i++) {
            if (deck.isEmpty()){
                break;
            }
            int r = (int)(Math.random()*deck.size());
            Card c = deck.remove(r);
            field.add(c);
        }
        for (Chips chip : chips){
            chips.add(chip);
        }
    }
    public void setAllCards(){
        for (Card card : hand){
            allCards.add(card);
        }
        for(Card card : field){
            allCards.add(card);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cardWidth = 71;
        int x = 280;
        int y = 180;

        for (int i = 0; i < 3; i++) {
            if (i < field.size()) {
                Card c = field.get(i);
                c.setRectangleLocation(x, y);
                g.drawImage(c.getImage(), x, y, null);
            }

            x += cardWidth + 10;
        }


        g.setFont(new Font("Courier New", Font.BOLD, 20));
        g.drawString("Check", 60, 420);
        g.drawRect((int)button.getX(), (int)button.getY(), (int)button.getWidth(), (int)button.getHeight());
        g.setFont(new Font("Courier New", Font.BOLD, 20));
        g.drawString("Fold", 160, 420);
        g.drawRect((int)button2.getX(), (int)button2.getY(), (int)button2.getWidth(), (int)button2.getHeight());
        g.drawString("Pot", 260, 420);
        g.drawRect((int)button3.getX(), (int)button3.getY(), (int)button3.getWidth(), (int)button3.getHeight());
        g.drawString("1/2", 360, 420);
        g.drawRect((int)button4.getX(), (int)button4.getY(), (int)button4.getWidth(), (int)button4.getHeight());

        g.drawString("1",110,325);
        g.drawOval((int) chip1.getX(), (int) chip1.getY(), (int) chip1.getWidth(), (int) chip1.getHeight());
        g.drawString("5",155, 325);
        g.drawOval((int) chip2.getX(), (int) chip2.getY(), (int) chip2.getWidth(), (int) chip2.getHeight());
        g.drawString("25",195, 325);
        g.drawOval((int) chip3.getX(), (int) chip3.getY(), (int) chip3.getWidth(), (int) chip3.getHeight());
        g.drawString("100",235, 325);
        g.drawOval((int) chip4.getX(), (int) chip4.getY(), (int) chip4.getWidth(), (int) chip4.getHeight());
        g.drawString("500",275, 325);
        g.drawOval((int) chip5.getX(), (int) chip5.getY(), (int) chip5.getWidth(), (int) chip5.getHeight());
        g.drawString("1000",315, 325);
        g.drawOval((int) chip6.getX(), (int) chip6.getY(), (int) chip6.getWidth(), (int) chip6.getHeight());

    }

    public boolean flush(ArrayList<Card> cards) {
        int[] suits = new int[4];
        for (Card card : cards) {
            suits[Integer.parseInt(card.getSuit())]++;
        }
        for (int i = 0; i < 4; i++) {
            if (suits[i] >= 5) {
                return true;
            }
        }
        return false;
    }


    public boolean royalFlush(ArrayList<Card> cards){
        int count = 0;
        ArrayList<Integer> values = new ArrayList<>();
        for (Card card : cards){
            values.add(card.getNumericalValue());
        }
        for (Integer value : values){
            if(value == 14) count++;
            if(value == 13) count++;
            if(value == 12) count++;
            if(value == 11) count++;
            if(value == 10) count++;
        }
        if (flush(cards) && count == 5){
            return true;
        }
        return false;
    }

    public boolean pair(ArrayList<Card> cards) {
    int[] counts = new int[15];
    for (Card card : cards) {
        counts[card.getNumericalValue()]++;
    }
    for (int i = 2; i <= 14; i++) {
        if (counts[i] >= 2) {
            return true;
        }
    }
    return false;
}

    public boolean threeOfKind(ArrayList<Card> cards) {
        int[] counts = new int[15];
        for (Card card : cards) {
         counts[card.getNumericalValue()]++;
     }
        for (int i = 2; i <= 14; i++) {
            if (counts[i] >= 3) {
              return true;
          }
     }
        return false;
    }

    public boolean fourOfKind(ArrayList<Card> cards) {
        int[] counts = new int[15];
        for (Card card : cards) {
            counts[card.getNumericalValue()]++;
        }
        for (int i = 2; i <= 14; i++) {
            if (counts[i] >= 4) {
                return true;
            }
        }
        return false;
    }


    public void removeThreeOfKind(ArrayList<Card> cards){
        int spades = 0;
        int diamonds = 0;
        int hearts = 0;
        int clubs = 0;
        ArrayList<String> types = new ArrayList<>();
        for (Card card:cards){
            types.add(card.getSuit());
        }
        for (int i = 0; i < types.size(); i++){
            if (types.get(i).equals("spades")) spades++;
            if (types.get(i).equals("diamonds")) diamonds++;
            if (types.get(i).equals("hearts")) hearts++;
            if (types.get(i).equals("clubs")) clubs++;

            if(spades == 3 && threeOfKind(cards)){
                types.remove(types.get(i));
            }
            else if(diamonds == 3 && threeOfKind(cards))
            {
                types.remove(types.get(i));
            }
            else if(hearts == 3 && threeOfKind(cards)){
                types.remove(types.get(i));
            }
            else if (clubs == 3){
                types.remove(types.get(i));
            }


        }
    }

    public boolean fullHouse(ArrayList<Card> cards) {
        int[] counts = new int[15];
        boolean hasThree = false;
        boolean hasPair = false;
    
        for (Card card : cards) {
            counts[card.getNumericalValue()]++;
        }

        for (int i = 2; i <= 14; i++) {
            if (counts[i] >= 3) {
                hasThree = true;
                counts[i] -= 3;
                break;
            }
        }

        for (int i = 2; i <= 14; i++) {
            if (counts[i] >= 2) {
                hasPair = true;
                break;
            }
        }

        return hasThree && hasPair;
    }


    public boolean twoPair(ArrayList<Card> cards) {
        int[] counts = new int[15];
        int pairCount = 0;
        for (Card card : cards) {
            counts[card.getNumericalValue()]++;
        }
        for (int i = 2; i <= 14; i++) {
            if (counts[i] >= 2) {
                pairCount++;
            }
        }
        if (pairCount >= 2){
            return true;
        }
        else{
            return false;
        }
    }
    
    public boolean straightFlush(ArrayList<Card> cards) {
        for (int suit = 0; suit < 4; suit++) {
            ArrayList<Card> suitedCards = new ArrayList<Card>();
            for (Card card : cards) {
//                if (card.getSuit() == suit) {
//                    suitedCards.add(card);
//                }
            }
            if (suitedCards.size() >= 5 && straight(suitedCards)) {
                return true;
            }
        }
        return false;
    }

    public boolean straight(ArrayList<Card> cards){
        boolean straight = false;
        int count = 0;
        ArrayList<Integer> values = new ArrayList<>();
        for (Card card : cards){
            values.add(card.getNumericalValue());
        }
        Collections.sort(values);
        for (Integer value : values){
            if(value == 14) count++;
            if(value == 13) count++;
            if(value == 12) count++;
            if(value == 11) count++;
            if(value == 10) count++;
        }


        if (count == 5 || straight){
            return true;
        }
        return true;
    }


    public void removeDuplicates(ArrayList<Card> cards){
        ArrayList<Integer> numberCards = new ArrayList<>();
        for (Card card : cards){
            numberCards.add(card.getNumericalValue());
        }
        for (int i = 0; i < numberCards.size();i++){

        }
    }

    public int result(){
        if (royalFlush(allCards)) return 10;
        //if (straightFLush(allCards)) return 9;
        if (fourOfKind(allCards)) return 8;
        if (fullHouse(allCards)) return 7;
        if (flush(allCards)) return 6;
//        if (straight(allCards)) return 5;
        if (threeOfKind(allCards)) return 4;
//        if (twoPair(allCards)) return 3;
        if (pair(allCards)) return 2;
        return 1;
    }

    public boolean greater(){


        return true;
    }

    public Integer highcard(ArrayList<Card> cards){
        int highestValue = 0;
        ArrayList<Integer> values = new ArrayList<>();
        for (Card card : cards){
            values.add(card.getNumericalValue());
        }
        for (Integer value : values){
            if(value == 14) highestValue = 14;
            if(value == 13) highestValue = 13;
            if(value == 12) highestValue = 12;
            if(value == 11) highestValue = 11;
        }
        return highestValue;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


    public static void main(String[] args) {
        Frame f = new Frame("Poker");
        ImageIcon image = new ImageIcon("field/map.png");
        f.add(image);
        f.setSize(1000, 800);
        f.setDefaultCloseOperation(Frame.EXIT_ON_CLOSE);
        f.add(image);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        ArrayList<Card> deck = new ArrayList<Card>();
        Collections.shuffle(deck);

        boolean userTurn = false;
        boolean dealtCard = false;
        boolean nextTurn = false;
        boolean game = true;

        while (game){
            userTurn = true;

        }
    }

}

