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
        setUpStartingPosition();
        
    }
    public Point getPoint(int index)
    {
        if(index < 0|| index >= BOARD_SIZE)
        {
            throw new IllegalArgumentException("Point index must be between 0 and 23.");
        }

        return points[index];
    }

    private void placeCheckers(int pointIndex, Player player, int count)
    {
        for(int i = 0; i < count; i++)
        {
            points[pointIndex].addChecker(player);
        }
    }

    private void setUpStartingPosition()
    {
        // White starting position
        placeCheckers(0, Player.WHITE, 2);
        placeCheckers(11, Player.WHITE, 5);
        placeCheckers(16, Player.WHITE, 3);
        placeCheckers(18, Player.WHITE, 5);

        // Black starting position
        placeCheckers(23, Player.BLACK, 2);
        placeCheckers(12, Player.BLACK, 5);
        placeCheckers(7, Player.BLACK, 3);
        placeCheckers(5, Player.BLACK, 5);
    }

    public void printBoard()
    {
        for (int i = 0; i < BOARD_SIZE; i++)
        {
            System.out.println("Point " + i + ": " + points[i]);
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
    }

    public void applyMove(Move move)
    {
        Point source = getPoint(move.getFromPoint());
        Point destination = getPoint(move.getToPoint());

        source.removeChecker();

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

    public int getWhiteBarCount()
    {
        return whiteCheckersOnBar;
    }

    public int getBlackBarCount()
    {
        return blackCheckersOnBar;
    }
}