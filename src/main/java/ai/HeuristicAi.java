package ai;

import game.Board;
import game.Dice;
import game.Move;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;
import game.Point;

import java.util.List;

public class HeuristicAi implements AiPlayer
{
    private final MoveGenerator moveGenerator;

    public HeuristicAi()
    {
        moveGenerator = new MoveGenerator();
    }

    @Override
    public MoveSequence chooseMove(
            Board board,
            Player player,
            Dice dice)
    {
        List<MoveSequence> legalSequences =
                moveGenerator.generateMoveSequences(
                        board,
                        player,
                        dice
                );

        if (legalSequences.isEmpty())
        {
            return new MoveSequence();
        }

        MoveSequence bestSequence = legalSequences.get(0);
        double bestScore = Double.NEGATIVE_INFINITY;

        for (MoveSequence sequence : legalSequences)
        {
            Board simulatedBoard = new Board(board);

            for (Move move : sequence.getMoves())
            {
                simulatedBoard.applyMove(move);
            }

            double score = evaluateBoard(
                    simulatedBoard,
                    player
            );

            if (score > bestScore)
            {
                bestScore = score;
                bestSequence = sequence;
            }
        }

        return bestSequence;
    }

    private double evaluateBoard(
            Board board,
            Player player)
    {
        Player opponent =
                player == Player.WHITE
                        ? Player.BLACK
                        : Player.WHITE;

        double score = 0;

        score += board.getBorneOffCount(player) * 100;
        score -= board.getBorneOffCount(opponent) * 100;

        score -= board.getBarCount(player) * 40;
        score += board.getBarCount(opponent) * 40;

        score -= countBlots(board, player) * 8;
        score += countBlots(board, opponent) * 8;

        score += calculateProgress(board, player);
        score -= calculateProgress(board, opponent);

        return score;
    }

    private int countBlots(
            Board board,
            Player player)
    {
        int blots = 0;

        for (int i = 0; i < 24; i++)
        {
            Point point = board.getPoint(i);

            if (point.getOwner() == player
                    && point.getCheckerCount() == 1)
            {
                blots++;
            }
        }

        return blots;
    }

    private int calculateProgress(
            Board board,
            Player player)
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
                progress +=
                        (i + 1) * point.getCheckerCount();
            }
            else
            {
                progress +=
                        (24 - i) * point.getCheckerCount();
            }
        }

        return progress;
    }
}