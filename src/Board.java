public class Board
{
    private final Point[] points;

    public Board()
    {
        points = new Point[24];

        for (int i = 0; i < 24; i++)
        {
            points[i] = new Point();
        }
    }
}