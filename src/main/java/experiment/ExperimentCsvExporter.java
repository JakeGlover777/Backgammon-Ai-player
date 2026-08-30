package experiment;

import statistics.DecisionStatistics;
import statistics.GameStatistics;
import statistics.SearchStatistics;
import statistics.StatisticsRecorder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExperimentCsvExporter
{
    private static final String GAME_HEADER =
            "game_id,white_ai,black_ai,starting_player,winner,turn_count";

    private static final String DECISION_HEADER =
            "game_id,player,ai_type,die_one,die_two,legal_sequences,"
                    + "decision_time_ns,nodes_evaluated,search_depth,"
                    + "node_budget,budget_reached";

    public void export(StatisticsRecorder statisticsRecorder, Path outputDirectory)
            throws IOException
    {
        Files.createDirectories(outputDirectory);

        exportGames(statisticsRecorder, outputDirectory.resolve("games.csv"));

        exportDecisions(statisticsRecorder, outputDirectory.resolve("decisions.csv"));
    }

    private void exportGames(StatisticsRecorder statisticsRecorder, Path outputPath)
            throws IOException
    {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath))
        {
            writer.write(GAME_HEADER);
            writer.newLine();

            for (GameStatistics game : statisticsRecorder.getGames())
            {
                writer.write(createGameRow(game));
                writer.newLine();
            }
        }
    }

    private void exportDecisions(StatisticsRecorder statisticsRecorder, Path outputPath)
            throws IOException
    {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath))
        {
            writer.write(DECISION_HEADER);
            writer.newLine();

            for (GameStatistics game : statisticsRecorder.getGames())
            {
                for (DecisionStatistics decision : game.getDecisions())
                {
                    writer.write(createDecisionRow(
                            game.getGameId(), decision));

                    writer.newLine();
                }
            }
        }
    }

    private String createGameRow(GameStatistics game)
    {
        return game.getGameId()
                + "," + game.getWhitePlayerType().name()
                + "," + game.getBlackPlayerType().name()
                + "," + game.getStartingPlayer()
                + "," + game.getWinner()
                + "," + game.getTurnCount();
    }

    private String createDecisionRow(int gameId, DecisionStatistics decision)
    {
        SearchStatistics searchStatistics = decision.getSearchStatistics();

        String searchValues = createSearchValues(searchStatistics);

        return gameId
                + "," + decision.getPlayer()
                + "," + decision.getAiType().name()
                + "," + decision.getDice().getDieOne()
                + "," + decision.getDice().getDieTwo()
                + "," + decision.getLegalSequenceCount()
                + "," + decision.getDecisionTimeNanoseconds()
                + "," + searchValues;
    }

    private String createSearchValues(SearchStatistics searchStatistics)
    {
        if (searchStatistics == null)
        {
            return ",,,";
        }

        return searchStatistics.getNodesEvaluated()
                + "," + searchStatistics.getSearchDepth()
                + "," + searchStatistics.getNodeBudget()
                + "," + searchStatistics.isBudgetReached();
    }
}