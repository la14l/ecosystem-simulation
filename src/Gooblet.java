import java.awt.*;
import java.util.ArrayList;

public class Gooblet {
    int x, y;
    int age  = 0;
    boolean dividing = false;
    static int matingAge = (int) (120 * (1/(SimPanel.DELAY * 0.001)));
    int matingCooldown = 0;
    int[] color = new int[] {255, 255, 255};

    double hunger = 1;
    double hungerRate;
    double poisonProbability = 0.05;
    static final double hungerThreshold = 0.4;

    double thirst = 1;
    double thirstRate;
    static final double thirstThreshold = 0.4;

    double sight;

    static int size = SimPanel.UNIT_SIZE;
    static ArrayList<Gooblet> gooblets = new ArrayList<>();

    int speed;
    int xSpeed;
    int ySpeed;
    int steps = 0;

    Gooblet(int x, int y) {
        this.x = x;
        this.y = y;
        this.sight = Math.random() * 4 + 6;
        this.speed = (int) (size / 4.0 * (Math.random() + 0.5));
        thirstRate = 0.0025 * speed / (size / 2.0) / 3;
        hungerRate = 0.0025 * speed / (size / 2.0) / 3;
        poisonProbability *= sight / 7;
        gooblets.add(this);
    }

    Gooblet(Gooblet parent) {
        this.hunger = 0.5;
        this.thirst = 0.5;
        this.x = parent.x + size;
        this.y = parent.y;
        this.sight = (Math.random() - 1.0/2.0) + parent.sight;
        this.speed = (int) (Math.random() - 1.0/2.0) + parent.speed;
        thirstRate = 0.0025 * speed / (size / 2.0) / 3;
        hungerRate = 0.0025 * speed / (size / 2.0) / 3;
        poisonProbability *= sight / 7;
        gooblets.add(this);
    }

    public void draw(Graphics g) {
        g.setColor(new Color(color[0], color[1], color[2]));
        g.fillOval(x, y, size, size);
    }

    public static void spawn(int number) {
        for (int i = 0; i < number; i++) {
            int x_pos = (int) ((SimPanel.SCREEN_WIDTH - 200) * Math.random()) + 100;
            int y_pos = (int) ((SimPanel.SCREEN_HEIGHT - 200) * Math.random()) + 100;
            new Gooblet(x_pos, y_pos);
        }
    }

    public static void updateAge() {
        for (Gooblet gooblet : gooblets) {
            gooblet.age ++;
        }
    }

    public void divide() {
        dividing = true;
        new Gooblet(this);
    }

    public int[] closeToBorders() {
        int[] pair = {0, 0};

        // Check X bounds
        if (x <= 0 || x + size >= SimPanel.SCREEN_WIDTH) {
            pair[0] = 1;
        }

        // Check Y bounds
        if (y <= 0 || y + size >= SimPanel.SCREEN_HEIGHT) {
            pair[1] = 1;
        }

        return pair;
    }

    public Lake toWater() {
        if (Lake.lakes.isEmpty()) return null;

        Lake closestLake = null;
        int bestDistance = Integer.MAX_VALUE;

        for (Lake lake : Lake.lakes) {

            int currentDistance = lake.goobletLakeDistance(this);
            if (currentDistance < bestDistance) {
                closestLake = lake;
                bestDistance = currentDistance;
            }

        }

        if ((bestDistance < sight * size) && (thirst < thirstThreshold)) {
            return closestLake;
        }
        return null;
    }

    public Bush toFood() {
        if (Bush.bushes.isEmpty()) return null;

        Bush closestBush = null;
        int bestDistance = Integer.MAX_VALUE;

        for (Bush bush : Bush.bushes) {

            if (bush.numberOfFruits > 0) {
                int currentDistance = bush.goobletBushDistance(this);
                if (currentDistance < bestDistance) {
                    closestBush = bush;
                    bestDistance = currentDistance;
                }
            }
        }

        if ((bestDistance < sight * size) && (hunger < hungerThreshold)) {
            return closestBush;
        }
        return null;
    }

    public int[] newSpeed() {
        double xComp;
        double yComp;
        Bush closeBush = toFood();
        Lake closeLake = toWater();

        if (toWater() != null) {
            int targetX = closeLake.x + Lake.size / 2;
            int targetY = closeLake.y + Lake.size / 2;

            int dx = targetX - (x + size / 2);
            int dy = targetY - (y + size / 2);

            double len = Math.hypot(dx, dy);
            if (len == 0) {
                double r = Math.random();
                xComp = Math.random() > 0.5 ? r : -r;
                yComp = Math.random() > 0.5 ? 1 - r : r - 1;
            } else {
                xComp = dx / len;
                yComp = dy / len;
            }

        } else if (closeBush != null) {
            int targetX = closeBush.x + Bush.size / 2;
            int targetY = closeBush.y + Bush.size / 2;

            int dx = targetX - (x + size / 2);
            int dy = targetY - (y + size / 2);

            double len = Math.hypot(dx, dy);
            if (len == 0) {
                double r = Math.random();
                xComp = Math.random() > 0.5 ? r : -r;
                yComp = Math.random() > 0.5 ? 1 - r : r - 1;
            } else {
                xComp = dx / len;
                yComp = dy / len;
            }

        } else {
            if (Math.random() > 0.85) {
                double r = Math.random();
                xComp = Math.random() > 0.5 ? r : -r;
                yComp = Math.random() > 0.5 ? 1 - r : r - 1;
            } else {
                xComp = 0;
                yComp = 0;
            }
        }

        int xSpeed = (int) (xComp * speed);
        int ySpeed = (int) (yComp * speed);
        return new int[]{xSpeed, ySpeed};
    }


    public void move() {
        // Pick a new direction
        if (Math.random() < 0.008 || steps >= 10 || toWater() != null || toFood() != null) {
            int[] speedPair = newSpeed();
            xSpeed = speedPair[0];
            ySpeed = speedPair[1];
            steps = 0;
        }

        int[] borders = closeToBorders();

        // Invert direction if hitting a border
        if (borders[0] == 1) xSpeed = -xSpeed;
        if (borders[1] == 1) ySpeed = -ySpeed;


        x += xSpeed;
        y += ySpeed;
        steps++;
        if (Math.hypot(xSpeed, ySpeed) > 0) {
            thirst = Math.max(thirst - thirstRate, 0);
            hunger = Math.max(hunger - hungerRate, 0);
        }

        // Hard-clamp inside screen
        x = Math.max(0, Math.min(x, SimPanel.SCREEN_WIDTH - size));
        y = Math.max(0, Math.min(y, SimPanel.SCREEN_HEIGHT - size));
    }


    public void live() {
        move();
        updateAge();
        matingCooldown = Math.max(0, matingCooldown - 1);
        Bush closeBush = toFood();
        Lake closeLake = toWater();

        // Drinking
        if (closeLake != null && closeLake.drinkable(this)) {
            thirst = 1;
        }

        // Eating
        else if (closeBush != null && closeBush.numberOfFruits > 0 && closeBush.edible(this)) {

            if (Math.random() > poisonProbability) {
                hunger = 1;
                closeBush.numberOfFruits--;
            } else {
                gooblets.remove(this);
            }
        }

        // Dividing
        else if (hunger > 0.8 && thirst > 0.8 && age >= matingAge && matingCooldown == 0) {
            divide();
            hunger = 0.2;
            thirst = 0.2;
            matingCooldown = (int) (30 * (1/(SimPanel.DELAY * 0.001)));
        }

        // Kill the gooblet
        if (thirst == 0 || hunger == 0) {
            gooblets.remove(this);
        }
    }
}
