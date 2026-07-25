public class Board
{
    private static final int BOARD_SIZE = 24;
    private final Point[] points;

    public Board()
    {
        points = new Point[BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            points[i] = new Point();
        }
    }
    public Point getPoint(int index)
    {
        if(index < 0|| index >= BOARD_SIZE)
        {
            throw new IllegalArgumentException("Point index must be between 0 and 23.");
        }

        return points[index];
    }
}