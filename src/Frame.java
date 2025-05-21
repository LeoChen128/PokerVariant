import javax.swing.JFrame;

public class Frame extends JFrame implements Runnable {

    private Game p;
    private Thread windowThread;

    public Frame(String display) {
        super(display);
        int frameWidth = 1000;
        int frameHeight = 600;
        p = new Game();
        this.add(p);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(frameWidth, frameHeight);
        this.setLocation(600, 100);
        this.setVisible(true);
        startThread();

    }

    public void startThread() {
        windowThread = new Thread(this);
        windowThread.start();
    }

    public void run() {
        while (true) {
            p.repaint();
        }
    }
}