import java.util.ArrayList;
import java.util.List;

public class MoveGenerator
{
    private static final int BOARD_SIZE = 24;

    public List<Move> generateMoves(Board board, Player player, Dice dice)
    {
        List<Move> moves = new ArrayList<>();

        if (board.getBarCount(player) > 0)
        {
            generateBarEntryMove(
                    moves,
                    board,
                    player,
                    dice.getDieOne()
            );

            if (dice.getDieTwo() != dice.getDieOne())
            {
                generateBarEntryMove(
                        moves,
                        board,
                        player,
                        dice.getDieTwo()
                );
            }

            return moves;
        }

        int direction;

        if (player == Player.WHITE)
        {
            direction = 1;
        }
        else
        {
            direction = -1;
        }

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            Point point = board.getPoint(i);

            if (point.getOwner() == player)
            {
                int destinationOne =
                        i + (dice.getDieOne() * direction);

                int destinationTwo =
                        i + (dice.getDieTwo() * direction);

                if (isOnBoard(destinationOne))
                {
                    Point destination =
                            board.getPoint(destinationOne);

                    if (isLegalDestination(destination, player))
                    {
                        moves.add(
                                new Move(
                                        player,
                                        i,
                                        destinationOne,
                                        dice.getDieOne()
                                )
                        );
                    }
                }

                if (dice.getDieTwo() != dice.getDieOne()
                        && isOnBoard(destinationTwo))
                {
                    Point destination =
                            board.getPoint(destinationTwo);

                    if (isLegalDestination(destination, player))
                    {
                        moves.add(
                                new Move(
                                        player,
                                        i,
                                        destinationTwo,
                                        dice.getDieTwo()
                                )
                        );
                    }
                }
            }
        }

        return moves;
    }

    private void generateBarEntryMove(
            List<Move> moves,
            Board board,
            Player player,
            int dieValue)
    {
        int destination;

        if (player == Player.WHITE)
        {
            destination = dieValue - 1;
        }
        else
        {
            destination = BOARD_SIZE - dieValue;
        }

        Point destinationPoint =
                board.getPoint(destination);

        if (isLegalDestination(destinationPoint, player))
        {
            moves.add(
                    new Move(
                            player,
                            destination,
                            dieValue,
                            true
                    )
            );
        }
    }

    private boolean isLegalDestination(
            Point destination,
            Player player)
    {
        return destination.isEmpty()
                || destination.getOwner() == player
                || (destination.getOwner() != player
                && destination.getCheckerCount() == 1);
    }

    private boolean isOnBoard(int index)
    {
        return index >= 0 && index < BOARD_SIZE;
    }
}