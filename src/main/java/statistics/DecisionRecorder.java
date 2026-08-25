package statistics;

import ai.AiPlayer;
import ai.ExpectimaxAi;
import game.Board;
import game.Dice;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;
import game.PlayerType;

public class DecisionRecorder
{
    private final MoveGenerator moveGenerator;

    public DecisionRecorder()
    {
        moveGenerator = new MoveGenerator();
    }

    public DecisionResult recordDecision(Board board, Player player, Dice dice, AiPlayer aiPlayer,
                                         PlayerType playerType)
    {
        int legalSequenceCount = moveGenerator.generateMoveSequences(board, player, dice).size();

        long startTime = System.nanoTime();

        MoveSequence sequence = aiPlayer.chooseMove(board, player, dice);

        long decisionTimeNanoseconds = System.nanoTime() - startTime;

        SearchStatistics searchStatistics = createSearchStatistics(aiPlayer);
        Dice recordedDice = new Dice(dice.getDieOne(), dice.getDieTwo());

        DecisionStatistics statistics = new DecisionStatistics(player, playerType, recordedDice, legalSequenceCount,
                decisionTimeNanoseconds, searchStatistics);

        return new DecisionResult(sequence, statistics);
    }

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
