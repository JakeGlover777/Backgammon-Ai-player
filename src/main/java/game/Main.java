package game;

public class Main
{
    public static void main(String[] args)
    {
        Board board = new Board();

        Move move = new Move(Player.WHITE, 18, 6);

        board.applyMove(move);

        System.out.println(
                "White borne off: "
                        + board.getBorneOffCount(Player.WHITE)
        );

        board.printBoard();
    }
}