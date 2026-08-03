import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        Board board = new Board();

        Move move = new Move(Player.WHITE, 0, 1, 1);

        board.applyMove(move);

        board.printBoard();
    }
}
