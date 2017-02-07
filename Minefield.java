import java.util.Random;

public class Minefield {
    private static int MINE_VAL = -1; // The value given to represent a mine
    private int[] minefield;

    public Minefield(int colLength, int rowLength, int mineCount, int Row, int Column) {
        minefield = new int[colLength * rowLength];
        for (int i = 0; i < minefield.length; i++)
            minefield[i] = 0;

        placeMines(colLength, rowLength, mineCount, Row, Column);
        fillHints(colLength, rowLength);
    }

    public int getMineVal() {
        return MINE_VAL;
    }

    public int getCellVal(int pos) {
        return minefield[pos];
    }

    // Avoid entries with mines and the first selected position
    private void placeMines(int colLength, int rowLength, int mineCount, int Row, int Column) {
        int row, column;
        Random random = new Random();
        for (int i = 0; i < mineCount; i++) {
            do {
                row = random.nextInt(rowLength);
                column = random.nextInt(colLength);
            } while (minefield[(colLength * row) + column] == MINE_VAL || (row == Row && column == Column));

            minefield[(colLength * row) + column] = MINE_VAL;
        }
    }

    /*
     * To fill in the hints, need to look at every empty cell and count the
     * number of adjacent mines into that cell. Make sure to avoid going outside
     * the boundaries of the playing field.
     */
    private void fillHints(int colLength, int rowLength) {
        for (int row = 0; row < rowLength; row++)
            for (int column = 0; column < colLength; column++) {
                    for (int i = -1; i <= 1; i++)
                        for (int j = -1; j <= 1; j++)
                            if ((row + i) >= 0 && (row + i) < rowLength && (column + j) >= 0 && (column + j) < colLength) 
                                if (minefield[(colLength * row) + column] != MINE_VAL)
                                    if (minefield[(colLength * (row + i)) + (column + j)] == MINE_VAL)
                                        minefield[(colLength * row) + column]++;
            }
    }
}
