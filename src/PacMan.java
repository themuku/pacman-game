import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    private class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;

        char direction = 'R';
        int velocityX = 0;
        int velocityY = 0;

        public Block(Image image, int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.image = image;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char d) {
            char previousDirection = this.direction;
            this.direction = d;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;

            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = previousDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize / 4;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize / 4;
            } else if (this.direction == 'R') {
                this.velocityX = tileSize / 4;
                this.velocityY = 0;
            } else if (this.direction == 'L') {
                this.velocityX = -tileSize / 4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
            this.direction = 'R';
            this.updateVelocity();
        }
    }

    private final int rowCount = 21;
    private final int columnCount = 19;
    private final int tileSize = 32;
    private final int boardWidth = columnCount * tileSize;

    private final Image wallImage;
    private final Image blueGhostImage;
    private final Image orangeGhostImage;
    private final Image redGhostImage;
    private final Image pinkGhostImage;

    private final Image pacmanRightImage;
    private final Image pacmanLeftImage;
    private final Image pacmanUpImage;
    private final Image pacmanDownImage;

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacMan;

    private final String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    PacMan() {
        int boardHeight = rowCount * tileSize;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("./wall.png"))).getImage();
        blueGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("./blueGhost.png"))).getImage();
        orangeGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("./orangeGhost.png"))).getImage();
        pinkGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("./pinkGhost.png"))).getImage();
        redGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("./redGhost.png"))).getImage();

        pacmanDownImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("./pacmanDown.png"))).getImage();
        pacmanLeftImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("./pacmanLeft.png"))).getImage();
        pacmanUpImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("./pacmanUp.png"))).getImage();
        pacmanRightImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("./pacmanRight.png"))).getImage();

        loadMap();

        for (Block ghost : ghosts) {
            char direction = directions[random.nextInt(4)];
            ghost.updateDirection(direction);
        }

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                if (tileMapChar == 'X') {
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                } else if (tileMapChar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                } else if (tileMapChar == 'b') {
                    Block blueGhost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(blueGhost);
                } else if (tileMapChar == 'o') {
                    Block orangeGhost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(orangeGhost);
                } else if (tileMapChar == 'p') {
                    Block pinkGhost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(pinkGhost);
                } else if (tileMapChar == 'r') {
                    Block redGhost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(redGhost);
                } else if (tileMapChar == 'P') {
                    pacMan = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacMan.image, pacMan.x, pacMan.y, pacMan.width, pacMan.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        g.setFont(new Font("Arial", Font.BOLD, 20));
        if (gameOver) {
            g.drawString("Game Over", tileSize / 2, tileSize / 2);
        } else {
            g.drawString("Score: " + score, 10, 20);
            g.drawString("Lives: " + lives, 10, 40);
        }
    }

    public void move() {
        pacMan.x += pacMan.velocityX;
        pacMan.y += pacMan.velocityY;

        if (pacMan.x < 0) {
            pacMan.x = boardWidth - tileSize;
        } else if (pacMan.x >= boardWidth) {
            pacMan.x = 0;
        }

        for (Block wall : walls) {
            if (collision(pacMan, wall)) {
                pacMan.x -= pacMan.velocityX;
                pacMan.y -= pacMan.velocityY;
                break;
            }
        }

        for (Block ghost : ghosts) {
            if (collision(pacMan, ghost)) {
                lives--;

                if (lives == 0) {
                    gameOver = true;
                    return;
                }

                resetPositions();
            }

            if (ghost.y == tileSize * 9 && ghost.x == tileSize * 9) {
                ghost.updateDirection('U');
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x < 0 || ghost.x >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacMan, food)) {
                foodEaten = food;
                score += 10;
                break;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadMap();
//            Can implement a new level here by adding new layout to tileMap
        }
    }

    public void resetPositions() {
        pacMan.reset();
        pacMan.velocityY = 0;
        pacMan.velocityX = 0;

        for (Block ghost : ghosts) {
            char direction = directions[random.nextInt(4)];
            ghost.updateDirection(direction);
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacMan.updateDirection('U');
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacMan.updateDirection('D');
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacMan.updateDirection('L');
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacMan.updateDirection('R');
        }

        if (pacMan.direction == 'U') {
            pacMan.image = pacmanUpImage;
        } else if (pacMan.direction == 'D') {
            pacMan.image = pacmanDownImage;
        } else if (pacMan.direction == 'L') {
            pacMan.image = pacmanLeftImage;
        } else if (pacMan.direction == 'R') {
            pacMan.image = pacmanRightImage;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver) {
            gameLoop.stop();
        }
    }
}
