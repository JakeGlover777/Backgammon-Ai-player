import ai.HeuristicAi;
import ai.RandomAi;
import game.Board;
import game.Dice;
import game.Player;
import game.PlayerType;
import org.junit.jupiter.api.Test;
import statistics.DecisionRecorder;
import statistics.DecisionResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecisionRecorderTest
{
    @Test
    void shouldReturnChosenMoveSequence()
    {
        Board board = new Board();
        Dice dice = new Dice(3, 5);

        DecisionRecorder recorder = new DecisionRecorder();

        DecisionResult result = recorder.recordDecision(
                board,
                Player.WHITE,
                dice,
                new HeuristicAi(),
                PlayerType.HEURISTIC_AI);

        assertNotNull(result);
        assertNotNull(result.getMoveSequence());
    }

    @Test
    void shouldRecordDecisionStatistics()
    {
        Board board = new Board();
        Dice dice = new Dice(3, 5);

        DecisionRecorder recorder = new DecisionRecorder();

        DecisionResult result = recorder.recordDecision(
                board,
                Player.WHITE,
                dice,
                new RandomAi(),
                PlayerType.RANDOM_AI);

        assertNotNull(result.getStatistics());
        assertEquals(Player.WHITE, result.getStatistics().getPlayer());
        assertEquals(PlayerType.RANDOM_AI, result.getStatistics().getAiType());
        assertEquals(3, result.getStatistics().getDice().getDieOne());
        assertEquals(5, result.getStatistics().getDice().getDieTwo());
        assertTrue(result.getStatistics().getLegalSequenceCount() > 0);
        assertTrue(result.getStatistics().getDecisionTimeNanoseconds() >= 0);
    }

    @Test
    void shouldNotCreateSearchStatisticsForNonExpectimaxAi()
    {
        Board board = new Board();
        Dice dice = new Dice(3, 5);

        DecisionRecorder recorder = new DecisionRecorder();

        DecisionResult result = recorder.recordDecision(
                board,
                Player.WHITE,
                dice,
                new HeuristicAi(),
                PlayerType.HEURISTIC_AI);

        assertNull(result.getStatistics().getSearchStatistics());
    }
}
