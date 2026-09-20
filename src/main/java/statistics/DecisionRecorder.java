package statistics;

import ai.AiPlayer;
import ai.ExpectimaxAi;
import game.Board;
import game.Dice;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;
import game.PlayerType;

/**
 * Records statistics associated with AI move decisions.
 *
 * <p>The recorder measures AI decision time, records the number of available
 * legal move sequences, and captures additional search statistics when the
 * decision is made by an Expectimax AI.
 */

public class DecisionRecorder
{
    private final MoveGenerator moveGenerator;

    /**
     * Creates a decision recorder with a move generator used to determine the
     * number of legal move sequences.
     */

    public DecisionRecorder()
    {
        moveGenerator = new MoveGenerator();
    }

    /**
     * Records an AI move decision and the statistics associated with it.
     *
     * <p>The AI decision itself is timed independently of the generation used to
     * count the available legal move sequences. Additional search statistics are
     * recorded when the supplied AI is an Expectimax AI.
     *
     * @param board the current board state
     * @param player the player making the decision
     * @param dice the dice available for the turn
     * @param aiPlayer the AI making the decision
     * @param playerType the type of AI making the decision
     * @return the selected move sequence together with its recorded statistics
     */

    public DecisionResult recordDecision(Board board, Player player, Dice dice, AiPlayer aiPlayer,
                                         PlayerType playerType)
    {
        int legalSequenceCount = moveGenerator.generateMoveSequences(board, player, dice).size();

        long startTime = System.nanoTime();

        MoveSequence sequence = aiPlayer.chooseMove(board, player, dice);

        long decisionTimeNanoseconds = System.nanoTime() - startTime;

        SearchStatistics searchStatistics = createSearchStatistics(aiPlayer);
        Dice recordedDice = new Dice(dice.getDieOne(), dice.getDieTwo());

        DecisionStatistics statistics = new DecisionStatistics(player, playerType, recordedDice,
                legalSequenceCount, decisionTimeNanoseconds, searchStatistics);

        return new DecisionResult(sequence, statistics);
    }

    /**
     * Creates search-specific statistics when the supplied AI uses Expectimax
     * search.
     *
     * @param aiPlayer the AI used for the recorded decision
     * @return the Expectimax search statistics, or null if the AI is not an
     *         Expectimax AI
     */

    private SearchStatistics createSearchStatistics(AiPlayer aiPlayer)
    {
        if (!(aiPlayer instanceof ExpectimaxAi))
        {
            return null;
        }

        ExpectimaxAi expectimaxAi = (ExpectimaxAi) aiPlayer;

        return new SearchStatistics(expectimaxAi.getNodesEvaluated(), expectimaxAi.getSearchDepth(),
                expectimaxAi.getNodeBudget(), expectimaxAi.wasBudgetReached());
    }
}
