package game;

/**
 * Represents the type of controller assigned to a player.
 *
 * <p>A player may be controlled by a human or by one of the available
 * artificial intelligence implementations.
 */

public enum PlayerType
{
    HUMAN("Human"),
    RANDOM_AI("Random AI"),
    HEURISTIC_AI("Heuristic AI"),
    EXPECTIMAX_AI("Expectimax AI");

    private final String displayName;

    PlayerType(String displayName)
    {
        this.displayName = displayName;
    }

    /**
     * Returns the display name of this player type.
     *
     * @return the display name
     */

    @Override
    public String toString()
    {
        return displayName;
    }
}
