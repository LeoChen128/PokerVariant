import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
public class Main {
    public static void main(String[] args) {
//        JFrame f = new JFrame("Poker");
//        Map map = new Map();
//        f.setSize(1000,800);
//        Frame frame = new Frame("Poker");
//        Map map = new Map("field/map.png");
//        map.setPreferredSize(new Dimension(800, 600));
//
//        frame.getContentPane().add(map);
//        frame.pack();
//        frame.setVisible(true);
        ImageIcon picture = new ImageIcon("field/map.png");
        JFrame f = new JFrame("Poker");
        Map image = new Map(picture.getImage());
        f.setSize(1000, 800);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(image);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}