package ai;

import game.Board;
import game.Dice;
import game.MoveSequence;
import game.Player;

/**
 * Defines the common interface for Backgammon AI players.
 *
 * <p>Each AI implementation selects a move sequence using the current board
 * state, player, and dice values.
 */

public interface AiPlayer
{
    /**
     * Selects a move sequence for the current game state.
     *
     * @param board the current board state
     * @param player the player making the move
     * @param dice the dice available for the turn
     * @return the move sequence selected by the AI
     */

    MoveSequence chooseMove(Board board, Player player, Dice dice);
}
