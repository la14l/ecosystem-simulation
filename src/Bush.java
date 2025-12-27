import java.awt.*;
import java.util.ArrayList;

public class Bush {
    int x, y;
    static int size = SimPanel.UNIT_SIZE * 2;

    int numberOfFruits = 3;
    static ArrayList<Bush> bushes = new ArrayList<>();

    int counter = 0;

    public Bush(int x, int y) {
        this.x = x;
        this.y = y;
        bushes.add(this);
    }

    public void draw(Graphics g) {
        g.setColor(new Color(18, 81, 12));
        g.fillOval(x, y, size, size);

        for (int i = 0; i < numberOfFruits; i++) {
            g.setColor(new Color(108, 9, 9));
            int appleSize = size / 4;
            g.fillOval(x + i * size / 3 + appleSize / 4, y + size / 2 - appleSize / 4, appleSize, appleSize);

            int stemLength = appleSize / 3;
            g.setColor(new Color(48, 20, 20));
            g.fillRect(x + i * size / 3 + appleSize / 4 + appleSize / 2, y + size / 2 - appleSize / 4 - stemLength * 3 / 4, appleSize / 5, stemLength);
        }
    }

    public static void spawn(int number) {
        for (int i = 0; i < number; i++) {
            int x_pos;
            int y_pos;
            boolean close;

            do {
                x_pos = (int) ((SimPanel.SCREEN_WIDTH - 200) * Math.random()) + 100;
                y_pos = (int) ((SimPanel.SCREEN_HEIGHT - 200) * Math.random()) + 100;

                close = false;
                for (Bush bush : bushes) {
                    double distance = Math.hypot((bush.x - x_pos - (double) Bush.size / 2), (bush.y - y_pos - (double) Bush.size / 2));
                    if (distance < 3 * size) {
                        close = true;
                        break;
                    }
                }

                for (Lake lake : Lake.lakes) {
                    double distance = Math.hypot((lake.x - x_pos - (double) size / 2), (lake.y - y_pos - (double) size / 2));
                    if (distance < 3 * size) {
                        close = true;
                        break;
                    }
                }
            } while (close);

            new Bush(x_pos, y_pos);
        }
    }


    public int goobletBushDistance(Gooblet gooblet) {
        int gRadius = Gooblet.size / 2;
        int bRadius = Bush.size / 2;

        int gCenterX = gooblet.x + gRadius;
        int gCenterY = gooblet.y + gRadius;

        int bCenterX = this.x + bRadius;
        int bCenterY = this.y + bRadius;

        int dx = bCenterX - gCenterX;
        int dy = bCenterY - gCenterY;

        return (int) Math.hypot(dx, dy);
    }


    public boolean edible(Gooblet gooblet) {
        int distance = goobletBushDistance(gooblet);
        int eatDistance = Gooblet.size / 2 + Bush.size / 2;
        return distance <= eatDistance;
    }


    public void regenerate() {
        if (counter > 63 * 12) {
            if (numberOfFruits < 3) numberOfFruits++;
            counter = 0;
        }
        counter++;
    }
}
