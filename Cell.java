/*
 * This is used by the solver to hold information gathered about a
 * specific cell in the playing field to help determine if it is a
 * safe move or a mine
 */
public class Cell {
    public int x;
    public int y;
    public int val;
    public int covNeighbors = 0;
    public int nearbyMines = 0;
    public int weight = 0;  // Holds probability weight for risky moves
    public int isMine = -1; // -1 = unsure, 0 = not mine, 1 = is mine

    public void setVal(int value) {
        val = value;
        // Reset weight and isMine state in the process
        weight = 0;
        isMine = -1;
    }
}
