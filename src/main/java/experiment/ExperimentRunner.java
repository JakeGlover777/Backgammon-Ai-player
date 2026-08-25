package experiment;

import ai.AiPlayer;
import ai.ExpectimaxAi;
import ai.HeuristicAi;
import ai.RandomAi;
import game.Board;
import game.Dice;
import game.Game;
import game.Move;
import game.MoveSequence;
import game.Player;
import game.PlayerType;
import statistics.DecisionRecorder;
import statistics.DecisionResult;
import statistics.GameStatistics;
import statistics.StatisticsRecorder;

public class ExperimentRunner
{
    private static final int EXPECTIMAX_SEARCH_DEPTH = 2;
    private static final int EXPECTIMAX_NODE_BUDGET = 10_000;

    private final DecisionRecorder decisionRecorder;
    private final StatisticsRecorder statisticsRecorder;

    public ExperimentRunner()
    {
        decisionRecorder = new DecisionRecorder();
        statisticsRecorder = new StatisticsRecorder();
    }

    public void runMatchup(PlayerType firstAi, PlayerType secondAi, int numberOfGames)
    {
        if (numberOfGames < 1)
        {
            throw new IllegalArgumentException("Number of games must be at least 1.");
        }

        validateAiType(firstAi);
        validateAiType(secondAi);

        statisticsRecorder.clear();

        for (int gameId = 1; gameId <= numberOfGames; gameId++)
        {
            PlayerType whitePlayerType;
            PlayerType blackPlayerType;

            if (gameId % 2 == 1)
            {
                whitePlayerType = firstAi;
                blackPlayerType = secondAi;
            }
            else
            {
                whitePlayerType = secondAi;
                blackPlayerType = firstAi;
            }

            GameStatistics gameStatistics = runGame(
                    gameId, whitePlayerType, blackPlayerType);

            statisticsRecorder.recordGame(gameStatistics);
        }
    }

    public GameStatistics runGame(int gameId, PlayerType whitePlayerType, PlayerType blackPlayerType)
    {
        validateAiType(whitePlayerType);
        validateAiType(blackPlayerType);

        Game game = new Game();
        Board board = game.getBoard();
        Dice dice = game.getDice();

        GameStatistics gameStatistics = new GameStatistics(gameId, whitePlayerType, blackPlayerType);

        while (game.getWinner() == Player.NONE)
        {
            Player player = game.getCurrentPlayer();

            PlayerType playerType = getPlayerType(player, whitePlayerType, blackPlayerType);

            AiPlayer aiPlayer = createAiPlayer(playerType);

            dice.roll();

            DecisionResult result = decisionRecorder.recordDecision(board, player, dice, aiPlayer, playerType);

            MoveSequence sequence = result.getMoveSequence();

            if (sequence.size() > 0)
            {
                gameStatistics.recordDecision(result.getStatistics());

                for (Move move : sequence.getMoves())
                {
                    board.applyMove(move);
                }
            }

            gameStatistics.incrementTurnCount();

            if (game.getWinner() == Player.NONE)
            {
                game.switchPlayer();
            }
        }

        gameStatistics.setWinner(game.getWinner());

        return gameStatistics;
    }

    public StatisticsRecorder getStatisticsRecorder()
    {
        return statisticsRecorder;
    }

    private PlayerType getPlayerType(Player player, PlayerType whitePlayerType,
                                     PlayerType blackPlayerType)
    {
        return player == Player.WHITE ? whitePlayerType : blackPlayerType;
    }

    private AiPlayer createAiPlayer(PlayerType playerType)
    {
        return switch (playerType)
        {
            case RANDOM_AI -> new RandomAi();
            case HEURISTIC_AI -> new HeuristicAi();
            case EXPECTIMAX_AI -> new ExpectimaxAi(
                    EXPECTIMAX_SEARCH_DEPTH, EXPECTIMAX_NODE_BUDGET);
            case HUMAN -> throw new IllegalArgumentException(
                    "Experiments require AI players.");
        };
    }

    private void validateAiType(PlayerType playerType)
    {
        if (playerType == PlayerType.HUMAN)
        {
            throw new IllegalArgumentException(
                    "Experiments require AI players.");
        }
    }
}