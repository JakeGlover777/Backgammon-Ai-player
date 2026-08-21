package ai;

import game.Board;
import game.Dice;
import game.Move;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;

import java.util.List;

public class HeuristicAi implements AiPlayer
{
    private final MoveGenerator moveGenerator;
    private final BoardEvaluator boardEvaluator;

    public HeuristicAi()
    {
        moveGenerator = new MoveGenerator();
        boardEvaluator = new BoardEvaluator();
    }

    @Override
    public MoveSequence chooseMove(Board board, Player player, Dice dice)
    {
        List<MoveSequence> legalSequences =
                moveGenerator.generateMoveSequences(board, player, dice);

        if (legalSequences.isEmpty())
        {
            return new MoveSequence();
        }

        MoveSequence bestSequence = legalSequences.getFirst();
        double bestScore = Double.NEGATIVE_INFINITY;

        for (MoveSequence sequence : legalSequences)
        {
            Board simulatedBoard = new Board(board);

            for (Move move : sequence.getMoves())
            {
                simulatedBoard.applyMove(move);
            }

            double score = boardEvaluator.evaluate(simulatedBoard, player);

            if (score > bestScore)
            {
                bestScore = score;
                bestSequence = sequence;
            }
        }

        return bestSequence;
    }
}