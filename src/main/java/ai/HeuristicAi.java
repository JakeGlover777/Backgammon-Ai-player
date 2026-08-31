package ai;

import game.Board;
import game.Dice;
import game.Move;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeuristicAi implements AiPlayer
{
    private final MoveGenerator moveGenerator;
    private final BoardEvaluator boardEvaluator;
    private final Random random;

    public HeuristicAi()
    {
        moveGenerator = new MoveGenerator();
        boardEvaluator = new BoardEvaluator();
        random = new Random();
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

        List<MoveSequence> bestSequences = new ArrayList<>();
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
                bestSequences.clear();
                bestSequences.add(sequence);
            }
            else if (Double.compare(score, bestScore) == 0)
            {
                bestSequences.add(sequence);
            }
        }

        return bestSequences.get(
                random.nextInt(bestSequences.size()));
    }
}
