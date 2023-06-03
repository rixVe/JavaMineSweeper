package main;

import javax.swing.*;
import java.awt.*;

public class MineSweeper extends JFrame {

    public int width = 20;
    public int height = 20;
    public int mines = 50;

    private GamePanel gamePanel;
    private SelectPanel selectPanel;


    public MineSweeper() {

        setResizable(false);
        setTitle("Minesweeper");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        selectPanel = new SelectPanel(this);

        add(selectPanel);

        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void gameEnded() {
        remove(gamePanel);
        selectPanel = new SelectPanel(this);
        add(selectPanel);
        pack();
        setLocationRelativeTo(null);
    }

    public void startGame() {
        remove(selectPanel);
        gamePanel = new GamePanel(width, height, mines, this);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new MineSweeper();
    }

}
