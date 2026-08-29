package experiment;

import game.PlayerType;

import java.io.IOException;
import java.nio.file.Path;

public class ExperimentMain
{
    public static void main(String[] args) throws IOException
    {
        PlayerType firstAi = PlayerType.EXPECTIMAX_AI;
        PlayerType secondAi = PlayerType.HEURISTIC_AI;

        int numberOfGames = 10;
        int expectimaxDepth = 2;
        int expectimaxNodeBudget = 5000;

        ExperimentRunner runner = new ExperimentRunner(expectimaxDepth, expectimaxNodeBudget);

        runner.runMatchup(firstAi, secondAi, numberOfGames);

        String experimentName =
                "expectimax-depth-" + expectimaxDepth
                        + "-budget-" + expectimaxNodeBudget
                        + "-vs-heuristic";

        ExperimentCsvExporter exporter = new ExperimentCsvExporter();

        exporter.export(runner.getStatisticsRecorder(), Path.of("experiment-output", experimentName));

        ExperimentAnalyser analyser =
                new ExperimentAnalyser(runner.getStatisticsRecorder());

        System.out.println("Games: " + analyser.getTotalGames());

        System.out.println("Incomplete games: "
                + analyser.getIncompleteGames());

        System.out.println(firstAi + " wins: "
                + analyser.getWins(firstAi));

        System.out.println(secondAi + " wins: "
                + analyser.getWins(secondAi));

        System.out.println(firstAi + " win rate: "
                + analyser.getWinRate(firstAi) + "%");

        System.out.println(secondAi + " win rate: "
                + analyser.getWinRate(secondAi) + "%");

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
                + analyser.getBudgetReachedPercentage() + "%");

        System.out.println("Expectimax average decision time: "
                + analyser.getAverageDecisionTimeMilliseconds(PlayerType.EXPECTIMAX_AI)
                + " ms");

        System.out.println("Heuristic average decision time: "
                + analyser.getAverageDecisionTimeMilliseconds(PlayerType.HEURISTIC_AI)
                + " ms");
    }
}
