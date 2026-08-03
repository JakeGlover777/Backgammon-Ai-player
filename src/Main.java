import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        Board board = new Board();

        board.getPoint(1).addChecker(Player.BLACK);

        Move move = new Move(Player.WHITE, 0, 1, 1);
        board.applyMove(move);

        board.printBoard();

        System.out.println("Black bar: " + board.getBlackBarCount());
    }
}
