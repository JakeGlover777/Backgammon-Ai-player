package game;

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

    @Override
    public String toString()
    {
        return displayName;
    }
}
