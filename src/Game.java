import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.Font;
import java.awt.Color;

class Game extends JPanel implements MouseListener {

    private ArrayList<Card> hand;
    private ArrayList<Card> deck;
    private ArrayList<Card> field;
    private Rectangle button;

    public Game(){
        deck = Card.buildDeck();
        hand = new ArrayList<>();
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
