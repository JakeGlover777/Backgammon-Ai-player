package game;

public class Board
{
    private static final int BOARD_SIZE = 24;

    private final Point[] points;

    private int whiteCheckersBorneOff;
    private int blackCheckersBorneOff;
    private int whiteCheckersOnBar;
    private int blackCheckersOnBar;

    public Board()
    {
        this(true);
    }

    public Board(boolean useStartingPosition)
    {
        points = new Point[BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            points[i] = new Point();
        }

        whiteCheckersOnBar = 0;
        blackCheckersOnBar = 0;

        whiteCheckersBorneOff = 0;
        blackCheckersBorneOff = 0;

        if (useStartingPosition)
        {
            setUpStartingPosition();
        }
    }

    public Point getPoint(int index)
    {
        if (index < 0 || index >= BOARD_SIZE)
        {
            throw new IllegalArgumentException(
                    "Main.Point index must be between 0 and "
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
                "Main.Player must be WHITE or BLACK."
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
                "Main.Player must be WHITE or BLACK."
        );
    }

    public int getBorneOffCount(Player player)
    {
        if (player == Player.WHITE)
        {
            return whiteCheckersBorneOff;
        }

        if (player == Player.BLACK)
        {
            return blackCheckersBorneOff;
        }

        throw new IllegalArgumentException(
                "Main.Player must be WHITE or BLACK."
        );
    }

    public boolean allCheckersInHomeBoard(Player player)
    {
        if (getBarCount(player) > 0)
        {
            return false;
        }

        if (player == Player.WHITE)
        {
            for (int i = 0; i < 18; i++)
            {
                if (points[i].getOwner() == Player.WHITE)
                {
                    return false;
                }
            }

            return true;
        }

        if (player == Player.BLACK)
        {
            for (int i = 6; i < BOARD_SIZE; i++)
            {
                if (points[i].getOwner() == Player.BLACK)
                {
                    return false;
                }
            }

            return true;
        }

        throw new IllegalArgumentException(
                "Main.Player must be WHITE or BLACK."
        );
    }

    public void applyMove(Move move)
    {
        if (move.isBearingOff())
        {
            Point source = getPoint(move.getFromPoint());

            source.removeChecker();
            addBorneOffChecker(move.getPlayer());

            return;
        }

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
                    "Main.Point " + i + ": " + points[i]
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
                    "Main.Player must be WHITE or BLACK."
            );
        }
    }

    private void addBorneOffChecker(Player player)
    {
        if (player == Player.WHITE)
        {
            whiteCheckersBorneOff++;
        }
        else if (player == Player.BLACK)
        {
            blackCheckersBorneOff++;
        }
        else
        {
            throw new IllegalArgumentException(
                    "Main.Player must be WHITE or BLACK."
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

    public Board(Board other)
    {
        points = new Point[BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            points[i] = new Point(other.points[i]);
        }

        this.whiteCheckersOnBar = other.whiteCheckersOnBar;
        this.blackCheckersOnBar = other.blackCheckersOnBar;
        this.whiteCheckersBorneOff = other.whiteCheckersBorneOff;
        this.blackCheckersBorneOff = other.blackCheckersBorneOff;
    }
}