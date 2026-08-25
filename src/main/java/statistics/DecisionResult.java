package statistics;

import game.MoveSequence;

public class DecisionResult
{
    private final MoveSequence moveSequence;
    private final DecisionStatistics statistics;

    public DecisionResult(MoveSequence moveSequence, DecisionStatistics statistics)
    {
        this.moveSequence = moveSequence;
        this.statistics = statistics;
    }

    public MoveSequence getMoveSequence()
    {
        return moveSequence;
    }

    public DecisionStatistics getStatistics()
    {
        return statistics;
    }
}
