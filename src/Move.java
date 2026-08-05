public class Move
{
    private static final int SPECIAL_POINT = -1;

    private final Player player;
    private final int fromPoint;
    private final int toPoint;
    private final int dieValue;
    private final boolean bearingOff;
    private final boolean enteringFromBar;

    public Move(Player player, int fromPoint, int toPoint, int dieValue)
    {
        this.player = player;
        this.fromPoint = fromPoint;
        this.toPoint = toPoint;
        this.dieValue = dieValue;
        this.bearingOff = false;
        this.enteringFromBar = false;
    }

    public Move(Player player, int fromPoint, int dieValue)
    {
        this.player = player;
        this.fromPoint = fromPoint;
        this.toPoint = SPECIAL_POINT;
        this.dieValue = dieValue;
        this.bearingOff = true;
        this.enteringFromBar = false;
    }

    public Move(Player player, int toPoint, int dieValue, boolean enteringFromBar)
    {
        if (!enteringFromBar)
        {
            throw new IllegalArgumentException(
                    "This is only for bar-entry moves."
            );
        }

        this.player = player;
        this.fromPoint = SPECIAL_POINT;
        this.toPoint = toPoint;
        this.dieValue = dieValue;
        this.bearingOff = false;
        this.enteringFromBar = true;
    }

    public Player getPlayer()
    {
        return player;
    }

    public int getFromPoint()
    {
        return fromPoint;
    }

    public int getToPoint()
    {
        return toPoint;
    }

    public int getDieValue()
    {
        return dieValue;
    }

    public boolean isBearingOff()
    {
        return bearingOff;
    }

    public boolean isEnteringFromBar()
    {
        return enteringFromBar;
    }

    @Override
    public String toString()
    {
        if (enteringFromBar)
        {
            return player + ": BAR -> " + toPoint
                    + " (Die: " + dieValue + ")";
        }

        if (bearingOff)
        {
            return player + ": " + fromPoint
                    + " -> BEAR OFF (Die: " + dieValue + ")";
        }

        return player + ": " + fromPoint + " -> " + toPoint
                + " (Die: " + dieValue + ")";
    }
}