package main;

import javax.swing.*;
import java.awt.*;

public class SelectPanel extends JPanel {

    int width = 250;
    int height = 200;

    MineSweeper mineSweeper;

    public SelectPanel(MineSweeper mineSweeper) {
        setPreferredSize(new Dimension(width, height));

        this.mineSweeper = mineSweeper;


        Color bg = new Color(220, 220, 220);
        setBackground(bg);
        setForeground(Color.darkGray);

        setLayout(new GridLayout(4, 2, 0, 10));

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font("Arial", Font.PLAIN, 16));
        textPane.setText("Widht:");
        textPane.setBackground(bg);
        add(textPane);

        JSpinner widthInput = new JSpinner(new SpinnerNumberModel(mineSweeper.width, 4, 100, 1));
        widthInput.setFont(new Font("Arial", Font.PLAIN, 16));
        widthInput.setBackground(bg);
        add(widthInput);

        JTextPane textPane2 = new JTextPane();
        textPane2.setEditable(false);
        textPane2.setFont(new Font("Arial", Font.PLAIN, 16));
        textPane2.setText("Height:");
        textPane2.setBackground(bg);
        add(textPane2);

        JSpinner heightInput = new JSpinner(new SpinnerNumberModel(mineSweeper.height, 4, 100, 1));
        heightInput.setFont(new Font("Arial", Font.PLAIN, 16));
        heightInput.setBackground(bg);
        add(heightInput);

        JTextPane textPane3 = new JTextPane();
        textPane3.setEditable(false);
        textPane3.setFont(new Font("Arial", Font.PLAIN, 16));
        textPane3.setBackground(bg);
        textPane3.setText("Number of mines:");
        add(textPane3);

        JSpinner mineInput = new JSpinner(new SpinnerNumberModel(mineSweeper.mines, 1, 100*100-9, 1));
        mineInput.setFont(new Font("Arial", Font.PLAIN, 16));
        mineInput.setBackground(bg);
        add(mineInput);

        add(new Component() {
        });

        JButton startButton = new JButton();
        startButton.setText("Start");
        startButton.setFont(new Font("Arial", Font.PLAIN, 16));
        startButton.setBackground(bg);
        startButton.addActionListener(e -> {
            if((int) heightInput.getValue() * (int) widthInput.getValue() - 9 >= (int) mineInput.getValue()) {
                mineSweeper.mines = (int) mineInput.getValue();
                mineSweeper.height = (int) heightInput.getValue();
                mineSweeper.width = (int) widthInput.getValue();

                mineSweeper.startGame();
            }
        });
        add(startButton);
    }

}
