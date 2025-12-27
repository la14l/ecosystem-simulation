import javax.swing.*;
import java.awt.*;

public class SimFrame extends JFrame {
    SimFrame() {
        this.add(new SimPanel());
        this.setTitle("Ecosystem Simulation");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}

