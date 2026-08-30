import experiment.ExperimentAnalyser;
import game.Dice;
import game.Player;
import game.PlayerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import statistics.DecisionStatistics;
import statistics.GameStatistics;
import statistics.SearchStatistics;
import statistics.StatisticsRecorder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExperimentAnalyserTest
{
    private StatisticsRecorder recorder;
    private ExperimentAnalyser analyser;

    @BeforeEach
    void setUp()
    {
        recorder = new StatisticsRecorder();
        analyser = new ExperimentAnalyser(recorder);
    }

    @Test
    void shouldReturnTotalGames()
    {
        recorder.recordGame(createGame(
                1,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.WHITE,
                50));

        recorder.recordGame(createGame(
                2,
                PlayerType.EXPECTIMAX_AI,
                PlayerType.HEURISTIC_AI,
                Player.BLACK,
                60));

        assertEquals(2, analyser.getTotalGames());
    }

    @Test
    void shouldCalculateWinsByAiType()
    {
        recorder.recordGame(createGame(
                1,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.WHITE,
                50));

        recorder.recordGame(createGame(
                2,
                PlayerType.EXPECTIMAX_AI,
                PlayerType.HEURISTIC_AI,
                Player.BLACK,
                60));

        recorder.recordGame(createGame(
                3,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.BLACK,
                55));

        assertEquals(2, analyser.getWins(PlayerType.HEURISTIC_AI));
        assertEquals(1, analyser.getWins(PlayerType.EXPECTIMAX_AI));
    }

    @Test
    void shouldCalculateWinRate()
    {
        recorder.recordGame(createGame(
                1,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.WHITE,
                50));

        recorder.recordGame(createGame(
                2,
                PlayerType.EXPECTIMAX_AI,
                PlayerType.HEURISTIC_AI,
                Player.BLACK,
                60));

        recorder.recordGame(createGame(
                3,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.BLACK,
                55));

        recorder.recordGame(createGame(
                4,
                PlayerType.EXPECTIMAX_AI,
                PlayerType.HEURISTIC_AI,
                Player.WHITE,
                45));

        assertEquals(
                50.0,
                analyser.getWinRate(PlayerType.HEURISTIC_AI),
                0.001);

        assertEquals(
                50.0,
                analyser.getWinRate(PlayerType.EXPECTIMAX_AI),
                0.001);
    }

    @Test
    void shouldCountWhiteAndBlackWins()
    {
        recorder.recordGame(createGame(
                1,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.WHITE,
                50));

        recorder.recordGame(createGame(
                2,
                PlayerType.EXPECTIMAX_AI,
                PlayerType.HEURISTIC_AI,
                Player.BLACK,
                60));

        recorder.recordGame(createGame(
                3,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.BLACK,
                55));

        assertEquals(1, analyser.getWhiteWins());
        assertEquals(2, analyser.getBlackWins());
    }

    @Test
    void shouldCalculateAverageTurnCount()
    {
        recorder.recordGame(createGame(
                1,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.WHITE,
                40));

        recorder.recordGame(createGame(
                2,
                PlayerType.EXPECTIMAX_AI,
                PlayerType.HEURISTIC_AI,
                Player.BLACK,
                60));

        assertEquals(
                50.0,
                analyser.getAverageTurnCount(),
                0.001);
    }

    @Test
    void shouldCalculateDecisionStatisticsByAiType()
    {
        GameStatistics game = createGame(
                1,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.WHITE,
                50);

        game.recordDecision(new DecisionStatistics(
                Player.WHITE,
                PlayerType.HEURISTIC_AI,
                new Dice(3, 5),
                10,
                2_000_000,
                null));

        game.recordDecision(new DecisionStatistics(
                Player.WHITE,
                PlayerType.HEURISTIC_AI,
                new Dice(2, 4),
                20,
                4_000_000,
                null));

        recorder.recordGame(game);

        assertEquals(
                2,
                analyser.getDecisionCount(PlayerType.HEURISTIC_AI));

        assertEquals(
                3.0,
                analyser.getAverageDecisionTimeMilliseconds(
                        PlayerType.HEURISTIC_AI),
                0.001);

        assertEquals(
                15.0,
                analyser.getAverageLegalSequenceCount(
                        PlayerType.HEURISTIC_AI),
                0.001);
    }

    @Test
    void shouldCalculateExpectimaxSearchStatistics()
    {
        GameStatistics game = createGame(
                1,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.BLACK,
                50);

        game.recordDecision(new DecisionStatistics(
                Player.BLACK,
                PlayerType.EXPECTIMAX_AI,
                new Dice(3, 5),
                10,
                5_000_000,
                new SearchStatistics(
                        1_000,
                        2,
                        10_000,
                        false)));

        game.recordDecision(new DecisionStatistics(
                Player.BLACK,
                PlayerType.EXPECTIMAX_AI,
                new Dice(4, 6),
                12,
                7_000_000,
                new SearchStatistics(
                        3_000,
                        2,
                        10_000,
                        true)));

        recorder.recordGame(game);

        assertEquals(
                2_000.0,
                analyser.getAverageNodesEvaluated(),
                0.001);

        assertEquals(
                1,
                analyser.getBudgetReachedCount());

        assertEquals(
                50.0,
                analyser.getBudgetReachedPercentage(),
                0.001);
    }

    @Test
    void shouldReturnZeroForEmptyStatistics()
    {
        assertEquals(0, analyser.getTotalGames());

        assertEquals(
                0.0,
                analyser.getWinRate(PlayerType.HEURISTIC_AI),
                0.001);

        assertEquals(
                0.0,
                analyser.getAverageTurnCount(),
                0.001);

        assertEquals(
                0.0,
                analyser.getAverageDecisionTimeMilliseconds(
                        PlayerType.HEURISTIC_AI),
                0.001);

        assertEquals(
                0.0,
                analyser.getAverageLegalSequenceCount(
                        PlayerType.HEURISTIC_AI),
                0.001);

        assertEquals(
                0.0,
                analyser.getAverageNodesEvaluated(),
                0.001);

        assertEquals(
                0.0,
                analyser.getBudgetReachedPercentage(),
                0.001);

        assertEquals(
                0,
                analyser.getIncompleteGames());
    }

    private GameStatistics createGame(int gameId, PlayerType whitePlayerType,
                                      PlayerType blackPlayerType, Player winner,
                                      int turnCount)
    {
        GameStatistics game =
                new GameStatistics(gameId, whitePlayerType, blackPlayerType, Player.WHITE);

        for (int i = 0; i < turnCount; i++)
        {
            game.incrementTurnCount();
        }

        game.setWinner(winner);

        return game;
    }

    @Test
    void shouldCountIncompleteGames()
    {
        recorder.recordGame(createGame(
                1,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.NONE,
                1000));

        recorder.recordGame(createGame(
                2,
                PlayerType.EXPECTIMAX_AI,
                PlayerType.HEURISTIC_AI,
                Player.WHITE,
                60));

        assertEquals(
                1,
                analyser.getIncompleteGames());
    }

    @Test
    void shouldExcludeIncompleteGamesFromWinRate()
    {
        recorder.recordGame(createGame(
                1,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.WHITE,
                50));

        recorder.recordGame(createGame(
                2,
                PlayerType.EXPECTIMAX_AI,
                PlayerType.HEURISTIC_AI,
                Player.WHITE,
                60));

        recorder.recordGame(createGame(
                3,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.NONE,
                1000));

        assertEquals(
                50.0,
                analyser.getWinRate(PlayerType.HEURISTIC_AI),
                0.001);

        assertEquals(
                50.0,
                analyser.getWinRate(PlayerType.EXPECTIMAX_AI),
                0.001);
    }
}
