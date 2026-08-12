package game;

public class Point
{
    private Player owner;
    private int checkerCount;

    public Point()
    {
        owner = Player.NONE;
        checkerCount = 0;
    }

    public Player getOwner()
    {
        return owner;
    }

    public int getCheckerCount()
    {
        return checkerCount;
    }

    public boolean isEmpty()
    {
        return checkerCount == 0;
    }

    public void addChecker(Player player)
    {
        if (player == Player.NONE)
        {
            throw new IllegalArgumentException(
                    "A checker must belong to WHITE or BLACK."
            );
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
            throw new IllegalStateException(
                    "Cannot add a checker to a point owned by the opponent."
            );
        }
    }

    public void removeChecker()
    {
        if(isEmpty())
        {
            throw new IllegalStateException("Cannot remove a checker from an empty point.");
        }
        checkerCount --;

        if(checkerCount == 0 )
        {
            owner = Player.NONE;
        }
    }

    @Override
    public String toString()
    {
        if (isEmpty())
        {
            return "Empty";
        }

        return owner + " x" + checkerCount;
    }

    public Point(Point other)
    {
        this.owner = other.owner;
        this.checkerCount = other.checkerCount;
    }
}