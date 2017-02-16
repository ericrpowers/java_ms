import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class TestBoard {
    private Board board;

    @Before
    public void setUp() throws Exception {
        board = new Board(2, 2, 2);
    }

    @After
    public void tearDown() throws Exception {
        board = null;
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // isCellCovered
    @Test
    public void Test_isCellCovered_Covered() {
        assertTrue(board.isCellCovered(0, 0));
    }

    @Test
    public void Test_isCellCovered_Empty() {
        board.isFinalMove(board.getPositionVal(0, 0) == -1);
        assertFalse(board.isCellCovered(0, 0));
    }

    // getPositionVal
    @Test
    public void Test_getPositionVal_NoneNerby() {
        board = new Board(2, 2, 0);
        assertEquals(board.getPositionVal(0, 0), 0);
    }

    @Test
    public void Test_getPositionVal_2NearbyMines() {
        assertEquals(board.getPositionVal(0, 0), 2);
    }

    @Test
    public void Test_getPositionVal_Mine() {
        board = new Board(2, 2, 3);
        board.getPositionVal(0, 0);
        assertEquals(board.getPositionVal(1, 0), -1);
    }

    @Test
    public void Test_getPositionVal_AllMines() {
        board = new Board(2, 2, 4);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Number of mines equals or is greater than size of board!");
        board.getPositionVal(0, 0);
    }

    // isFinalMove
    @Test
    public void Test_isFinalMove_True() {
        assertTrue(board.isFinalMove(board.getPositionVal(0, 0) == -1));
    }

    @Test
    public void Test_isFinalMove_False() {
        board = new Board(4, 4, 8);
        assertFalse(board.isFinalMove(board.getPositionVal(0, 0) == -1));
    }

    // win
    @Test
    public void Test_win_True() {
        board.isFinalMove(board.getPositionVal(0, 0) == -1);
        assertTrue(board.win());
    }

    @Test
    public void Test_win_False() {
        assertFalse(board.win());
    }
}
