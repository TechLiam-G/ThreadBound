package com.techliam;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Main {
    private static final int MAX_ROWS = 100;
    private static final int MAX_COLUMNS = 100;
    private static final int MAX_CELLS = 10_000;
    private static final int PIXELS_PER_CELL = 12;
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showWindow);
    }

    private static void showWindow() {
        JFrame frame = new JFrame("Maze Maker");
        JTextField dimensionsField = new JTextField("10x10", 10);
        JButton generateButton = new JButton("Generate maze");
        JLabel status = new JLabel("Enter rows x columns (or one number for a square maze).");
        MazePanel mazePanel = new MazePanel();

        JPanel controls = new JPanel();
        controls.add(new JLabel("Dimensions:"));
        controls.add(dimensionsField);
        controls.add(generateButton);

        generateButton.addActionListener(event -> {
            try {
                int[] dimensions = parseDimensions(dimensionsField.getText());
                mazePanel.setMaze(generateMaze(dimensions[0], dimensions[1]));
                status.setText("Generated a " + dimensions[0] + " x " + dimensions[1] + " maze.");
            } catch (IllegalArgumentException exception) {
                status.setText(exception.getMessage());
            }
        });
        dimensionsField.addActionListener(event -> generateButton.doClick());

        frame.setLayout(new BorderLayout(8, 8));
        frame.add(controls, BorderLayout.NORTH);
        frame.add(new JScrollPane(mazePanel), BorderLayout.CENTER);
        frame.add(status, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(420, 320));
        frame.setSize(700, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        generateButton.doClick();
    }

    private static int[] parseDimensions(String input) {
        String normalized = input.trim().toLowerCase().replace('×', 'x');
        String[] parts = normalized.split("[x,]", -1);
        if (parts.length == 1) {
            parts = new String[] { parts[0], parts[0] };
        }
        if (parts.length != 2) {
            throw new IllegalArgumentException("Use a size like 12x20 or 12.");
        }

        try {
            int rows = Integer.parseInt(parts[0].trim());
            int columns = Integer.parseInt(parts[1].trim());
            if (rows < 1 || columns < 1) {
                throw new IllegalArgumentException("Maze dimensions must be positive numbers.");
            }
            if (rows > MAX_ROWS || columns > MAX_COLUMNS || (long) rows * columns > MAX_CELLS) {
                throw new IllegalArgumentException("Dimensions are limited to 100 x 100 cells.");
            }
            return new int[] { rows, columns };
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Use whole-number dimensions, such as 12x20.");
        }
    }

    private static boolean[][] generateMaze(int rows, int columns) {
        int gridRows = rows * 2 + 1;
        int gridColumns = columns * 2 + 1;
        boolean[][] open = new boolean[gridRows][gridColumns];
        boolean[][] visited = new boolean[rows][columns];
        ArrayDeque<int[]> stack = new ArrayDeque<>();

        visited[0][0] = true;
        open[1][1] = true;
        stack.push(new int[] { 0, 0 });

        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            List<int[]> unvisitedNeighbors = new ArrayList<>();
            for (int[] direction : directions) {
                int nextRow = current[0] + direction[0];
                int nextColumn = current[1] + direction[1];
                if (nextRow >= 0 && nextRow < rows
                        && nextColumn >= 0 && nextColumn < columns
                        && !visited[nextRow][nextColumn]) {
                    unvisitedNeighbors.add(new int[] { nextRow, nextColumn });
                }
            }

            if (unvisitedNeighbors.isEmpty()) {
                stack.pop();
                continue;
            }

            Collections.shuffle(unvisitedNeighbors, RANDOM);
            int[] next = unvisitedNeighbors.get(0);
            visited[next[0]][next[1]] = true;
            open[next[0] * 2 + 1][next[1] * 2 + 1] = true;
            open[current[0] + next[0] + 1][current[1] + next[1] + 1] = true;
            stack.push(next);
        }

        open[0][1] = true;
        open[gridRows - 1][gridColumns - 2] = true;
        return open;
    }

    private static final class MazePanel extends JPanel {
        private boolean[][] open = new boolean[0][0];

        private MazePanel() {
            setBackground(Color.BLACK);
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        }

        private void setMaze(boolean[][] maze) {
            open = maze;
            int width = maze[0].length * PIXELS_PER_CELL + 24;
            int height = maze.length * PIXELS_PER_CELL + 24;
            setPreferredSize(new Dimension(width, height));
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, getWidth(), getHeight());
            graphics.setColor(Color.WHITE);
            for (int row = 0; row < open.length; row++) {
                for (int column = 0; column < open[row].length; column++) {
                    if (open[row][column]) {
                        graphics.fillRect(
                                12 + column * PIXELS_PER_CELL,
                                12 + row * PIXELS_PER_CELL,
                                PIXELS_PER_CELL,
                                PIXELS_PER_CELL);
                    }
                }
            }
        }
    }
}
