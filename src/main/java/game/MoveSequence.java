package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an ordered sequence of moves made during a Backgammon turn.
 *
 * <p>A sequence may contain multiple moves when more than one die can be
 * played during the turn.
 */

public class MoveSequence
{
    private final List<Move> moves;

    /**
     * Creates an empty move sequence.
     */

    public MoveSequence()
    {
        moves = new ArrayList<>();
    }

    /**
     * Creates a copy of another move sequence.
     *
     * @param other the move sequence to copy
     */

    public MoveSequence(MoveSequence other)
    {
        moves = new ArrayList<>(other.moves);
    }

    /**
     * Adds a move to the end of this sequence.
     *
     * @param move the move to add
     */

    public void addMove(Move move)
    {
        moves.add(move);
    }

    /**
     * Returns an unmodifiable view of the moves in this sequence.
     *
     * @return the moves in this sequence
     */

    public List<Move> getMoves()
    {
        return Collections.unmodifiableList(moves);
    }

    /**
     * Returns the number of moves in this sequence.
     *
     * @return the number of moves
     */

    public int size()
    {
        return moves.size();
    }

    /**
     * Determines whether this sequence contains no moves.
     *
     * @return true if the sequence is empty, otherwise false
     */

    public boolean isEmpty()
    {
        return moves.isEmpty();
    }

    /**
     * Returns a textual representation of this move sequence.
     *
     * @return the move sequence as a string
     */

    @Override
    public String toString()
    {
        return moves.toString();
    }
}
