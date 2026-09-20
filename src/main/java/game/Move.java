package game;

/**
 * Represents a single checker move in Backgammon.
 *
 * <p>A move may represent movement between two board points, bearing a checker
 * off the board, or entering a checker from the bar. The move also records the
 * die value used.
 */

public class Move
{
    private static final int BAR = -1;
    private static final int BEAR_OFF = -1;

    private final Player player;
    private final int fromPoint;
    private final int toPoint;
    private final int dieValue;
    private final boolean bearingOff;
    private final boolean enteringFromBar;

    /**
     * Creates a move between two points on the board.
     *
     * @param player the player making the move
     * @param fromPoint the point the checker moves from
     * @param toPoint the point the checker moves to
     * @param dieValue the die value used for the move
     */

    public Move(Player player, int fromPoint, int toPoint, int dieValue)
    {
        this.player = player;
        this.fromPoint = fromPoint;
        this.toPoint = toPoint;
        this.dieValue = dieValue;
        this.bearingOff = false;
        this.enteringFromBar = false;
    }

    /**
     * Creates a move that bears a checker off the board.
     *
     * @param player the player making the move
     * @param fromPoint the point the checker is borne off from
     * @param dieValue the die value used for the move
     */

    public Move(Player player, int fromPoint, int dieValue)
    {
        this.player = player;
        this.fromPoint = fromPoint;
        this.toPoint = BEAR_OFF;
        this.dieValue = dieValue;
        this.bearingOff = true;
        this.enteringFromBar = false;
    }

    /**
     * Creates a move that enters a checker from the bar.
     *
     * @param player the player making the move
     * @param toPoint the point the checker enters onto
     * @param dieValue the die value used for the move
     * @param enteringFromBar must be true to indicate a bar-entry move
     * @throws IllegalArgumentException if enteringFromBar is false
     */

    public Move(Player player, int toPoint, int dieValue, boolean enteringFromBar)
    {
        if (!enteringFromBar)
        {
            throw new IllegalArgumentException("This constructor is only for bar-entry moves.");
        }

        this.player = player;
        this.fromPoint = BAR;
        this.toPoint = toPoint;
        this.dieValue = dieValue;
        this.bearingOff = false;
        this.enteringFromBar = true;
    }

    /**
     * Returns the player making this move.
     *
     * @return the player making the move
     */

    public Player getPlayer()
    {
        return player;
    }

    /**
     * Returns the point the checker moves from.
     *
     * @return the source point
     */

    public int getFromPoint()
    {
        return fromPoint;
    }

    /**
     * Returns the point the checker moves to.
     *
     * @return the destination point
     */

    public int getToPoint()
    {
        return toPoint;
    }

    /**
     * Returns the die value used for this move.
     *
     * @return the die value
     */

    public int getDieValue()
    {
        return dieValue;
    }

    /**
     * Determines whether this move bears a checker off the board.
     *
     * @return true if the move is a bearing-off move, otherwise false
     */

    public boolean isBearingOff()
    {
        return bearingOff;
    }

    /**
     * Determines whether this move enters a checker from the bar.
     *
     * @return true if the move is a bar-entry move, otherwise false
     */

    public boolean isEnteringFromBar()
    {
        return enteringFromBar;
    }

    /**
     * Returns a textual representation of this move.
     *
     * @return the move as a string
     */

    @Override
    public String toString()
    {
        if (enteringFromBar)
        {
            return player + ": BAR -> " + toPoint + " (Die: " + dieValue + ")";
        }

        if (bearingOff)
        {
            return player + ": " + fromPoint + " -> BEAR OFF (Die: " + dieValue + ")";
        }

        return player + ": " + fromPoint + " -> " + toPoint + " (Die: " + dieValue + ")";
    }
}
