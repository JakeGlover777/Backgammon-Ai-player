import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        Board board = new Board();

        Dice dice = new Dice();
        dice.roll();

        MoveGenerator generator = new MoveGenerator();

        List<Move> moves = generator.generateMoves(board, Player.WHITE, dice);

        System.out.println(dice);

        for (Move move : moves)
        {
            System.out.println(move);
        }
    }
}
