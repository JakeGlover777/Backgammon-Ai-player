package statistics;

import game.Dice;
import game.Player;

public class DecisionStatistics
{
    private final Player player;
    private final String aiType;
    private final Dice dice;
    private final int legalSequenceCount;
    private final long decisionTimeNanoseconds;
    private final SearchStatistics searchStatistics;

    public DecisionStatistics(Player player, String aiType, Dice dice, int legalSequenceCount,
                              long decisionTimeNanoseconds, SearchStatistics searchStatistics)
    {
        this.player = player;
        this.aiType = aiType;
        this.dice = dice;
        this.legalSequenceCount = legalSequenceCount;
        this.decisionTimeNanoseconds = decisionTimeNanoseconds;
        this.searchStatistics = searchStatistics;
    }

    public Player getPlayer()
    {
        return player;
    }

    public String getAiType()
    {
        return aiType;
    }

    public Dice getDice()
    {
        return dice;
    }

    public int getLegalSequenceCount()
    {
        return legalSequenceCount;
    }

    public long getDecisionTimeNanoseconds()
    {
        return decisionTimeNanoseconds;
    }

    public SearchStatistics getSearchStatistics()
    {
        return searchStatistics;
    }
}
