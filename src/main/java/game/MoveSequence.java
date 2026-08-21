package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoveSequence
{
    private final List<Move> moves;

    public MoveSequence()
    {
        moves = new ArrayList<>();
    }

    public MoveSequence(MoveSequence other)
    {
        moves = new ArrayList<>(other.moves);
    }

    public void addMove(Move move)
    {
        moves.add(move);
    }

    public List<Move> getMoves()
    {
        return Collections.unmodifiableList(moves);
    }

    public int size()
    {
        return moves.size();
    }

    public boolean isEmpty()
    {
        return moves.isEmpty();
    }

    @Override
    public String toString()
    {
        return moves.toString();
    }
}
