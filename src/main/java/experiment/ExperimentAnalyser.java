package experiment;

import game.Player;
import game.PlayerType;
import statistics.DecisionStatistics;
import statistics.GameStatistics;
import statistics.SearchStatistics;
import statistics.StatisticsRecorder;

/**
 * Analyses statistics collected from automated Backgammon experiments.
 *
 * <p>The analyser provides game-level, decision-level, colour, starting-player,
 * and Expectimax search measurements derived from recorded experiment data.
 */

public class ExperimentAnalyser
{
    private static final int PERCENTAGE = 100;
    private static final double NANOSECONDS_PER_MILLISECOND = 1_000_000.0;

    private final StatisticsRecorder statisticsRecorder;

    /**
     * Creates an experiment analyser for the supplied recorded statistics.
     *
     * @param statisticsRecorder the experiment statistics to analyse
     */

    public ExperimentAnalyser(StatisticsRecorder statisticsRecorder)
    {
        this.statisticsRecorder = statisticsRecorder;
    }

    /**
     * Returns the total number of recorded games.
     *
     * @return the number of recorded games
     */

    public int getTotalGames()
    {
        return statisticsRecorder.getGameCount();
    }

    /**
     * Returns the number of games that finished without a recorded winner.
     *
     * @return the number of incomplete games
     */

    public int getIncompleteGames()
    {
        int count = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            if (game.getWinner() == Player.NONE)
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns the number of games won by the specified player type.
     *
     * @param playerType the player type whose wins are counted
     * @return the number of games won by the player type
     */

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

    /**
     * Calculates the win rate of the specified player type across completed games.
     *
     * @param aiType the player type whose win rate is calculated
     * @return the win rate as a percentage, or zero if no games were completed
     */

    public double getWinRate(PlayerType aiType)
    {
        int completedGames = getTotalGames() - getIncompleteGames();

        if (completedGames == 0)
        {
            return 0;
        }

        return (double) getWins(aiType) / completedGames * PERCENTAGE;
    }

    /**
     * Returns the number of games won by White.
     *
     * @return the number of White wins
     */

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

    /**
     * Returns the number of games won by Black.
     *
     * @return the number of Black wins
     */

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

    /**
     * Returns the number of games in which White was the starting player.
     *
     * @return the number of games started by White
     */

    public int getWhiteStarts()
    {
        int count = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            if (game.getStartingPlayer() == Player.WHITE)
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns the number of games in which Black was the starting player.
     *
     * @return the number of games started by Black
     */

    public int getBlackStarts()
    {
        int count = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            if (game.getStartingPlayer() == Player.BLACK)
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns the number of games in which the specified player both started and
     * won the game.
     *
     * @param player the player whose starting wins are counted
     * @return the number of games started and won by the player
     */

    public int getWinsWhenStarting(Player player)
    {
        int wins = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            if (game.getStartingPlayer() == player && game.getWinner() == player)
            {
                wins++;
            }
        }

        return wins;
    }

    /**
     * Calculates the win rate of the specified player in games where that player
     * started.
     *
     * @param player the player whose starting win rate is calculated
     * @return the starting-player win rate as a percentage, or zero if the player
     *         did not start any games
     * @throws IllegalArgumentException if the player is not WHITE or BLACK
     */

    public double getStartingPlayerWinRate(Player player)
    {
        int starts;

        if (player == Player.WHITE)
        {
            starts = getWhiteStarts();
        }
        else if (player == Player.BLACK)
        {
            starts = getBlackStarts();
        }
        else
        {
            throw new IllegalArgumentException("Player must be WHITE or BLACK.");
        }

        if (starts == 0)
        {
            return 0;
        }

        return (double) getWinsWhenStarting(player) / starts * PERCENTAGE;
    }

    /**
     * Calculates the average number of turns across all recorded games.
     *
     * @return the average turn count, or zero if no games are recorded
     */

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

    /**
     * Returns the number of recorded decisions made by the specified player type.
     *
     * @param playerType the player type whose decisions are counted
     * @return the number of recorded decisions
     */

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

    /**
     * Calculates the average decision time for the specified player type.
     *
     * @param playerType the player type whose decision time is calculated
     * @return the average decision time in milliseconds, or zero if no decisions
     *         are recorded for the player type
     */

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

        return totalNanoseconds / NANOSECONDS_PER_MILLISECOND / decisionCount;
    }

    /**
     * Calculates the average number of legal move sequences available to the
     * specified player type when making recorded decisions.
     *
     * @param playerType the player type whose decisions are analysed
     * @return the average number of legal move sequences, or zero if no decisions
     *         are recorded for the player type
     */

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

    /**
     * Calculates the average number of decision nodes evaluated across recorded
     * Expectimax decisions.
     *
     * @return the average number of evaluated decision nodes, or zero if no
     *         search statistics are recorded
     */

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

    /**
     * Returns the number of recorded Expectimax decisions in which the available
     * node budget limited the search.
     *
     * @return the number of budget-limited decisions
     */

    public int getBudgetReachedCount()
    {
        int count = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            for (DecisionStatistics decision : game.getDecisions())
            {
                SearchStatistics searchStatistics =
                        decision.getSearchStatistics();

                if (searchStatistics != null
                        && searchStatistics.isBudgetReached())
                {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Calculates the percentage of recorded Expectimax decisions in which the
     * available node budget limited the search.
     *
     * @return the percentage of budget-limited Expectimax decisions, or zero if
     *         no Expectimax decisions are recorded
     */

    public double getBudgetReachedPercentage()
    {
        int searchCount = getExpectimaxDecisionCount();

        if (searchCount == 0)
        {
            return 0;
        }

        return (double) getBudgetReachedCount() / searchCount * PERCENTAGE;
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

    /**
     * Returns the number of games matching the specified starting player and
     * winner.
     *
     * @param startingPlayer the player that started the game
     * @param winner the player that won the game
     * @return the number of matching games
     */

    public int getWinsByStarterAndWinner(Player startingPlayer, Player winner)
    {
        int count = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            if (game.getStartingPlayer() == startingPlayer && game.getWinner() == winner)
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Calculates White's win rate across completed games.
     *
     * @return White's win rate as a percentage, or zero if no games were completed
     */

    public double getWhiteWinRate()
    {
        int completedGames = getTotalGames() - getIncompleteGames();

        if (completedGames == 0)
        {
            return 0;
        }

        return (double) getWhiteWins() / completedGames * PERCENTAGE;
    }

    /**
     * Calculates Black's win rate across completed games.
     *
     * @return Black's win rate as a percentage, or zero if no games were completed
     */

    public double getBlackWinRate()
    {
        int completedGames = getTotalGames() - getIncompleteGames();

        if (completedGames == 0)
        {
            return 0;
        }

        return (double) getBlackWins() / completedGames * PERCENTAGE;
    }

    /**
     * Calculates the percentage of completed games won by the player that started
     * the game.
     *
     * @return the overall starting-player win rate as a percentage, or zero if no
     *         games were completed
     */

    public double getOverallStarterWinRate()
    {
        int completedGames = getTotalGames() - getIncompleteGames();

        if (completedGames == 0)
        {
            return 0;
        }

        int starterWins = 0;

        for (GameStatistics game : statisticsRecorder.getGames())
        {
            if (game.getWinner() != Player.NONE && game.getStartingPlayer() == game.getWinner())
            {
                starterWins++;
            }
        }

        return (double) starterWins / completedGames * PERCENTAGE;
    }
}
