package experiment;

import game.PlayerType;
import game.Player;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExperimentMain
{
    public static void main(String[] args) throws IOException
    {
        PlayerType firstAi = PlayerType.HEURISTIC_AI;
        PlayerType secondAi = PlayerType.HEURISTIC_AI;

        int numberOfGames = 1000;
        int expectimaxDepth = 2;
        int expectimaxNodeBudget = 5000;

        ExperimentRunner runner = new ExperimentRunner(expectimaxDepth, expectimaxNodeBudget);

        runner.runMatchup(firstAi, secondAi, numberOfGames);

        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        String experimentName = firstAi.name().toLowerCase()
                        + "-vs-"
                        + secondAi.name().toLowerCase()
                        + "-depth-"
                        + expectimaxDepth
                        + "-budget-"
                        + expectimaxNodeBudget
                        + "-"
                        + timestamp;

        ExperimentCsvExporter exporter =
                new ExperimentCsvExporter();

        exporter.export(runner.getStatisticsRecorder(),
                Path.of("experiment-output", experimentName));

        ExperimentAnalyser analyser = new ExperimentAnalyser(runner.getStatisticsRecorder());

        System.out.println("Games: "
                        + analyser.getTotalGames());

        System.out.println("Incomplete games: "
                        + analyser.getIncompleteGames());

        System.out.println(firstAi
                        + " wins: "
                        + analyser.getWins(firstAi));

        System.out.println(secondAi
                        + " wins: "
                        + analyser.getWins(secondAi));

        System.out.println(firstAi
                        + " win rate: "
                        + analyser.getWinRate(firstAi)
                        + "%");

        System.out.println(secondAi
                        + " win rate: "
                        + analyser.getWinRate(secondAi)
                        + "%");

        System.out.println("Average turns: "
                        + analyser.getAverageTurnCount());

        System.out.println("Expectimax depth: "
                        + expectimaxDepth);

        System.out.println("Expectimax node budget: "
                        + expectimaxNodeBudget);

        System.out.println("Average Expectimax nodes: "
                        + analyser.getAverageNodesEvaluated());

        System.out.println("Budget reached count: "
                        + analyser.getBudgetReachedCount());

        System.out.println("Budget reached percentage: "
                        + analyser.getBudgetReachedPercentage()
                        + "%");

        System.out.println("Expectimax average decision time: "
                        + analyser.getAverageDecisionTimeMilliseconds(PlayerType.EXPECTIMAX_AI)
                        + " ms");

        System.out.println("Heuristic average decision time: "
                        + analyser.getAverageDecisionTimeMilliseconds(PlayerType.HEURISTIC_AI)
                        + " ms");

        System.out.println("Results saved to: "
                        + experimentName);

        System.out.println("White starts: "
                + analyser.getWhiteStarts());

        System.out.println("Black starts: "
                + analyser.getBlackStarts());

        System.out.println("White wins when starting: "
                + analyser.getWinsWhenStarting(Player.WHITE));

        System.out.println("Black wins when starting: "
                + analyser.getWinsWhenStarting(Player.BLACK));

        System.out.println("White starter win rate: "
                + analyser.getStartingPlayerWinRate(Player.WHITE)
                + "%");

        System.out.println("Black starter win rate: "
                + analyser.getStartingPlayerWinRate(Player.BLACK)
                + "%");

        System.out.println();
        System.out.println("Sanity statistics:");

        System.out.println("White wins: "
                + analyser.getWhiteWins());

        System.out.println("Black wins: "
                + analyser.getBlackWins());

        System.out.println("White win rate: "
                + analyser.getWhiteWinRate()
                + "%");

        System.out.println("Black win rate: "
                + analyser.getBlackWinRate()
                + "%");

        System.out.println("WHITE start -> WHITE win: "
                + analyser.getWinsByStarterAndWinner(
                Player.WHITE, Player.WHITE));

        System.out.println("WHITE start -> BLACK win: "
                + analyser.getWinsByStarterAndWinner(
                Player.WHITE, Player.BLACK));

        System.out.println("BLACK start -> WHITE win: "
                + analyser.getWinsByStarterAndWinner(
                Player.BLACK, Player.WHITE));

        System.out.println("BLACK start -> BLACK win: "
                + analyser.getWinsByStarterAndWinner(
                Player.BLACK, Player.BLACK));

        System.out.println("Overall starting-player win rate: "
                + analyser.getOverallStarterWinRate()
                + "%");
    }
}
