package game;

/**
 * Represents a single point on a Backgammon board.
 *
 * <p>A point tracks its current owner and the number of checkers occupying it.
 * An empty point has no owner and a checker count of zero.
 */

public class Point
{
    private Player owner;
    private int checkerCount;

    /**
     * Creates an empty point with no owner.
     */

    public Point()
    {
        owner = Player.NONE;
        checkerCount = 0;
    }

    /**
     * Creates a copy of another point.
     *
     * @param other the point to copy
     */

    public Point(Point other)
    {
        owner = other.owner;
        checkerCount = other.checkerCount;
    }

    /**
     * Returns the player that owns this point.
     *
     * @return the owner of the point.
     */

    public Player getOwner()
    {
        return owner;
    }

    /**
     * Returns the number of checkers on this point.
     *
     * @return the number of checkers
     */

    public int getCheckerCount()
    {
        return checkerCount;
    }

    /**
     * Determines whether this point contains no checkers.
     *
     * @return true if the point is empty, otherwise false
     */

    public boolean isEmpty()
    {
        return checkerCount == 0;
    }

    /**
     * Adds a checker belonging to the specified player to this point.
     *
     * @param player the player whose checker is added
     * @throws IllegalArgumentException if the player field is none.
     * @throws IllegalStateException if the point is owned by the opposing player
     */

    public void addChecker(Player player)
    {
        if (player == Player.NONE)
        {
            throw new IllegalArgumentException("A checker must belong to WHITE or BLACK.");
        }

        if (isEmpty())
        {
            owner = player;
            checkerCount++;
        }
        else if (owner == player)
        {
            checkerCount++;
        }
        else
        {
            throw new IllegalStateException("Cannot add a checker to a point owned by the opponent.");
        }
    }

    /**
     * Removes one checker from this point.
     *
     * <p>If the final checker is removed, the point becomes unowned.
     *
     * @throws IllegalStateException if the point is empty
     */

    public void removeChecker()
    {
        if (isEmpty())
        {
            throw new IllegalStateException("Cannot remove a checker from an empty point.");
        }

        checkerCount--;

        if (checkerCount == 0)
        {
            owner = Player.NONE;
        }
    }

    /**
     * Returns a textual representation of this point.
     *
     * @return the point contents as a string
     */

    @Override
    public String toString()
    {
        if (isEmpty())
        {
            return "Empty";
        }

        return owner + " x" + checkerCount;
    }
}
