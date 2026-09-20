package ai;

import game.Board;
import game.Player;
import game.Point;

/**
 * Evaluates a Backgammon board state from the perspective of a specified
 * player.
 *
 * <p>The evaluation considers borne-off checkers, checkers on the bar,
 * exposed blots, and checker progress. The player's score is evaluated
 * relative to the opponent.
 */


public class BoardEvaluator
{
    private static final int BOARD_SIZE = 24;
    private static final int BORNE_OFF_WEIGHT = 100;
    private static final int BAR_WEIGHT = 40;
    private static final int BLOT_WEIGHT = 8;

    /**
     * Calculates a heuristic score for the current board state from the
     * perspective of the specified player.
     *
     * <p>Higher scores represent board states that are more favourable to the
     * specified player.
     *
     * @param board the board state to evaluate
     * @param player the player whose perspective is used for the evaluation
     * @return the heuristic score of the board state
     */


    public double evaluate(Board board, Player player)
    {
        Player opponent = getOpponent(player);

        double score = 0;

        score += board.getBorneOffCount(player) * BORNE_OFF_WEIGHT;
        score -= board.getBorneOffCount(opponent) * BORNE_OFF_WEIGHT;

        score -= board.getBarCount(player) * BAR_WEIGHT;
        score += board.getBarCount(opponent) * BAR_WEIGHT;

        score -= countBlots(board, player) * BLOT_WEIGHT;
        score += countBlots(board, opponent) * BLOT_WEIGHT;

        score += calculateProgress(board, player);
        score -= calculateProgress(board, opponent);

        return score;
    }

    /**
     * Counts the number of exposed blots belonging to the specified player.
     *
     * <p>A blot is a point containing exactly one checker belonging to the
     * player.
     *
     * @param board the board state to examine
     * @param player the player whose blots are counted
     * @return the number of blots belonging to the player
     */


    private int countBlots(Board board, Player player)
    {
        int blots = 0;

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            Point point = board.getPoint(i);

            if (point.getOwner() == player && point.getCheckerCount() == 1)
            {
                blots++;
            }
        }

        return blots;
    }

    /**
     * Calculates the positional progress of the specified player's checkers
     * across the board.
     *
     * <p>Checkers positioned further in the player's direction of travel
     * contribute a higher value.
     *
     * @param board the board state to examine
     * @param player the player whose progress is calculated
     * @return the total positional progress value
     */


    private int calculateProgress(Board board, Player player)
    {
        int progress = 0;

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            Point point = board.getPoint(i);

            if (point.getOwner() != player)
            {
                continue;
            }

            if (player == Player.WHITE)
            {
                progress += (i + 1) * point.getCheckerCount();
            }
            else
            {
                progress += (BOARD_SIZE - i) * point.getCheckerCount();
            }
        }

        return progress;
    }

    private Player getOpponent(Player player)
    {
        return player == Player.WHITE ? Player.BLACK : Player.WHITE;
    }
}
