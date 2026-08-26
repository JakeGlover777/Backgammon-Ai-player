package experiment;

import game.Player;
import game.PlayerType;
import statistics.DecisionStatistics;
import statistics.GameStatistics;
import statistics.SearchStatistics;
import statistics.StatisticsRecorder;

public class ExperimentAnalyser
{
    private final StatisticsRecorder statisticsRecorder;

    public ExperimentAnalyser(StatisticsRecorder statisticsRecorder)
    {
        this.statisticsRecorder = statisticsRecorder;
    }

    public int getTotalGames()
    {
        return statisticsRecorder.getGameCount();
    }

    public int getWins(PlayerType playerType)
    {
        int wins = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            if (getWinnerType(game) == playerType)
            {
                wins++;
            }
        }

        return wins;
    }

    public double getWinRate(PlayerType playerType)
    {
        if (getTotalGames() == 0)
        {
            return 0;
        }

        return (double) getWins(playerType) / getTotalGames() * 100;
    }

    public int getWhiteWins()
    {
        int wins = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            if (game.getWinner() == Player.WHITE)
            {
                wins++;
            }
        }

        return wins;
    }

    public int getBlackWins()
    {
        int wins = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            if (game.getWinner() == Player.BLACK)
            {
                wins++;
            }
        }

        return wins;
    }

    public double getAverageTurnCount()
    {
        if (getTotalGames() == 0)
        {
            return 0;
        }

        long totalTurns = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            totalTurns += game.getTurnCount();
        }

        return (double) totalTurns / getTotalGames();
    }

    public int getDecisionCount(PlayerType playerType)
    {
        int count = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            for (DecisionStatistics decision : game.getDecisions())
            {
                if (decision.getAiType() == playerType)
                {
                    count++;
                }
            }
        }

        return count;
    }

    public double getAverageDecisionTimeMilliseconds(PlayerType playerType)
    {
        long totalNanoseconds = 0;
        int decisionCount = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            for (DecisionStatistics decision : game.getDecisions())
            {
                if (decision.getAiType() == playerType)
                {
                    totalNanoseconds += decision.getDecisionTimeNanoseconds();
                    decisionCount++;
                }
            }
        }

        if (decisionCount == 0)
        {
            return 0;
        }

        return totalNanoseconds / 1_000_000.0 / decisionCount;
    }

    public double getAverageLegalSequenceCount(PlayerType playerType)
    {
        long totalLegalSequences = 0;
        int decisionCount = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            for (DecisionStatistics decision : game.getDecisions())
            {
                if (decision.getAiType() == playerType)
                {
                    totalLegalSequences += decision.getLegalSequenceCount();
                    decisionCount++;
                }
            }
        }

        if (decisionCount == 0)
        {
            return 0;
        }

        return (double) totalLegalSequences / decisionCount;
    }

    public double getAverageNodesEvaluated()
    {
        long totalNodes = 0;
        int searchCount = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            for (DecisionStatistics decision : game.getDecisions())
            {
                SearchStatistics searchStatistics = decision.getSearchStatistics();

                if (searchStatistics != null)
                {
                    totalNodes += searchStatistics.getNodesEvaluated();
                    searchCount++;
                }
            }
        }

        if (searchCount == 0)
        {
            return 0;
        }

        return (double) totalNodes / searchCount;
    }

    public int getBudgetReachedCount()
    {
        int count = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            for (DecisionStatistics decision : game.getDecisions())
            {
                SearchStatistics searchStatistics = decision.getSearchStatistics();

                if (searchStatistics != null && searchStatistics.isBudgetReached())
                {
                    count++;
                }
            }
        }

        return count;
    }

    public double getBudgetReachedPercentage()
    {
        int searchCount = getExpectimaxDecisionCount();

        if (searchCount == 0)
        {
            return 0;
        }

        return (double) getBudgetReachedCount() / searchCount * 100;
    }

    private int getExpectimaxDecisionCount()
    {
        int count = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            for (DecisionStatistics decision : game.getDecisions())
            {
                if (decision.getSearchStatistics() != null)
                {
                    count++;
                }
            }
        }

        return count;
    }

    private PlayerType getWinnerType(GameStatistics game)
    {
        if (game.getWinner() == Player.WHITE)
        {
            return game.getWhitePlayerType();
        }

        if (game.getWinner() == Player.BLACK)
        {
            return game.getBlackPlayerType();
        }

        return null;
    }
}
