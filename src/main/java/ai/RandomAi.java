package ai;

import game.Board;
import game.Dice;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;

import java.util.List;
import java.util.Random;

/**
 * Implements an AI player that selects randomly from the available legal move
 * sequences.
 *
 * <p>The Random AI is used as a baseline for evaluating the performance of
 * the other AI approaches.
 */

public class RandomAi implements AiPlayer
{
    private final MoveGenerator moveGenerator;
    private final Random random;

    /**
     * Creates a Random AI with a move generator and random number generator.
     */

    public RandomAi()
    {
        moveGenerator = new MoveGenerator();
        random = new Random();
    }

    /**
     * Selects a random legal move sequence for the current game state.
     *
     * @param board the current board state
     * @param player the player making the move
     * @param dice the dice available for the turn
     * @return a randomly selected legal move sequence, or an empty sequence if
     *         no legal moves are available
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

        int randomIndex = random.nextInt(legalSequences.size());

        return legalSequences.get(randomIndex);
    }
}
