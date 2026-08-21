package ai;

import game.Board;
import game.Player;
import game.Point;

public class BoardEvaluator
{
    private static final int BORNE_OFF_WEIGHT = 100;
    private static final int BAR_WEIGHT = 40;
    private static final int BLOT_WEIGHT = 8;

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

    private int countBlots(Board board, Player player)
    {
        int blots = 0;

        for (int i = 0; i < 24; i++)
        {
            Point point = board.getPoint(i);

            if (point.getOwner() == player && point.getCheckerCount() == 1)
            {
                blots++;
            }
        }

        return blots;
    }

    private int calculateProgress(Board board, Player player)
    {
        int progress = 0;

        for (int i = 0; i < 24; i++)
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
                progress += (24 - i) * point.getCheckerCount();
            }
        }

        return progress;
    }

    private Player getOpponent(Player player)
    {
        return player == Player.WHITE ? Player.BLACK : Player.WHITE;
    }
}