package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, MouseListener {

    private final int WIDTH;
    private final int HEIGHT;
    private final int MINES;

    private final int TILESIZE = 32;

    private final Random random = new Random();

    private final Color tileBackgroundColor = new Color(37, 37, 37);
    private final Color flashyColor = new Color(35, 48, 138) ;
    private final Color numberColor = new Color(121, 121, 121);
    private final Color lineColor = new Color(26, 26, 26);
    private final Color counterColor = new Color(217, 215, 215);
    private final Color mineColor = new Color(23, 23, 23);
    private final Color flagColor = new Color(187, 44, 44);


    Thread thread;

    private boolean gameOver = false;
    private boolean gameRunning = false;

    private boolean recast = false;

    private boolean gotInput = false;

    private boolean gameWon = false;

    private int uncoveredCounter = 0;
    private int flaggedCounter = 0;

    private final MineSweeper mineSweeper;

    private static class Tile {
        public boolean covered = true;
        public boolean hasMine = false;
        public int number = 0;
        public boolean flagged = false;
    }

    private final ArrayList<ArrayList<Tile>> game = new ArrayList<>();

    public GamePanel(int width, int height, int mines, MineSweeper mineSweeper) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.MINES = mines;

        this.mineSweeper = mineSweeper;

        for(int i = 0; i < HEIGHT; i++) {
            game.add(new ArrayList<>());
        }

        for(ArrayList<Tile> row : game) {
            for(int i = 0; i < WIDTH; i++) {
                row.add(new Tile());
            }
        }

        setPreferredSize(new Dimension(WIDTH*TILESIZE, height*TILESIZE + TILESIZE));

        setFocusable(true);

        addMouseListener(this);

        this.setBackground(tileBackgroundColor);

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        for(ArrayList<Tile> row : game) {
            for (Tile tile : row) {
                if(!tile.covered) {

                    g.setColor(tileBackgroundColor);
                    g.fillRect(row.indexOf(tile) * TILESIZE, game.indexOf(row) * TILESIZE, TILESIZE, TILESIZE);


                    g.setColor(numberColor);
                    if(tile.number != 0) {
                        drawCenteredString(g, String.valueOf(tile.number), new Rectangle(row.indexOf(tile) * TILESIZE, game.indexOf(row) * TILESIZE, TILESIZE, TILESIZE), new Font("Arial", Font.BOLD, 16));
                    }
                }

            }
        }


        g.setColor(lineColor);

        for(int i = 0; i < WIDTH; i++) {
            g.drawLine(i * TILESIZE, 0, i * TILESIZE, HEIGHT * TILESIZE -1);
        }

        for(int i = 0; i < HEIGHT; i++) {
            g.drawLine(0, i * TILESIZE, WIDTH * TILESIZE, i*TILESIZE);
        }


        for(ArrayList<Tile> row : game) {
            for(Tile tile : row) {
                if(tile.covered) {
                    g.setColor(flashyColor);
                    g.fillRect(row.indexOf(tile) * TILESIZE, game.indexOf(row) * TILESIZE, TILESIZE, TILESIZE);
                }

                if(gameOver && tile.hasMine) {
                    g.setColor(mineColor);
                    g.fillOval(row.indexOf(tile) * TILESIZE, game.indexOf(row) * TILESIZE, TILESIZE, TILESIZE);
                }

                if(tile.flagged) {
                    g.setColor(flagColor);
                    g.fillOval(row.indexOf(tile) * TILESIZE + TILESIZE/4, game.indexOf(row) * TILESIZE + TILESIZE/4, TILESIZE/2, TILESIZE/2);
                }

            }
        }

        g.setColor(lineColor);
        g.drawLine(0, HEIGHT*TILESIZE, WIDTH*TILESIZE, HEIGHT*TILESIZE);

        g.setColor(counterColor);
        g.setFont(new Font("Arial", Font.PLAIN, TILESIZE*2/3));
        g.drawString("Mines: " + (MINES - flaggedCounter), TILESIZE/3, HEIGHT * TILESIZE + TILESIZE*3/4);

        if(gameOver) {
            g.setColor(gameWon ? Color.green : Color.red);
            drawCenteredString(g, gameWon ? "You Won! - press anywhere to continue" : "You lost - press anywhere to continue", new Rectangle(0, 0, WIDTH*TILESIZE, HEIGHT*TILESIZE), new Font("Arial", Font.BOLD, WIDTH*16/10));
        }

    }

    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }

    @Override
    public void run() {
        while(!gameOver) {
            if(gotInput) {
                repaint();
                if(uncoveredCounter == WIDTH*HEIGHT - MINES) {
                    gameWon = true;
                    gameOver = true;
                }
                gotInput = false;
            }
            else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        repaint();
        gotInput = false;

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(!gotInput) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if(!gameOver) {
                    if (gameRunning) {
                        recast = false;
                        uncoverTile((e.getPoint().x - e.getPoint().x % TILESIZE) / TILESIZE, (e.getPoint().y - e.getPoint().y % TILESIZE) / TILESIZE);
                    } else {
                        recast = false;
                        startGame((e.getPoint().x - e.getPoint().x % TILESIZE) / TILESIZE, (e.getPoint().y - e.getPoint().y % TILESIZE) / TILESIZE);
                        gameRunning = true;
                    }
                    gotInput = true;
                } else {
                    mineSweeper.gameEnded();
                }
            }
            if(e.getButton() == MouseEvent.BUTTON3) {
                if(gameRunning) {
                    recast = false;
                    flagTile((e.getPoint().x - e.getPoint().x % TILESIZE)/TILESIZE, (e.getPoint().y - e.getPoint().y % TILESIZE)/TILESIZE);
                    gotInput = true;
                }
                if(gameOver) {
                    mineSweeper.gameEnded();
                }
            }

        }
    }



    private void startGame(int clickedX, int clickedY) {
        generateMines(clickedX, clickedY);
        generateNumbers();
        uncoverTile(clickedX, clickedY);
    }

    private void generateMines(int clickedX, int clickedY) {
        ArrayList<Double> tiles = new ArrayList<>();

        for(double i = 0; i < WIDTH * HEIGHT; i++) {
            tiles.add(i);
        }


        for(int i = -1; i < 2; i++) {
            if(clickedY + i >= 0 && clickedY + i < HEIGHT) {
                for (int j = -1; j < 2; j++) {
                    if(clickedX + j >=0 && clickedX + j < WIDTH) {
                        tiles.remove((double) clickedX + j + ((clickedY + i) * WIDTH));
                    }
                }
            }
        }


        int selectedTile;
        for(int i = 0; i < MINES; i++) {
            selectedTile = random.nextInt(0, tiles.size());
            game.get((int) ((tiles.get(selectedTile) - tiles.get(selectedTile) % WIDTH)/WIDTH)).get((int) (tiles.get(selectedTile) % WIDTH)).hasMine = true;
            tiles.remove(selectedTile);
        }

    }

    private void generateNumbers() {
        for(ArrayList<Tile> row : game) {
            for(Tile tile : row) {
                if(tile.hasMine) {

                    int tileX = 0;
                    int tileY = 0;

                    for(ArrayList<Tile> rowB : game) {
                        if(rowB.contains(tile)) {
                            tileX = rowB.indexOf(tile);
                            tileY = game.indexOf(rowB);
                        }
                    }

                    for(int i = -1; i < 2; i++) {
                        if(tileY + i >= 0 && tileY + i < HEIGHT) {
                            for(int j = -1; j < 2; j++) {
                                if(!(i == j && i == 0)) {
                                    if (tileX + j >= 0 && tileX + j < WIDTH) {
                                        game.get(tileY + i).get(tileX + j).number++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private void uncoverTile(int originalX, int originalY) {
        Tile tile;
        try {
            tile = game.get(originalY).get(originalX);
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        if(!tile.flagged) {
            if (!tile.hasMine && tile.covered) {
                tile.covered = false;
                uncoveredCounter++;
                if (tile.number == 0) {
                    checkSourroundingTiles(originalX, originalY);
                }
                return;
            }

            if (tile.hasMine) {
                gameOver = true;
                return;
            }
        }
        int flagCounter = 0;
        int coveredCounter = 0;
        for(int i = -1; i < 2; i++) {
            if(originalY + i >= 0 && originalY + i < HEIGHT) {
                for(int j = -1; j < 2; j++) {
                    if(!(i == j && i == 0)) {
                        if(originalX + j >= 0 && originalX + j < WIDTH) {
                            if(game.get(originalY + i).get(originalX + j).flagged)
                                flagCounter++;
                            else if(game.get(originalY + i).get(originalX + j).covered)
                                coveredCounter++;
                        }
                    }
                }
            }
        }
        if(!tile.covered && coveredCounter > 0 && flagCounter == tile.number && !recast) {
            checkSourroundingTiles(originalX, originalY);
        }
    }

    private void checkSourroundingTiles(int originalX, int originalY) {
        for(int i = -1; i < 2; i++) {
            if(originalY + i >= 0 && originalY + i < HEIGHT) {
                for(int j = -1; j < 2; j++) {
                    if(!(i == j && i == 0)) {
                        if(originalX + j >= 0 && originalX + j < WIDTH) {
                            recast = true;
                            uncoverTile(originalX + j, originalY + i);
                        }
                    }
                }
            }
        }
    }

    private void flagTile(int x, int y) {
        Tile tile;
        try {
            tile = game.get(y).get(x);
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        if(tile.covered) {
            tile.flagged = !tile.flagged;

            if (tile.flagged) {
                flaggedCounter++;
            } else {
                flaggedCounter--;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}