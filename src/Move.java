public class Move
{
    private final Player player;
    private final int fromPoint;
    private final int toPoint;
    private final int dieValue;
    private final boolean bearingOff;
    private static final int BEAR_OFF = -1;

    public Move(Player player, int fromPoint, int toPoint, int dieValue)
    {
        this.player = player;
        this.fromPoint = fromPoint;
        this.toPoint = toPoint;
        this.dieValue = dieValue;
        this.bearingOff = false;
    }

    public Move(Player player, int fromPoint, int dieValue)
    {
        this.player = player;
        this.fromPoint = fromPoint;
        this.toPoint = BEAR_OFF;
        this.dieValue = dieValue;
        this.bearingOff = true;
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

    @Override
    public String toString()
    {
        return player + ": " + fromPoint + " -> " + toPoint +
                " (Die: " + dieValue + ")" +
                (bearingOff ? " Bearing Off" : "");
    }
}
