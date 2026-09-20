package experiment;

import statistics.DecisionStatistics;
import statistics.GameStatistics;
import statistics.SearchStatistics;
import statistics.StatisticsRecorder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Exports recorded experimental statistics to CSV files.
 *
 * <p>Game-level statistics are written to a games file, while individual
 * decision statistics are written to a separate decisions file.
 */

public class ExperimentCsvExporter
{
    private static final String GAME_HEADER =
            "game_id,white_ai,black_ai,starting_player,winner,turn_count";

    private static final String DECISION_HEADER =
            "game_id,player,ai_type,die_one,die_two,legal_sequences,"
                    + "decision_time_ns,nodes_evaluated,search_depth,"
                    + "node_budget,budget_reached";

    /**
     * Exports the recorded experiment statistics to CSV files in the specified
     * output directory.
     *
     * <p>The output directory is created if necessary. Game statistics are
     * written to games.csv and decision statistics are written to decisions.csv.
     *
     * @param statisticsRecorder the recorded experiment statistics to export
     * @param outputDirectory the directory in which the CSV files are created
     * @throws IOException if an error occurs while creating or writing the files
     */

    public void export(StatisticsRecorder statisticsRecorder, Path outputDirectory)
            throws IOException
    {
        Files.createDirectories(outputDirectory);

        exportGames(statisticsRecorder, outputDirectory.resolve("games.csv"));

        exportDecisions(statisticsRecorder, outputDirectory.resolve("decisions.csv"));
    }

    /**
     * Writes the recorded game-level statistics to a CSV file.
     *
     * @param statisticsRecorder the recorded experiment statistics
     * @param outputPath the path of the game statistics CSV file
     * @throws IOException if an error occurs while writing the file
     */

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

    /**
     * Writes the recorded decision-level statistics to a CSV file.
     *
     * @param statisticsRecorder the recorded experiment statistics
     * @param outputPath the path of the decision statistics CSV file
     * @throws IOException if an error occurs while writing the file
     */

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
                    writer.write(createDecisionRow(game.getGameId(), decision));

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
