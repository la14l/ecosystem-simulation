import java.awt.*;
import java.util.ArrayList;

public class Lake {
    int x, y;
    static int size = SimPanel.UNIT_SIZE * 4;
    static ArrayList<Lake> lakes = new ArrayList<>();


    public Lake(int x, int y) {
        this.x = x;
        this.y = y;
        lakes.add(this);
    }

    public void draw(Graphics g) {
        g.setColor(new Color(0x071A25));
        g.fillOval(x, y, size, size);
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
                for (Bush bush : Bush.bushes) {
                    double distance = Math.hypot((bush.x - x_pos - (double) Bush.size / 2), (bush.y - y_pos - (double) Bush.size / 2));
                    if (distance < 3 * size) {
                        close = true;
                        break;
                    }
                }

                for (Lake lake : lakes) {
                    double distance = Math.hypot((lake.x - x_pos - (double) size / 2), (lake.y - y_pos - (double) size / 2));
                    if (distance < 3 * size) {
                        close = true;
                        break;
                    }
                }
            } while (close);

            new Lake(x_pos, y_pos);
        }
    }

    public int goobletLakeDistance(Gooblet gooblet) {
        int gRadius = Gooblet.size / 2;
        int bRadius = Lake.size / 2;

        int gCenterX = gooblet.x + gRadius;
        int gCenterY = gooblet.y + gRadius;

        int bCenterX = this.x + bRadius;
        int bCenterY = this.y + bRadius;

        int dx = bCenterX - gCenterX;
        int dy = bCenterY - gCenterY;

        return (int) Math.hypot(dx, dy);
    }


    public boolean drinkable(Gooblet gooblet) {
        int distance = goobletLakeDistance(gooblet);
        int drinkDistance = Gooblet.size / 2 + Lake.size / 2;
        return distance <= drinkDistance;
    }
}
