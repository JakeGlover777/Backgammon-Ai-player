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

                if (isOnBoard(destinationOne))
                {
                    Point destination = board.getPoint(destinationOne);

                    if (destination.isEmpty()
                            || destination.getOwner() == player
                            || (destination.getOwner() != player
                            && destination.getCheckerCount() == 1))
                    {
                        moves.add(new Move(
                                player,
                                i,
                                destinationOne,
                                dice.getDieOne()
                        ));
                    }
                }

                if (dice.getDieTwo() != dice.getDieOne())
                {
                    if (isOnBoard(destinationTwo))
                    {
                        Point destination = board.getPoint(destinationTwo);

                        if (destination.isEmpty()
                                || destination.getOwner() == player
                                || (destination.getOwner() != player
                                && destination.getCheckerCount() == 1))
                        {
                            moves.add(new Move(
                                    player,
                                    i,
                                    destinationTwo,
                                    dice.getDieTwo()
                            ));
                        }
                    }
                }
            }
        }

        return moves;
    }

    private boolean isOnBoard(int index)
    {
        return index >= 0 && index < 24;
    }
}