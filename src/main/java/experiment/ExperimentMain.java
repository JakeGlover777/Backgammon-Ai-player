package experiment;

import game.PlayerType;

import java.io.IOException;
import java.nio.file.Path;

public class ExperimentMain
{
    public static void main(String[] args) throws IOException
    {
        ExperimentRunner runner = new ExperimentRunner();

        runner.runMatchup(PlayerType.RANDOM_AI, PlayerType.HEURISTIC_AI, 100);

        ExperimentCsvExporter exporter = new ExperimentCsvExporter();

        exporter.export(
                runner.getStatisticsRecorder(),
                Path.of("experiment-output", "random-vs-heuristic"));

        ExperimentAnalyser analyser = new ExperimentAnalyser(runner.getStatisticsRecorder());

        System.out.println("Games: " + analyser.getTotalGames());
        System.out.println("Random wins: " + analyser.getWins(PlayerType.RANDOM_AI));
        System.out.println("Heuristic wins: " + analyser.getWins(PlayerType.HEURISTIC_AI));
        System.out.println("Random win rate: "
                + analyser.getWinRate(PlayerType.RANDOM_AI) + "%");
        System.out.println("Heuristic win rate: "
                + analyser.getWinRate(PlayerType.HEURISTIC_AI) + "%");
        System.out.println("Average turns: " + analyser.getAverageTurnCount());
    }
}
