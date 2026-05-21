/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package music.editor.grid;

/**
 *
 * @author desktop
 */
public class NoteGrid {

    private final int rows = 7;
    private int beats;
    private NoteCell[][] grid;

    public NoteGrid(int beats) {
        this.beats = beats;
        grid = new NoteCell[rows][beats];

        for (int r = 0; r < rows; r++) {
            for (int b = 0; b < beats; b++) {
                grid[r][b] = new NoteCell();
            }
        }
    }
    
    public boolean isActive(int row, int beat) {
        return grid[row][beat].isActive();
    }

    public void setActive(int row, int beat, boolean value) {
        grid[row][beat].setActive(value);
    }
    
    public boolean isContinuation(int row, int beat) {
        return grid[row][beat].isContinuation();
    }

    public void setContinuation(int row, int beat, boolean value) {
        grid[row][beat].setContinuation(value);
    }
    
    public void toggleNote(int row, int beat) {

        if (grid[row][beat].isActive()) {
            clearNote(row, beat);
        } else {
            grid[row][beat].setActive(true);
            grid[row][beat].setContinuation(false);
        }
    }
    
    public void clearNote(int row, int beat) {

        int start = beat;
        int end = beat;

        while (start > 0 && grid[row][start].isContinuation()) {
            start--;
        }

        while (end + 1 < beats &&
               grid[row][end + 1].isActive() &&
               grid[row][end + 1].isContinuation()) {
            end++;
        }

        for (int b = start; b <= end; b++) {
            grid[row][b].setActive(false);
            grid[row][b].setContinuation(false);
        }
    }
    
    public boolean[][] getActiveData() {
        boolean[][] data = new boolean[rows][beats];

        for (int r = 0; r < rows; r++) {
            for (int b = 0; b < beats; b++) {
                data[r][b] = grid[r][b].isActive();
            }
        }
        return data;
    }
    
    public void resize(int newBeats) {

        this.beats = newBeats;

        NoteCell[][] newGrid = new NoteCell[rows][newBeats];

        for (int r = 0; r < rows; r++) {
            for (int b = 0; b < newBeats; b++) {

                // keep old data if exists
                if (grid != null && b < this.beats && r < rows) {
                    newGrid[r][b] = grid[r][b];
                } else {
                    newGrid[r][b] = new NoteCell();
                }
            }
        }

        grid = newGrid;
    }
    
    public void load(boolean[][] active, boolean[][] continuation) {

        int maxRows = Math.min(rows, active.length);

        for (int r = 0; r < maxRows; r++) {

            int maxBeats = Math.min(beats, active[r].length);

            for (int b = 0; b < maxBeats; b++) {

                grid[r][b].setActive(active[r][b]);
                grid[r][b].setContinuation(continuation[r][b]);
            }
        }
    }
    
    public void clearRow(int row) {
        for (int b = 0; b < beats; b++) {
            grid[row][b].setActive(false);
            grid[row][b].setContinuation(false);
        }
    }
}