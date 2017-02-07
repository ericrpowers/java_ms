import java.util.Scanner;

public class Game {
    private Board board;
    int turn;

    public Game() {
        Scanner input = new Scanner(System.in);
        String answer;

        // Check if user or the solver will play
        do {
            System.out.print("Will the user or the bot play?(u/b) ");
            answer = input.next().replaceAll("\\s+", "");
        } while (!(answer.equalsIgnoreCase("u") || answer.equalsIgnoreCase("b")));

        if (answer.equalsIgnoreCase("u"))
            User(input);
        else
            new Solver();
    }

    public void User(Scanner input) {
        // Initialize size and mines to use in loop
        int xSize = 0, ySize = 0, mines = 0, tmp;
        boolean fPass;
        String answer;

        for (int i = 0; i <= 2; i++) {
            tmp = -1;
            fPass = true;
            do {
                // Do not bother repeating these lines after first time
                if (fPass) {
                    if (i == 0)
                        System.out.print("Size of X-axis (5 - 20): ");
                    else if (i == 1)
                        System.out.print("Size of Y-axis (5 - 20): ");
                    else
                        // Let's avoid too many mines and restrict it to max ~1/3rd of entire board
                        System.out.print("Number of mines (5 - " + (xSize * ySize) / 3 + "): ");
                    fPass = false;
                }

                // Look for an integer within the user input else ignore
                if (input.hasNextInt())
                    tmp = Integer.parseInt(input.next());
                else
                    input.next();

                if (tmp < 5 || (i <= 1 ? tmp > 20 : tmp > (xSize * ySize) / 3))
                    System.out.print("Choose a number between 5 and " + (i <= 1 ? 20 : (xSize * ySize) / 5) + ": ");
            } while (tmp < 5 || (i <= 1 ? tmp > 20 : tmp > (xSize * ySize) / 3));

            switch (i) {
                case 0: xSize = tmp;
                case 1: ySize = tmp;
                case 2: mines = tmp;
            }
        }

        do {
            board = new Board(xSize, ySize, mines);
            turn = 0;
            Play();

            // See if user wants to play again
            do {
                System.out.print("Want to play again?(y/n) ");
                answer = input.next().replaceAll("\\s+", "");
            } while (!(answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("n")));
        } while (answer.equalsIgnoreCase("y"));

        // Close to avoid warning/resource leak
        input.close();
    }

    public void Play() {
        do  {
            turn++;
            System.out.println("\nTurn " + turn);
            board.showBoard();
        } while (!board.isFinalMove(board.setPosition()));

        if (board.win())
            System.out.println("Congrats, you found all the mines in " + turn + " turns.");
        else
            System.out.println("You hit a mine! Try again.");

        board.showMines();
    }
}
