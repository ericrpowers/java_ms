import java.util.Scanner;

public class Board {
    private static char COVER_SYMB = 'X';
    private static char EMPTY_SYMB = '.';
    private static char MINE_SYMB = '*';
    private int colLen;                 // column length
    private int rowLen;                 // row length
    private int mCount;                 // mine count for other functions to reference
    private int Row, Column;            // Coordinates of the selected cell
    private int[] boardgame;
    private Minefield minefield;
    Scanner input = new Scanner(System.in);

    public Board (int xSize, int ySize, int mineCount) {
        colLen = xSize;
        rowLen = ySize;
        mCount = mineCount;
        // Only setup the board to avoid losing on the first turn
        startBoard();
    }

    private void startBoard() {
        boardgame = new int[colLen * rowLen];
        for (int i = 0; i < boardgame.length; i++)
            boardgame[i] = 9;
    }

    public int[] getBoardValues() {
        return boardgame;
    }

    public boolean isCellCovered(int column, int row) {
        return boardgame[(colLen * row) + column] == 9;
    }

    public void showBoard() {
        // TODO: Cleanup layout to make things prettier
        System.out.println(String.format("%1$10s", "Rows"));
        for (int row = rowLen; row > 0; row--) {
            System.out.print(String.format("%1$9s", row + " "));

            for (int column = 1; column <= colLen; column++) {
                String str = "   ";
                int val = boardgame[(colLen * (row - 1)) + (column - 1)];
                switch (val) {
                    case -1:    str += MINE_SYMB;
                                break;
                    case 0:     str += EMPTY_SYMB;
                                break;
                    case 9:     str += COVER_SYMB;
                                break;
                    default:    str += val;
                                break;
                }
                System.out.print(str);
            }
            System.out.println();
        }

        String cLine = "\n            1";
        for (int i = 2; i <= colLen; i++)
            cLine += (i < 10) ? "   " + i : "  " + i;
        System.out.println(cLine);

        System.out.println(String.format("%1$" + (cLine.length() / 2 + 7) + "s", "Columns"));
    }

    public boolean setPosition() {
        do {
            int itr = 0;
            Row = -1;
            Column = -1;
            // Do not leave this region until two valid inputs are given
            do {
                if (itr == 0)
                    System.out.print("Row: ");
                else
                    System.out.print("Column: ");

                if (input.hasNextInt()) {
                    if (itr == 0)
                        Row = Integer.parseInt(input.next()) - 1;
                    else
                        Column = Integer.parseInt(input.next()) - 1;
                } else {
                    input.next();
                }

                if (itr == 0 ? (Row < 0 || Row > rowLen - 1) : (Column < 0 || Column > colLen - 1))
                    System.out.println("Choose a number between 1 and " + (itr == 0 ? rowLen : colLen));
                else
                    itr++;
            } while (itr == 0 ? (Row < 0 || Row > rowLen - 1) : (Column < 0 || Column > colLen - 1));

            if (!isCellCovered(Column, Row))
                System.out.println("Field already shown");
        } while (!isCellCovered(Column, Row));

        // Return true if a mine is hit
        return getPositionVal(Column, Row) == minefield.getMineVal();
    }

    public int getPositionVal(int column, int row) {
        // Setup the minefield if it does not exist
        Column = column;
        Row = row;
        if (minefield == null)
            minefield = new Minefield(colLen, rowLen, mCount, row, column);

        return minefield.getCellVal((colLen * row) + column);
    }

    public boolean isFinalMove(boolean isMine) {
        // If the user did not hit a mine, uncover the empty region
        if (!isMine) {
            openNeighbors();
            isMine = win();
        }

        return isMine;
    }

    /*
     * When revealing the playing field, need to take into account the boundaries
     * of the playing field and if the position is an empty cell (aka 0). If it is
     * an empty cell, recursively continue to expand range until the entire empty
     * region is exposed.
     */
    private void openNeighbors() {
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++) {
                if ((Row + i) < 0 || (Row + i) >= rowLen || (Column + j) < 0 || (Column + j) >= colLen)
                    continue;
                if (minefield.getCellVal((colLen * (Row + i)) + (Column + j)) == minefield.getMineVal())
                    continue;
                int val = minefield.getCellVal((colLen * (Row + i)) + (Column + j));
                if (!isCellCovered(Column + j, Row + i))
                    continue;

                boardgame[(colLen * (Row + i)) + (Column + j)] = val;
                if (val == 0 && !((Row + i) == Row && (Column + j) == Column)) {
                    Row += i;
                    Column += j;
                    openNeighbors();
                    Column -= j;
                    Row -= i;
                }
            }
    }

    /*
     * Once the number of covered cells equal the number of mines,
     * the game is over.
     */
    public boolean win() {
        int count = 0;
        for (int row = 0; row < rowLen; row++)
            for (int column = 0; column < colLen; column++)
                if (isCellCovered(column, row))
                    count++;

        return count == mCount;                
    }

    public void showMines() {
        for (int i = 0; i < rowLen; i++)
            for (int j = 0; j < colLen; j++)
                if (minefield.getCellVal((colLen * i) + j) == minefield.getMineVal())
                    boardgame[(colLen * i) + j] = -1;

        showBoard();
    }
}
