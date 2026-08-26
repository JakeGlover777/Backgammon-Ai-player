import experiment.ExperimentCsvExporter;
import game.Dice;
import game.Player;
import game.PlayerType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import statistics.DecisionStatistics;
import statistics.GameStatistics;
import statistics.SearchStatistics;
import statistics.StatisticsRecorder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExperimentCsvExporterTest
{
    @TempDir
    Path tempDirectory;

    @Test
    void shouldExportGameStatistics() throws IOException
    {
        StatisticsRecorder recorder = createStatistics();

        ExperimentCsvExporter exporter = new ExperimentCsvExporter();

        exporter.export(recorder, tempDirectory);

        Path gamesFile = tempDirectory.resolve("games.csv");

        assertTrue(Files.exists(gamesFile));

        List<String> lines = Files.readAllLines(gamesFile);

        assertEquals(
                "game_id,white_ai,black_ai,winner,turn_count",
                lines.get(0));

        assertEquals(
                "1,HEURISTIC_AI,EXPECTIMAX_AI,WHITE,50",
                lines.get(1));
    }

    @Test
    void shouldExportDecisionStatistics() throws IOException
    {
        StatisticsRecorder recorder = createStatistics();

        ExperimentCsvExporter exporter = new ExperimentCsvExporter();

        exporter.export(recorder, tempDirectory);

        Path decisionsFile = tempDirectory.resolve("decisions.csv");

        assertTrue(Files.exists(decisionsFile));

        List<String> lines = Files.readAllLines(decisionsFile);

        assertEquals(
                "game_id,player,ai_type,die_one,die_two,legal_sequences,"
                        + "decision_time_ns,nodes_evaluated,search_depth,"
                        + "node_budget,budget_reached",
                lines.get(0));

        assertEquals(
                "1,WHITE,HEURISTIC_AI,3,5,10,2000000,,,,",
                lines.get(1));

        assertEquals(
                "1,BLACK,EXPECTIMAX_AI,4,6,12,5000000,3000,2,10000,true",
                lines.get(2));
    }

    private StatisticsRecorder createStatistics()
    {
        StatisticsRecorder recorder = new StatisticsRecorder();

        GameStatistics game = new GameStatistics(
                1,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI);

        game.recordDecision(new DecisionStatistics(
                Player.WHITE,
                PlayerType.HEURISTIC_AI,
                new Dice(3, 5),
                10,
                2_000_000,
                null));

        game.recordDecision(new DecisionStatistics(
                Player.BLACK,
                PlayerType.EXPECTIMAX_AI,
                new Dice(4, 6),
                12,
                5_000_000,
                new SearchStatistics(
                        3_000,
                        2,
                        10_000,
                        true)));

        for (int i = 0; i < 50; i++)
        {
            game.incrementTurnCount();
        }

        game.setWinner(Player.WHITE);

        recorder.recordGame(game);

        return recorder;
    }
}
