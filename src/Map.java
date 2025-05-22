import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Map extends JPanel {

    private Image image;

    public Map(){
        ImageIcon picture = new ImageIcon("field/map.png");
        image = picture.getImage();
//        try {
//            image = ImageIO.read(new File(imageFileName));
//        }
//        catch (IOException e) {
//            System.out.println(e);
//        }
    }

    protected void paintComponent(Graphics g){
//        super.paintComponents(g);
//        if (image != null) {
//            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
//        }
        super.paintComponent(g);
        g.drawImage(image,0,0,null);
    }
    public BufferedImage getImage(){
        return (BufferedImage) image;
    }
    public static void main(String[] args) {
        JFrame f = new JFrame("Poker");
        Map image = new Map();
        f.setSize(1000, 800);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(image);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }


}
