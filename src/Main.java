import java.awt.Dimension;
public class Main {
    public static void main(String[] args) {
        Frame frame = new Frame("Poker");
        Map map = new Map("field/map.png");
        map.setPreferredSize(new Dimension(800, 600));

        frame.getContentPane().add(map);
        frame.pack();
        frame.setVisible(true);
    }
}