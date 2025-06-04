import javax.imageio.ImageIO;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Chips {

    private String imageFileName;
    private BufferedImage image;
    private Ellipse2D chip;
    private int number;

    public Chips(int number) {
        this.number = number;
        this.imageFileName = "chips/pngimg.com - poker_PNG" + number + ".png";
        this.image = readImage();
        this.chip = new Ellipse2D.Double(-100, -100, 50, 50);
    }

    public Ellipse2D getChip() {
        return chip;
    }

    public int getNumber() {
        return number;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setChipLocation(int x, int y) {
        chip.setFrame(x, y, 50, 50);
    }

    public BufferedImage readImage() {
        try {
            BufferedImage image;
            image = ImageIO.read(new File(imageFileName));
            return image;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }
}
