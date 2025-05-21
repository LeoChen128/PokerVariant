import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Map extends JPanel {

    private Image image;

    public Map(String imageFileName){
        try {
            image = ImageIO.read(new File(imageFileName));
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }



}
