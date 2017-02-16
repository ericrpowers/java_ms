/*
 * This will house the strategy/logic needed to solve
 * Minesweeper automatically. The current thought process
 * is the following:
 * 1) Identify all safe moves and mines based on immediate neighbors
 * 2) Identify all safe moves based on known mines
 * 3) Identify all safe moves by using neighbors' info
 * 4) Identify least risky move
 * 5) Blind click if no other options are found
 */
public class Solver {
    /*
     * The size of the board will be 10x10
     * The number of the mines will be 10
     */
    private static int xSize = 10, ySize = 10, mines = 10;
    private int numOfGames = 0, numOfTurns = 0, wins = 0, safeMoves = 0;
    private Board board;
    private int[] savedBoard;
    private Cell[] cl;

    public Solver() {
        int iterations = 100000;
        int[] coordArry;
        long startTime = System.currentTimeMillis();

        do {
            numOfGames++;
            board = new Board(xSize, ySize, mines);
            savedBoard = null;
            do  {
                numOfTurns++;
//                board.showBoard();
                coordArry = selectNextCell();
            } while (!board.isFinalMove(board.getPositionVal(coordArry[0], coordArry[1]) == -1));

            if (board.win())
                wins++;
            /*else {
                board.showBoard();
                throw new RuntimeException("Debug: Lost game");
            }*/
            iterations--;
        } while (iterations != 0);
        float elapsedTime = (System.currentTimeMillis() - startTime) / (float) 1000;

        // Now to print out the statistics
        System.out.println(String.format("%1$20s %2$10s", "Total games", numOfGames));
        System.out.println(String.format("%1$20s %2$10s", "Total wins", wins));
        System.out.println(String.format("%1$20s %2$10s", "Total time taken (s)", elapsedTime));
        System.out.println(String.format("%1$20s %2$10s", "Win Percentage (%)", (wins / (float) numOfGames * 100)));
        System.out.println(String.format("%1$20s %2$10s", "Average # of turns", (numOfTurns / (float) numOfGames)));
    }

    /*
     * First move needs to be handled, and then
     * can move on to safe moves and finally
     * risky moves
     */
    public int[] selectNextCell() {
        int[] arry = null;
        if (savedBoard == null) {
            safeMoves = 0;
            savedBoard = new int[xSize * ySize];
            cl = new Cell[xSize * ySize];
            for (int i = 0; i < (xSize * ySize); i++)
                cl[i] = new Cell();

            // First round just click in the center, and we know we cannot lose
            return new int[] { xSize / 2, ySize / 2 };
        }
        if (safeMoves == 0) {
            savedBoard = board.getBoardValues();
            for (int i = 0; i < (xSize * ySize); i++)
                cl[i].setVal(savedBoard[i]);

            checkNeighbors();
            findSafeMoves();
        }

        // If no safe moves are identified, find least risky move
        if (safeMoves == 0) {
            arry = selectRiskyMove();
//            System.out.println("Debug: Risky move - Column " + (arry[0] + 1) + " Row " + (arry[1] + 1));
        } else {
            arry = selectSafeMove();
            safeMoves--;
        }

        return arry;
    }

    /*
     * To determine safe moves, there is a need
     * to examine the landscape and determine
     * where the known mines are, so that we
     * can exhaust all safe moves in each pass
     */
    public void checkNeighbors() {
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                if (cl[(xSize * y) + x].val == 0) continue;  // empty cell
                if (cl[(xSize * y) + x].val == 9) continue;  // covered cell
                cl[(xSize * y) + x].covNeighbors = 0;
                cl[(xSize * y) + x].nearbyMines = 0;

                // Count the number of covered neighbors
                for (int yy = y - 1; yy <= y + 1; yy++) {
                    if (yy < 0 || yy >= ySize) continue;
                    for (int xx = x - 1; xx <= x + 1; xx++) {
                        if (xx < 0 || xx >= xSize || (yy == y && xx == x)) continue;
                        if (cl[(xSize * yy) + xx].val == 9) {
                            cl[(xSize * y) + x].covNeighbors++;
                            cl[(xSize * yy) + xx].weight += cl[(xSize * y) + x].val;
                        }
                    }
                }

                // If the neighbors equal the value, all neighbors are mines
                if (cl[(xSize * y) + x].val == cl[(xSize * y) + x].covNeighbors) {
                    for (int yy = y - 1; yy <= y + 1; yy++) {
                        if (yy < 0 || yy >= ySize) continue;
                        for (int xx = x - 1; xx <= x + 1; xx++) {
                            if (xx < 0 || xx >= xSize || (yy == y && xx == x)) continue;
                            if (cl[(xSize * yy) + xx].val == 9)
                                cl[(xSize * yy) + xx].isMine = 1;
                        }
                    }
                }

                // Keep count of identified nearby mines
                for (int yy = y - 1; yy <= y + 1; yy++) {
                    if (yy < 0 || yy >= ySize) continue;
                    for (int xx = x - 1; xx <= x + 1; xx++) {
                        if (xx < 0 || xx >= xSize || (yy == y && xx == x)) continue;
                        if (cl[(xSize * yy) + xx].isMine == 1)
                            cl[(xSize * y) + x].nearbyMines++;
                    }
                }

                // See if we can already identify safe moves
                if (cl[(xSize * y) + x].val == cl[(xSize * y) + x].nearbyMines) {
                    for (int yy = y - 1; yy <= y + 1; yy++) {
                        if (yy < 0 || yy >= ySize) continue;
                        for (int xx = x - 1; xx <= x + 1; xx++) {
                            if (xx < 0 || xx >= xSize || (yy == y && xx == x)) continue;
                            if (cl[(xSize * yy) + xx].val == 9 &&
                                    cl[(xSize * yy) + xx].isMine == -1) {
                                cl[(xSize * yy) + xx].isMine = 0;
                                safeMoves++;
                            }
                        }
                    }
                }

                // If more mines than value, we ran into an issue
                if (cl[(xSize * y) + x].val < cl[(xSize * y) + x].nearbyMines) {
                    System.out.println("ERROR - more mines (" + cl[(xSize * y) + x].nearbyMines +
                            ") than value (" + cl[(xSize * y) + x].val + ")! Row " + (y + 1) + " Column " + (x + 1));
                    board.showBoard();
                    throw new RuntimeException();
                }
            }
        }
    }

    /*
     * This method will handle more deductive means of finding safe
     * moves and mines
     */
    public void findSafeMoves() {
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
                if (cl[(xSize * y) + x].val == 0) continue;  // empty cell
                if (cl[(xSize * y) + x].val == 9) continue;  // covered cell

                /*
                 * If we have a scenario where we know val - 1 mines, we should be able to determine
                 * the other mine and safe move based on neighboring hints
                 */
                if (cl[(xSize * y) + x].val - cl[(xSize * y) + x].nearbyMines == 1 &&
                        cl[(xSize * y) + x].covNeighbors - cl[(xSize * y) + x].nearbyMines == 2) {
                    for (int yy = y - 1; yy <= y + 1; yy++) {
                        if (yy < 0 || yy >= ySize) continue;
                        for (int xx = x - 1; xx <= x + 1; xx++) {
                            if (xx < 0 || xx >= xSize || (yy == y && xx == x)) continue;
                            if (cl[(xSize * yy) + xx].val == 9 && cl[(xSize * yy) + xx].isMine == -1) {
                                if (x1 == -1) {
                                    x1 = xx;
                                    y1 = yy;
                                } else {
                                    x2 = xx;
                                    y2 = yy;
                                }
                            }
                        }
                    }

                    if (x1 == -1 || x2 == -1) continue;
                    // Look below and above
                    if (x1 == x2) {
                        if (y - 1 >= 0 && y - 1 < ySize)
                            deduceMine(y - 1, x, y1, x1, y2, x2);
                        if (y + 1 >= 0 && y + 1 < ySize)
                            deduceMine(y + 1, x, y1, x1, y2, x2);
                    }
                    // Look left and right
                    if (y1 == y2) {
                        if (x - 1 >= 0 && x - 1 < xSize)
                            deduceMine(y, x - 1, y1, x1, y2, x2);
                        if (x + 1 >= 0 && x + 1 < xSize)
                            deduceMine(y, x + 1, y1, x1, y2, x2);
                    }
                }
            }
        }
    }

    /*
     * Ideally, we should be able to figure out where more of
     * the mines and safe spots are by looking at the neighbors' hints.
     */
    public void deduceMine(int y0, int x0, int y1, int x1, int y2, int x2) {
        for (int yy = y0 - 1; yy <= y0 + 1; yy++) {
            if (yy < 0 || yy >= ySize) continue;
            for (int xx = x0 - 1; xx <= x0 + 1; xx++) {
                // Ignore entries we have already looked at or already identified mine state
                if (xx < 0 || xx >= xSize || !(cl[(xSize * yy) + xx].val == 9 &&
                        cl[(xSize * yy) + xx].isMine == -1) || (yy == y0 && xx == x0) ||
                        (yy == y1 && xx == x1) || (yy == y2 && xx == x2)) continue;
                if (cl[(xSize * y0) + x0].val - cl[(xSize * y0) + x0].nearbyMines == 1) {
                    // Return if any of the coordinates are 3 away from the cells in question
                    if (yy == y1 + 3 || yy == y1 - 3 || yy == y2 + 3 || yy == y2 - 3 ||
                            xx == x1 + 3 || xx == x1 - 3 || xx == x2 + 3 || xx == x2 - 3) return;
                    cl[(xSize * yy) + xx].isMine = 0;
                    safeMoves++;
                }
                if (cl[(xSize * y0) + x0].covNeighbors - cl[(xSize * y0) + x0].val == 2 &&
                        cl[(xSize * yy) + xx].isMine == -1) {
                    cl[(xSize * yy) + xx].isMine = 1;

                    // Increase the nearby mine count if value is between 1 and 8
                    for (int yyy = yy - 1; yyy <= yy + 1; yyy++) {
                        if (yyy < 0 || yyy >= ySize) continue;
                        for (int xxx = xx - 1; xxx <= xx + 1; xxx++) {
                            if (xxx < 0 || xxx >= xSize || (yyy == yy && xxx == xx)) continue;
                            if (cl[(xSize * yyy) + xxx].val > 0 ||  cl[(xSize * yyy) + xxx].val < 9)
                                cl[(xSize * yyy) + xxx].nearbyMines++;
                        }
                    }
                }
            }
        }
    }

    public int[] selectSafeMove() {
        // All safe moves are identified with isMine = 0
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                if (cl[(xSize * y) + x].val != 9 || cl[(xSize * y) + x].isMine != 0) continue;
                cl[(xSize * y) + x].isMine = -1;
                return new int[] { x, y };
            }
        }

        throw new RuntimeException("ERROR - Could not find a safe move!");
    }

    // Weigh the likelihood of each move and select the lowest chance of failure
    public int[] selectRiskyMove() {
        int prob = 99;
        int[] array = null;     // This will hold the least risky cell
        int[] blindArr = null;  // This will hold the blind click cell if there are no risky cells
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                if (cl[(xSize * y) + x].val != 9 || cl[(xSize * y) + x].isMine != -1) continue;
                int cWeight = cl[(xSize * y) + x].weight;
                if (cWeight > 0 && prob > cWeight) {
                    prob = cWeight;
                    array = new int[] { x, y };
                } else if (cWeight <= 0 ) {
                    blindArr = new int[] { x, y };
                }
            }
        }

        if (array == null && blindArr == null) {
            board.showBoard();
            throw new RuntimeException("ERROR - Could not find a risky move!");
        } else if (array == null) {
            // Fall back on blind clicking as it is the only option
            array = blindArr;
        }
        return array;
    }
}
