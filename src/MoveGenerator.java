import java.util.ArrayList;
import java.util.List;

public class MoveGenerator
{
    public List<Move> generateMoves(Board board, Player player, Dice dice)
    {
        List<Move> moves = new ArrayList<>();

        int direction;

        if (player == Player.WHITE)
        {
            direction = 1;
        }
        else
        {
            direction = -1;
        }

        for (int i = 0; i < 24; i++)
        {
            Point point = board.getPoint(i);

            if (point.getOwner() == player)
            {
                int destinationOne = i + (dice.getDieOne() * direction);
                int destinationTwo = i + (dice.getDieTwo() * direction);
            }
        }

        return moves;
    }

    private boolean isOnBoard(int index)
    {
        return index >= 0 && index < 24;
    }
}