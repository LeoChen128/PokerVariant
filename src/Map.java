import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Map extends JPanel {

    private Image image;

    public Map(Image image){
        ImageIcon picture = new ImageIcon("field/map.png");
        this.image = picture.getImage();
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

}
