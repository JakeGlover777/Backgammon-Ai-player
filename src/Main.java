public class Main
{
    public static void main(String[] args)
    {
        Board board = new Board();

        board.getPoint(1).addChecker(Player.BLACK);

        Move hitMove = new Move(
                Player.WHITE,
                0,
                1,
                1
        );

        board.applyMove(hitMove);

        System.out.println(
                "Black bar before entry: "
                        + board.getBarCount(Player.BLACK)
        );

        Move entryMove = new Move(
                Player.BLACK,
                22,
                2,
                true
        );

        board.applyMove(entryMove);

        System.out.println(
                "Black bar after entry: "
                        + board.getBarCount(Player.BLACK)
        );

        board.printBoard();
    }
}