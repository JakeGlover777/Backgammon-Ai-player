package statistics;

import game.MoveSequence;

/**
 * Stores the result of an AI decision together with the statistics recorded
 * for that decision.
 */

public class DecisionResult
{
    private final MoveSequence moveSequence;
    private final DecisionStatistics statistics;

    /**
     * Creates a decision result containing the selected move sequence and its
     * associated statistics.
     *
     * @param moveSequence the move sequence selected by the AI
     * @param statistics the statistics recorded for the decision
     */

    public DecisionResult(MoveSequence moveSequence, DecisionStatistics statistics)
    {
        this.moveSequence = moveSequence;
        this.statistics = statistics;
    }

    /**
     * Returns the move sequence selected by the AI.
     *
     * @return the selected move sequence
     */

    public MoveSequence getMoveSequence()
    {
        return moveSequence;
    }

    /**
     * Returns the statistics recorded for the AI decision.
     *
     * @return the decision statistics
     */

    public DecisionStatistics getStatistics()
    {
        return statistics;
    }
}
