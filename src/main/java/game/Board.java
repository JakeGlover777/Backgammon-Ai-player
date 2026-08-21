package game;

public class Board
{
    private static final int BOARD_SIZE = 24;
    private static final int WHITE_HOME_START = 18;
    private static final int BLACK_HOME_END = 6;

    private static final int WHITE_START_POINT_ONE = 0;
    private static final int WHITE_START_COUNT_ONE = 2;
    private static final int WHITE_START_POINT_TWO = 11;
    private static final int WHITE_START_COUNT_TWO = 5;
    private static final int WHITE_START_POINT_THREE = 16;
    private static final int WHITE_START_COUNT_THREE = 3;
    private static final int WHITE_START_POINT_FOUR = 18;
    private static final int WHITE_START_COUNT_FOUR = 5;

    private static final int BLACK_START_POINT_ONE = 23;
    private static final int BLACK_START_COUNT_ONE = 2;
    private static final int BLACK_START_POINT_TWO = 12;
    private static final int BLACK_START_COUNT_TWO = 5;
    private static final int BLACK_START_POINT_THREE = 7;
    private static final int BLACK_START_COUNT_THREE = 3;
    private static final int BLACK_START_POINT_FOUR = 5;
    private static final int BLACK_START_COUNT_FOUR = 5;

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

    public Board(Board other)
    {
        points = new Point[BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            points[i] = new Point(other.points[i]);
        }

        whiteCheckersBorneOff = other.whiteCheckersBorneOff;
        blackCheckersBorneOff = other.blackCheckersBorneOff;
        whiteCheckersOnBar = other.whiteCheckersOnBar;
        blackCheckersOnBar = other.blackCheckersOnBar;
    }

    public Point getPoint(int index)
    {
        if (index < 0 || index >= BOARD_SIZE)
        {
            throw new IllegalArgumentException("Point index must be between 0 and "
                    + (BOARD_SIZE - 1) + ".");
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

        throw new IllegalArgumentException("Player must be WHITE or BLACK.");
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

        throw new IllegalArgumentException("Player must be WHITE or BLACK.");
    }

    public boolean allCheckersInHomeBoard(Player player)
    {
        if (getBarCount(player) > 0)
        {
            return false;
        }

        if (player == Player.WHITE)
        {
            for (int i = 0; i < WHITE_HOME_START; i++)
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
            for (int i = BLACK_HOME_END; i < BOARD_SIZE; i++)
            {
                if (points[i].getOwner() == Player.BLACK)
                {
                    return false;
                }
            }

            return true;
        }

        throw new IllegalArgumentException("Player must be WHITE or BLACK.");
    }

    public void removeFromBar(Player player)
    {
        if (player == Player.WHITE)
        {
            if (whiteCheckersOnBar == 0)
            {
                throw new IllegalStateException("White has no checkers on the bar.");
            }

            whiteCheckersOnBar--;
            return;
        }

        if (player == Player.BLACK)
        {
            if (blackCheckersOnBar == 0)
            {
                throw new IllegalStateException("Black has no checkers on the bar.");
            }

            blackCheckersOnBar--;
            return;
        }

        throw new IllegalArgumentException("Player must be WHITE or BLACK.");
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
            throw new IllegalArgumentException("Player must be WHITE or BLACK.");
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
            throw new IllegalArgumentException("Player must be WHITE or BLACK.");
        }
    }

    private void placeCheckers(int pointIndex, Player player, int count)
    {
        for (int i = 0; i < count; i++)
        {
            points[pointIndex].addChecker(player);
        }
    }

    private void setUpStartingPosition()
    {
        placeCheckers(WHITE_START_POINT_ONE, Player.WHITE, WHITE_START_COUNT_ONE);
        placeCheckers(WHITE_START_POINT_TWO, Player.WHITE, WHITE_START_COUNT_TWO);
        placeCheckers(WHITE_START_POINT_THREE, Player.WHITE, WHITE_START_COUNT_THREE);
        placeCheckers(WHITE_START_POINT_FOUR, Player.WHITE, WHITE_START_COUNT_FOUR);

        placeCheckers(BLACK_START_POINT_ONE, Player.BLACK, BLACK_START_COUNT_ONE);
        placeCheckers(BLACK_START_POINT_TWO, Player.BLACK, BLACK_START_COUNT_TWO);
        placeCheckers(BLACK_START_POINT_THREE, Player.BLACK, BLACK_START_COUNT_THREE);
        placeCheckers(BLACK_START_POINT_FOUR, Player.BLACK, BLACK_START_COUNT_FOUR);
    }
}
