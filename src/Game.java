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
        g.setFont(new Font("Courier New", Font.BOLD, 20));
        g.drawString("Check", 60, 420);
        g.drawRect((int)button.getX(), (int)button.getY(), (int)button.getWidth(), (int)button.getHeight());
        g.setFont(new Font("Courier New", Font.BOLD, 20));
        g.drawString("Fold", 160, 420);
        g.drawRect((int)button2.getX(), (int)button2.getY(), (int)button2.getWidth(), (int)button2.getHeight());
        g.drawString("Pot", 260, 420);
        g.drawRect((int)button3.getX(), (int)button3.getY(), (int)button3.getWidth(), (int)button3.getHeight());
        g.drawString(String.format("%d\n---\n%d",1,2), 360, 420);
        g.drawRect((int)button4.getX(), (int)button4.getY(), (int)button4.getWidth(), (int)button4.getHeight());

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point click = e.getPoint();

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
