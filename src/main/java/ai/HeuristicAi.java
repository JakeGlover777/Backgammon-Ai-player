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

/**
 * Implements an AI player that selects moves using heuristic board evaluation.
 *
 * <p>Each legal move sequence is simulated and the resulting board state is
 * evaluated. The AI selects a sequence with the highest heuristic score,
 * choosing randomly when multiple sequences receive the same best score.
 */

public class HeuristicAi implements AiPlayer
{
    private final MoveGenerator moveGenerator;
    private final BoardEvaluator boardEvaluator;
    private final Random random;

    /**
     * Creates a Heuristic AI with a move generator, board evaluator, and random
     * number generator.
     */

    public HeuristicAi()
    {
        moveGenerator = new MoveGenerator();
        boardEvaluator = new BoardEvaluator();
        random = new Random();
    }

    /**
     * Selects a legal move sequence using heuristic board evaluation.
     *
     * <p>Each legal sequence is applied to a copy of the current board and the
     * resulting position is evaluated from the current player's perspective.
     * If multiple sequences receive the highest score, one is selected randomly.
     *
     * @param board the current board state
     * @param player the player making the move
     * @param dice the dice available for the turn
     * @return the highest-rated legal move sequence, or an empty sequence if no
     *         legal moves are available
     */

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
