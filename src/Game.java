import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.awt.Font;

public class Game extends JPanel implements MouseListener {

    private ArrayList<Card> hand;
    private ArrayList<Card> deck;
    private ArrayList<Card> field;
    private ArrayList<Card> allCards;
    private Rectangle button;
    private Rectangle button2;
    private Rectangle button3;
    private Rectangle button4;




    public Game(){
        button = new Rectangle(50, 400, 80, 26);
        button2 = new Rectangle(150, 400, 80,26);
        button3 = new Rectangle(250, 400, 80,26);
        button4 = new Rectangle(350, 400, 80,26);
        this.addMouseListener(this);
        deck = Card.buildDeck();
        hand = new ArrayList<>();
        allCards = new ArrayList<>();
        field = new ArrayList<>();
        placeField();
    }

    public void placeField(){
        field.clear();
        for (int i = 0; i < 5; i++) {
            if (deck.isEmpty()){
                break;
            }
            int r = (int)(Math.random()*deck.size());
            Card c = deck.remove(r);
            field.add(c);
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
    public int getDiameter(){
        int diameter = Math.min(getWidth(), getHeight());
        return diameter;
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cardWidth = 71;
        int cardHeight = 96;
        int x = 50;
        int y = 10;

        for (int i = 0; i < 5; i++) {
            if (i < field.size()) {
                Card c = field.get(i);
                c.setRectangleLocation(x, y);
                g.drawImage(c.getImage(), x, y, null);
            }

            x += cardWidth + 10;
            if ((i+1) % 3 == 0) {
                x = 50;
                y += cardHeight + 10;
            }
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
        g.drawOval(100,300,30,30);

        g.drawString("5",155, 325);
        g.drawOval(140,300,40,40);
    }

    public boolean flush(ArrayList<Card> cards){
        int spades = 0;
        int diamonds = 0;
        int hearts = 0;
        int clubs = 0;
        ArrayList<String> types = new ArrayList<>();
        for (Card card:cards){
            types.add(card.getSuit());
        }
        for (String type : types){
            if(type.equals("spades")) spades++;
            if(type.equals("diamonds")) diamonds++;
            if(type.equals("hearts")) hearts++;
            if(type.equals("clubs")) clubs++;
        }
        if (spades == 5) return true;
        if (diamonds == 5) return true;
        if (hearts == 5) return true;
        if (clubs == 5) return true;
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

    public boolean fourOfKind(ArrayList<Card> cards){
        int spades = 0;
        int diamonds = 0;
        int hearts = 0;
        int clubs = 0;
        ArrayList<String> types = new ArrayList<>();
        for (Card card:cards){
            types.add(card.getSuit());
        }
        for (String type : types){
            if(type.equals("spades")) spades++;
            if(type.equals("diamonds")) diamonds++;
            if(type.equals("hearts")) hearts++;
            if(type.equals("clubs")) clubs++;
        }
        if (spades == 4) return true;
        if (diamonds == 4) return true;
        if (hearts == 4) return true;
        if (clubs == 4) return true;
        return false;
    }

    public boolean threeOfKind(ArrayList<Card> cards){
        int spades = 0;
        int diamonds = 0;
        int hearts = 0;
        int clubs = 0;
        ArrayList<String> types = new ArrayList<>();
        for (Card card:cards){
            types.add(card.getSuit());
        }
        for (String type : types){
            if(type.equals("spades")) spades++;
            if(type.equals("diamonds")) diamonds++;
            if(type.equals("hearts")) hearts++;
            if(type.equals("clubs")) clubs++;
        }
        if (spades == 3) return true;
        if (diamonds == 3) return true;
        if (hearts == 3) return true;
        if (clubs == 3) return true;
        return false;
    }

    public boolean pair(ArrayList<Card> cards){
        int spades = 0;
        int diamonds = 0;
        int hearts = 0;
        int clubs = 0;
        ArrayList<String> types = new ArrayList<>();
        for (Card card:cards){
            types.add(card.getSuit());
        }
        for (String type : types){
            if(type.equals("spades")) spades++;
            if(type.equals("diamonds")) diamonds++;
            if(type.equals("hearts")) hearts++;
            if(type.equals("clubs")) clubs++;
        }
        if (spades == 2) return true;
        if (diamonds == 2) return true;
        if (hearts == 2) return true;
        if (clubs == 2) return true;
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
//        for (String type : types){
//            if(type.equals("spades")) spades++;
//            if(type.equals("diamonds")) diamonds++;
//            if(type.equals("hearts")) hearts++;
//            if(type.equals("clubs")) clubs++;
//        }
    }
//    public boolean fullHouse(ArrayList<Card> cards){
//
//    }


//    public boolean twoPair(ArrayList<Card> cards){
//
//    }

//    public boolean straightFlush(ArrayList<Card> cards){
//
//    }

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
//        if (fullHouse(allCards)) return 7;
        if (flush(allCards)) return 6;
//        if (straight(allCards)) return 5;
        if (threeOfKind(allCards)) return 4;
//        if (twoPair(allCards)) return 3;
        if (pair(allCards)) return 2;
        return 1;
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
}

