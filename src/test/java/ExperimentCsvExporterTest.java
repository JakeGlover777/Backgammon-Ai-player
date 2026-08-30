import game.Dice;
import game.Player;
import game.PlayerType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import statistics.DecisionStatistics;
import statistics.GameStatistics;
import statistics.SearchStatistics;
import statistics.StatisticsRecorder;
import experiment.ExperimentCsvExporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExperimentCsvExporterTest
{
    @TempDir
    Path tempDirectory;

    @Test
    public void exportCreatesGameAndDecisionFiles() throws IOException
    {
        StatisticsRecorder recorder = createStatistics();

        ExperimentCsvExporter exporter = new ExperimentCsvExporter();

        exporter.export(recorder, tempDirectory);

        assertTrue(Files.exists(tempDirectory.resolve("games.csv")));
        assertTrue(Files.exists(tempDirectory.resolve("decisions.csv")));
    }

    @Test
    public void exportWritesExpectedGameData() throws IOException
    {
        StatisticsRecorder recorder = createStatistics();

        ExperimentCsvExporter exporter = new ExperimentCsvExporter();

        exporter.export(recorder, tempDirectory);

        List<String> lines =
                Files.readAllLines(tempDirectory.resolve("games.csv"));

        assertEquals(2, lines.size());

        assertEquals(
                "game_id,white_ai,black_ai,starting_player,winner,turn_count",
                lines.get(0));

        assertEquals(
                "1,HEURISTIC_AI,EXPECTIMAX_AI,BLACK,WHITE,50",
                lines.get(1));
    }

    @Test
    public void exportWritesExpectedDecisionData() throws IOException
    {
        StatisticsRecorder recorder = createStatistics();

        ExperimentCsvExporter exporter = new ExperimentCsvExporter();

        exporter.export(recorder, tempDirectory);

        List<String> lines =
                Files.readAllLines(tempDirectory.resolve("decisions.csv"));

        assertEquals(3, lines.size());

        assertEquals(
                "game_id,player,ai_type,die_one,die_two,legal_sequences,"
                        + "decision_time_ns,nodes_evaluated,search_depth,"
                        + "node_budget,budget_reached",
                lines.get(0));

        assertEquals(
                "1,WHITE,HEURISTIC_AI,3,4,5,1000,,,,",
                lines.get(1));

        assertEquals(
                "1,BLACK,EXPECTIMAX_AI,6,2,8,2000,500,2,1000,true",
                lines.get(2));
    }

    private StatisticsRecorder createStatistics()
    {
        StatisticsRecorder recorder = new StatisticsRecorder();

        GameStatistics game = new GameStatistics(
                1,
                PlayerType.HEURISTIC_AI,
                PlayerType.EXPECTIMAX_AI,
                Player.BLACK);

        DecisionStatistics heuristicDecision =
                new DecisionStatistics(
                        Player.WHITE,
                        PlayerType.HEURISTIC_AI,
                        new Dice(3, 4),
                        5,
                        1000,
                        null);

        SearchStatistics searchStatistics =
                new SearchStatistics(
                        500,
                        2,
                        1000,
                        true);

        DecisionStatistics expectimaxDecision =
                new DecisionStatistics(
                        Player.BLACK,
                        PlayerType.EXPECTIMAX_AI,
                        new Dice(6, 2),
                        8,
                        2000,
                        searchStatistics);

        game.recordDecision(heuristicDecision);
        game.recordDecision(expectimaxDecision);

        for (int i = 0; i < 50; i++)
        {
            game.incrementTurnCount();
        }

        game.setWinner(Player.WHITE);

        recorder.recordGame(game);

        return recorder;
    }
}
