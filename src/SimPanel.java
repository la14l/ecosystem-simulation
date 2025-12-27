import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class SimPanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 900;
    static final int SCREEN_HEIGHT = 720;
    static final int UNIT_SIZE = 20;
    static final int DELAY = 4;
    boolean running = false;
    Timer timer;
    static ArrayList<Gooblet> goobletsCopy = new ArrayList<>();

    SimPanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(0x000000));
        this.setFocusable(true);
        startSim();
    }

    public void startSim() {
        Gooblet.spawn(12);
        Bush.spawn(4);
        Lake.spawn(4);
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        for (Bush bush : Bush.bushes) {
            bush.draw(g);
        }

        for (Lake lake : Lake.lakes) {
            lake.draw(g);
        }

        for (Gooblet gooblet : Gooblet.gooblets) {
            gooblet.draw(g);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.PLAIN, 14));

        int x = 10;
        int y = 20;
        double[] stats = stats();
        g.drawString(String.format("AveSpeed: %.2f", stats[0]), x, y);
        g.drawString(String.format("AveSight: %.2f", stats[1]), x, y + 16);
        g.drawString(String.format("Population: %d", Gooblet.gooblets.size()), x, y + 32);
    }

    public double[] stats() {
        double sumSight = 0;
        double sumSpeed = 0;

        for (Gooblet gooblet : Gooblet.gooblets) {
            sumSight += gooblet.sight;
            sumSpeed += gooblet.speed;
        }

        int divider = Gooblet.gooblets.isEmpty() ? 1 : Gooblet.gooblets.size();
        return new double[] {sumSpeed/divider, sumSight/divider};
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Some stuff
        goobletsCopy = new ArrayList<>(Gooblet.gooblets);

        for (Gooblet gooblet : goobletsCopy) {
            gooblet.live();
        }

        for (Bush bush : Bush.bushes) {
            bush.regenerate();
        }

        repaint();
    }
}
