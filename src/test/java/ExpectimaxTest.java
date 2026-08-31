import ai.ExpectimaxAi;
import game.Board;
import game.Dice;
import game.Move;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpectimaxTest
{
    @Test
    void shouldReturnLegalMoveSequence()
    {
        Board board = new Board();
        Dice dice = new Dice(3, 5);

        ExpectimaxAi ai = new ExpectimaxAi(1, 10_000);
        MoveGenerator moveGenerator = new MoveGenerator();

        MoveSequence chosenSequence = ai.chooseMove(board, Player.WHITE, dice);

        List<MoveSequence> legalSequences = moveGenerator.generateMoveSequences(board,
                Player.WHITE, dice);

        assertNotNull(chosenSequence);
        assertTrue(containsEquivalentSequence(legalSequences, chosenSequence));
    }

    @Test
    void shouldEvaluateNodesWhenChoosingMove()
    {
        Board board = new Board();
        Dice dice = new Dice(3, 5);

        ExpectimaxAi ai = new ExpectimaxAi(1, 10_000);

        ai.chooseMove(board, Player.WHITE, dice);

        assertTrue(ai.getNodesEvaluated() > 0);
    }

    @Test
    void shouldReportConfiguredSearchDepth()
    {
        ExpectimaxAi ai = new ExpectimaxAi(2, 10_000);

        assertTrue(ai.getSearchDepth() == 2);
    }

    @Test
    void shouldReportConfiguredNodeBudget()
    {
        ExpectimaxAi ai = new ExpectimaxAi(1, 5_000);

        assertTrue(ai.getNodeBudget() == 5_000);
    }

    @Test
    void shouldReachBudgetWhenBudgetIsTooSmallForChanceNode()
    {
        Board board = new Board();
        Dice dice = new Dice(3, 5);

        ExpectimaxAi ai = new ExpectimaxAi(2, 10);

        MoveSequence chosenSequence = ai.chooseMove(board, Player.WHITE, dice);

        assertNotNull(chosenSequence);
        assertTrue(ai.wasBudgetReached());
    }

    @Test
    void shouldStillReturnLegalMoveWhenBudgetIsTooSmall()
    {
        Board board = new Board();
        Dice dice = new Dice(3, 5);

        ExpectimaxAi ai = new ExpectimaxAi(2, 10);
        MoveGenerator moveGenerator = new MoveGenerator();

        MoveSequence chosenSequence = ai.chooseMove(board, Player.WHITE, dice);

        List<MoveSequence> legalSequences = moveGenerator.generateMoveSequences(
                board, Player.WHITE, dice);

        assertNotNull(chosenSequence);
        assertTrue(containsEquivalentSequence(legalSequences, chosenSequence));
    }

    @Test
    void shouldNotReachBudgetWithSufficientBudgetAtDepthOne()
    {
        Board board = new Board();
        Dice dice = new Dice(3, 5);

        ExpectimaxAi ai = new ExpectimaxAi(1, 100_000);

        ai.chooseMove(board, Player.WHITE, dice);

        assertFalse(ai.wasBudgetReached());
    }

    @Test
    void largerBudgetShouldAllowAtLeastAsMuchSearch()
    {
        Board board = new Board();
        Dice dice = new Dice(3, 5);

        ExpectimaxAi smallBudgetAi = new ExpectimaxAi(2, 100);
        ExpectimaxAi largeBudgetAi = new ExpectimaxAi(2, 10_000);

        smallBudgetAi.chooseMove(board, Player.WHITE, dice);
        largeBudgetAi.chooseMove(board, Player.WHITE, dice);

        assertTrue(largeBudgetAi.getNodesEvaluated()
                >= smallBudgetAi.getNodesEvaluated());
    }

    @Test
    void depthTwoShouldCompleteWithLimitedBudget()
    {
        Board board = new Board();
        Dice dice = new Dice(3, 5);

        ExpectimaxAi ai = new ExpectimaxAi(2, 1_000);

        MoveSequence chosenSequence = ai.chooseMove(board, Player.WHITE, dice);

        assertNotNull(chosenSequence);
        assertTrue(ai.getNodesEvaluated() > 0);
    }

    private boolean containsEquivalentSequence(List<MoveSequence> legalSequences,
                                               MoveSequence chosenSequence)
    {
        for (MoveSequence sequence : legalSequences)
        {
            if (sequencesMatch(sequence, chosenSequence))
            {
                return true;
            }
        }

        return false;
    }

    private boolean sequencesMatch(MoveSequence first, MoveSequence second)
    {
        if (first.size() != second.size())
        {
            return false;
        }

        for (int i = 0; i < first.size(); i++)
        {
            Move firstMove = first.getMoves().get(i);
            Move secondMove = second.getMoves().get(i);

            if (firstMove.getPlayer() != secondMove.getPlayer())
            {
                return false;
            }

            if (firstMove.getFromPoint() != secondMove.getFromPoint())
            {
                return false;
            }

            if (firstMove.getToPoint() != secondMove.getToPoint())
            {
                return false;
            }

            if (firstMove.getDieValue() != secondMove.getDieValue())
            {
                return false;
            }

            if (firstMove.isBearingOff() != secondMove.isBearingOff())
            {
                return false;
            }

            if (firstMove.isEnteringFromBar() != secondMove.isEnteringFromBar())
            {
                return false;
            }
        }

        return true;
    }
}
