public class Board
{
    private static final int BOARD_SIZE = 24;

    private final Point[] points;

    private int whiteCheckersOnBar;
    private int blackCheckersOnBar;

    public Board()
    {
        points = new Point[BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            points[i] = new Point();
        }

        whiteCheckersOnBar = 0;
        blackCheckersOnBar = 0;

        setUpStartingPosition();
    }

    public Point getPoint(int index)
    {
        if (index < 0 || index >= BOARD_SIZE)
        {
            throw new IllegalArgumentException(
                    "Point index must be between 0 and "
                            + (BOARD_SIZE - 1) + "."
            );
        }

        return points[index];
    }

    public int getBarCount(Player player)
    {
        if (player == Player.WHITE)
        {
            return whiteCheckersOnBar;
        }

        if (player == Player.BLACK)
        {
            return blackCheckersOnBar;
        }

        throw new IllegalArgumentException(
                "Player must be WHITE or BLACK."
        );
    }

    public void removeFromBar(Player player)
    {
        if (player == Player.WHITE)
        {
            if (whiteCheckersOnBar == 0)
            {
                throw new IllegalStateException(
                        "White has no checkers on the bar."
                );
            }

            whiteCheckersOnBar--;
            return;
        }

        if (player == Player.BLACK)
        {
            if (blackCheckersOnBar == 0)
            {
                throw new IllegalStateException(
                        "Black has no checkers on the bar."
                );
            }

            blackCheckersOnBar--;
            return;
        }

        throw new IllegalArgumentException(
                "Player must be WHITE or BLACK."
        );
    }

    public void applyMove(Move move)
    {
        Point destination = getPoint(move.getToPoint());

        if (move.isEnteringFromBar())
        {
            removeFromBar(move.getPlayer());
        }
        else
        {
            Point source = getPoint(move.getFromPoint());
            source.removeChecker();
        }

        if (!destination.isEmpty()
                && destination.getOwner() != move.getPlayer()
                && destination.getCheckerCount() == 1)
        {
            Player hitPlayer = destination.getOwner();

            destination.removeChecker();
            addToBar(hitPlayer);
        }

        destination.addChecker(move.getPlayer());
    }

    public void printBoard()
    {
        for (int i = 0; i < BOARD_SIZE; i++)
        {
            System.out.println(
                    "Point " + i + ": " + points[i]
            );
        }
    }

    private void addToBar(Player player)
    {
        if (player == Player.WHITE)
        {
            whiteCheckersOnBar++;
        }
        else if (player == Player.BLACK)
        {
            blackCheckersOnBar++;
        }
        else
        {
            throw new IllegalArgumentException(
                    "Player must be WHITE or BLACK."
            );
        }
    }

    private void placeCheckers(
            int pointIndex,
            Player player,
            int count)
    {
        for (int i = 0; i < count; i++)
        {
            points[pointIndex].addChecker(player);
        }
    }

    private void setUpStartingPosition()
    {
        placeCheckers(0, Player.WHITE, 2);
        placeCheckers(11, Player.WHITE, 5);
        placeCheckers(16, Player.WHITE, 3);
        placeCheckers(18, Player.WHITE, 5);

        placeCheckers(23, Player.BLACK, 2);
        placeCheckers(12, Player.BLACK, 5);
        placeCheckers(7, Player.BLACK, 3);
        placeCheckers(5, Player.BLACK, 5);
    }
}