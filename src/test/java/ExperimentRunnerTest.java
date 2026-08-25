import experiment.ExperimentRunner;
import game.Player;
import game.PlayerType;
import org.junit.jupiter.api.Test;
import statistics.GameStatistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExperimentRunnerTest
{
    @Test
    void shouldRunGameToCompletion()
    {
        ExperimentRunner runner = new ExperimentRunner();

        GameStatistics statistics = runner.runGame(
                1, PlayerType.RANDOM_AI, PlayerType.RANDOM_AI);

        assertTrue(statistics.getWinner() == Player.WHITE
                || statistics.getWinner() == Player.BLACK);

        assertTrue(statistics.getTurnCount() > 0);
    }

    @Test
    void shouldRecordRequestedNumberOfGames()
    {
        ExperimentRunner runner = new ExperimentRunner();

        runner.runMatchup(
                PlayerType.RANDOM_AI,
                PlayerType.RANDOM_AI,
                4);

        assertEquals(
                4,
                runner.getStatisticsRecorder().getGameCount());
    }

    @Test
    void shouldAlternatePlayerAssignments()
    {
        ExperimentRunner runner = new ExperimentRunner();

        runner.runMatchup(
                PlayerType.RANDOM_AI,
                PlayerType.HEURISTIC_AI,
                4);

        GameStatistics firstGame =
                runner.getStatisticsRecorder().getGames().get(0);

        GameStatistics secondGame =
                runner.getStatisticsRecorder().getGames().get(1);

        GameStatistics thirdGame =
                runner.getStatisticsRecorder().getGames().get(2);

        GameStatistics fourthGame =
                runner.getStatisticsRecorder().getGames().get(3);

        assertEquals(
                PlayerType.RANDOM_AI,
                firstGame.getWhitePlayerType());

        assertEquals(
                PlayerType.HEURISTIC_AI,
                firstGame.getBlackPlayerType());

        assertEquals(
                PlayerType.HEURISTIC_AI,
                secondGame.getWhitePlayerType());

        assertEquals(
                PlayerType.RANDOM_AI,
                secondGame.getBlackPlayerType());

        assertEquals(
                PlayerType.RANDOM_AI,
                thirdGame.getWhitePlayerType());

        assertEquals(
                PlayerType.HEURISTIC_AI,
                thirdGame.getBlackPlayerType());

        assertEquals(
                PlayerType.HEURISTIC_AI,
                fourthGame.getWhitePlayerType());

        assertEquals(
                PlayerType.RANDOM_AI,
                fourthGame.getBlackPlayerType());
    }

    @Test
    void shouldRejectHumanPlayerInGame()
    {
        ExperimentRunner runner = new ExperimentRunner();

        assertThrows(
                IllegalArgumentException.class,
                () -> runner.runGame(
                        1,
                        PlayerType.HUMAN,
                        PlayerType.RANDOM_AI));
    }

    @Test
    void shouldRejectHumanPlayerInMatchup()
    {
        ExperimentRunner runner = new ExperimentRunner();

        assertThrows(
                IllegalArgumentException.class,
                () -> runner.runMatchup(
                        PlayerType.RANDOM_AI,
                        PlayerType.HUMAN,
                        10));
    }

    @Test
    void shouldRejectZeroGames()
    {
        ExperimentRunner runner = new ExperimentRunner();

        assertThrows(
                IllegalArgumentException.class,
                () -> runner.runMatchup(
                        PlayerType.RANDOM_AI,
                        PlayerType.HEURISTIC_AI,
                        0));
    }
}
