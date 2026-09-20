package statistics;

import game.Dice;
import game.Player;
import game.PlayerType;

/**
 * Stores statistics recorded for a single AI move decision.
 *
 * <p>The recorded data includes the player and AI type, dice values, number
 * of legal move sequences, decision time, and any available search-specific
 * statistics.
 */

public class DecisionStatistics
{
    private final Player player;
    private final PlayerType aiType;
    private final Dice dice;
    private final int legalSequenceCount;
    private final long decisionTimeNanoseconds;
    private final SearchStatistics searchStatistics;

    /**
     * Creates a set of statistics for a single AI move decision.
     *
     * @param player the player making the decision
     * @param aiType the type of AI making the decision
     * @param dice the dice used for the decision
     * @param legalSequenceCount the number of legal move sequences available
     * @param decisionTimeNanoseconds the time taken to make the decision in
     *                                nanoseconds
     * @param searchStatistics the search-specific statistics, or null if they
     *                         are not applicable
     */

    public DecisionStatistics(Player player, PlayerType aiType, Dice dice, int legalSequenceCount,
                              long decisionTimeNanoseconds, SearchStatistics searchStatistics)
    {
        this.player = player;
        this.aiType = aiType;
        this.dice = dice;
        this.legalSequenceCount = legalSequenceCount;
        this.decisionTimeNanoseconds = decisionTimeNanoseconds;
        this.searchStatistics = searchStatistics;
    }

    /**
     * Returns the player that made the decision.
     *
     * @return the player
     */

    public Player getPlayer()
    {
        return player;
    }

    /**
     * Returns the type of AI that made the decision.
     *
     * @return the AI type
     */

    public PlayerType getAiType()
    {
        return aiType;
    }

    /**
     * Returns the dice used for the decision.
     *
     * @return the recorded dice
     */

    public Dice getDice()
    {
        return dice;
    }

    /**
     * Returns the number of legal move sequences available for the decision.
     *
     * @return the number of legal move sequences
     */

    public int getLegalSequenceCount()
    {
        return legalSequenceCount;
    }

    /**
     * Returns the time taken to make the AI decision in nanoseconds.
     *
     * @return the decision time in nanoseconds
     */

    public long getDecisionTimeNanoseconds()
    {
        return decisionTimeNanoseconds;
    }

    /**
     * Returns the search-specific statistics associated with the decision.
     *
     * @return the search statistics, or null if they are not applicable
     */

    public SearchStatistics getSearchStatistics()
    {
        return searchStatistics;
    }
}
