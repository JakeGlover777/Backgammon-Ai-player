package game;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates legal moves and move sequences for a Backgammon position.
 *
 * <p>The generator accounts for bar entry, bearing off, doubles, both possible
 * dice orders, the requirement to use the maximum possible number of dice, and
 * the higher-die rule when only one of two different dice can be played.
 */

public class MoveGenerator
{
    private static final int BOARD_SIZE = 24;
    private static final int MAX_DOUBLE_MOVES = 4;
    private static final int WHITE_HOME_START = 18;
    private static final int BLACK_HOME_END = 5;

    /**
     * Generates the individual legal moves available to a player for the supplied
     * dice values.
     *
     * <p>If the player has checkers on the bar, only legal bar-entry moves are
     * returned.
     *
     * @param board the current board state
     * @param player the player making the move
     * @param dice the dice available for the turn
     * @return the available legal moves
     */

    public List<Move> generateMoves(Board board, Player player, Dice dice)
    {
        List<Move> moves = new ArrayList<>();

        if (board.getBarCount(player) > 0)
        {
            generateBarEntryMove(moves, board, player, dice.getDieOne());

            if (dice.getDieTwo() != dice.getDieOne())
            {
                generateBarEntryMove(moves, board, player, dice.getDieTwo());
            }

            return moves;
        }

        int direction;

        if (player == Player.WHITE)
        {
            direction = 1;
        }
        else
        {
            direction = -1;
        }

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            Point point = board.getPoint(i);

            if (point.getOwner() != player)
            {
                continue;
            }

            int destinationOne = i + (dice.getDieOne() * direction);
            int destinationTwo = i + (dice.getDieTwo() * direction);

            if (isOnBoard(destinationOne))
            {
                Point destination = board.getPoint(destinationOne);

                if (isLegalDestination(destination, player))
                {
                    moves.add(new Move(player, i, destinationOne, dice.getDieOne()));
                }
            }
            else if (canBearOff(board, player, i, dice.getDieOne()))
            {
                moves.add(new Move(player, i, dice.getDieOne()));
            }

            if (dice.getDieTwo() != dice.getDieOne())
            {
                if (isOnBoard(destinationTwo))
                {
                    Point destination = board.getPoint(destinationTwo);

                    if (isLegalDestination(destination, player))
                    {
                        moves.add(new Move(player, i, destinationTwo, dice.getDieTwo()));
                    }
                }
                else if (canBearOff(board, player, i, dice.getDieTwo()))
                {
                    moves.add(new Move(player, i, dice.getDieTwo()));
                }
            }
        }

        return moves;
    }

    /**
     * Generates the legal move sequences available to a player for the supplied
     * dice values.
     *
     * <p>For different dice values, both possible dice orders are considered. For
     * doubles, up to four moves using the repeated die value are generated. Only
     * sequences using the maximum possible number of dice are retained. If only
     * one of two different dice can be played, the higher die is used when
     * possible.
     *
     * @param board the current board state
     * @param player the player making the move
     * @param dice the dice available for the turn
     * @return the legal move sequences available to the player
     */

    public List<MoveSequence> generateMoveSequences(Board board, Player player, Dice dice)
    {
        List<MoveSequence> sequences = new ArrayList<>();

        int dieOne = dice.getDieOne();
        int dieTwo = dice.getDieTwo();

        if (dieOne == dieTwo)
        {
            generateDoubleSequences(sequences, board, player, dieOne, new MoveSequence(),
                    MAX_DOUBLE_MOVES);
        }
        else
        {
            generateSequencesForOrder(sequences, board, player, dieOne, dieTwo);
            generateSequencesForOrder(sequences, board, player, dieTwo, dieOne);
        }

        int maxMoves = 0;

        for (MoveSequence sequence : sequences)
        {
            if (sequence.size() > maxMoves)
            {
                maxMoves = sequence.size();
            }
        }

        int finalMaxMoves = maxMoves;
        sequences.removeIf(sequence -> sequence.size() < finalMaxMoves);

        if (maxMoves == 1 && dieOne != dieTwo)
        {
            int higherDie = Math.max(dieOne, dieTwo);
            boolean higherDieCanBeUsed = false;

            for (MoveSequence sequence : sequences)
            {
                if (sequence.getMoves().getFirst().getDieValue() == higherDie)
                {
                    higherDieCanBeUsed = true;
                    break;
                }
            }

            if (higherDieCanBeUsed)
            {
                sequences.removeIf(sequence ->
                        sequence.getMoves().getFirst().getDieValue() != higherDie);
            }
        }

        return sequences;
    }

    /**
     * Generates move sequences for a specified ordering of two different dice.
     *
     * @param sequences the collection receiving generated sequences
     * @param board the current board state
     * @param player the player making the moves
     * @param firstDie the die value to use first
     * @param secondDie the die value to use second
     */

    private void generateSequencesForOrder(List<MoveSequence> sequences, Board board,
                                           Player player, int firstDie, int secondDie)
    {
        List<Move> firstMoves = generateMovesForDie(board, player, firstDie);

        for (Move firstMove : firstMoves)
        {
            Board copiedBoard = new Board(board);
            copiedBoard.applyMove(firstMove);

            List<Move> secondMoves = generateMovesForDie(copiedBoard, player, secondDie);

            if (secondMoves.isEmpty())
            {
                MoveSequence sequence = new MoveSequence();
                sequence.addMove(firstMove);
                sequences.add(sequence);
            }
            else
            {
                for (Move secondMove : secondMoves)
                {
                    MoveSequence sequence = new MoveSequence();
                    sequence.addMove(firstMove);
                    sequence.addMove(secondMove);
                    sequences.add(sequence);
                }
            }
        }
    }

    /**
     * Recursively generates legal move sequences for a double roll.
     *
     * @param sequences the collection receiving generated sequences
     * @param board the current board state
     * @param player the player making the moves
     * @param dieValue the repeated die value
     * @param currentSequence the sequence generated so far
     * @param movesRemaining the maximum number of additional moves to generate
     */

    private void generateDoubleSequences(List<MoveSequence> sequences, Board board,
                                         Player player, int dieValue, MoveSequence currentSequence,
                                         int movesRemaining)
    {
        if (movesRemaining == 0)
        {
            sequences.add(currentSequence);
            return;
        }

        List<Move> legalMoves = generateMovesForDie(board, player, dieValue);

        if (legalMoves.isEmpty())
        {
            if (!currentSequence.isEmpty())
            {
                sequences.add(currentSequence);
            }

            return;
        }

        for (Move move : legalMoves)
        {
            Board copiedBoard = new Board(board);
            copiedBoard.applyMove(move);

            MoveSequence copiedSequence = new MoveSequence(currentSequence);
            copiedSequence.addMove(move);

            generateDoubleSequences(sequences, copiedBoard, player, dieValue, copiedSequence,
                    movesRemaining - 1);
        }
    }

    /**
     * Generates the legal moves available for a single die value.
     *
     * @param board the current board state
     * @param player the player making the move
     * @param dieValue the die value to use
     * @return the legal moves available for the die value
     */

    private List<Move> generateMovesForDie(Board board, Player player, int dieValue)
    {
        List<Move> moves = new ArrayList<>();

        if (board.getBarCount(player) > 0)
        {
            generateBarEntryMove(moves, board, player, dieValue);
            return moves;
        }

        int direction;

        if (player == Player.WHITE)
        {
            direction = 1;
        }
        else
        {
            direction = -1;
        }

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            Point point = board.getPoint(i);

            if (point.getOwner() != player)
            {
                continue;
            }

            int destination = i + (dieValue * direction);

            if (isOnBoard(destination))
            {
                Point destinationPoint = board.getPoint(destination);

                if (isLegalDestination(destinationPoint, player))
                {
                    moves.add(new Move(player, i, destination, dieValue));
                }
            }
            else if (canBearOff(board, player, i, dieValue))
            {
                moves.add(new Move(player, i, dieValue));
            }
        }

        return moves;
    }

    /**
     * Adds a legal bar-entry move for the supplied die value when one is
     * available.
     *
     * @param moves the collection receiving the move
     * @param board the current board state
     * @param player the player entering from the bar
     * @param dieValue the die value used for entry
     */

    private void generateBarEntryMove(List<Move> moves, Board board, Player player, int dieValue)
    {
        int destination;

        if (player == Player.WHITE)
        {
            destination = dieValue - 1;
        }
        else
        {
            destination = BOARD_SIZE - dieValue;
        }

        Point destinationPoint = board.getPoint(destination);

        if (isLegalDestination(destinationPoint, player))
        {
            moves.add(new Move(player, destination, dieValue, true));
        }
    }

    /**
     * Determines whether a point can legally receive a checker.
     *
     * <p>A destination is legal if it is empty, owned by the moving player, or
     * contains exactly one opposing checker.
     *
     * @param destination the destination point
     * @param player the player making the move
     * @return true if the destination can be moved onto, otherwise false
     */

    private boolean isLegalDestination(Point destination, Player player)
    {
        return destination.isEmpty() || destination.getOwner() == player
                || (destination.getOwner() != player && destination.getCheckerCount() == 1);
    }

    /**
     * Determines whether a checker can be borne off using the supplied die value.
     *
     * <p>Bearing off is only permitted when all of the player's remaining
     * checkers are in the home board. A checker may bear off using the exact die
     * value or, where permitted, a higher die when there are no checkers on a
     * higher point.
     *
     * @param board the current board state
     * @param player the player bearing off
     * @param fromPoint the point containing the checker
     * @param dieValue the die value being used
     * @return true if the checker can legally be borne off, otherwise false
     */

    private boolean canBearOff(Board board, Player player, int fromPoint, int dieValue)
    {
        if (!board.allCheckersInHomeBoard(player))
        {
            return false;
        }

        int exactDieRequired;

        if (player == Player.WHITE)
        {
            exactDieRequired = BOARD_SIZE - fromPoint;

            if (dieValue == exactDieRequired)
            {
                return true;
            }

            if (dieValue > exactDieRequired)
            {
                for (int i = WHITE_HOME_START; i < fromPoint; i++)
                {
                    if (board.getPoint(i).getOwner() == player)
                    {
                        return false;
                    }
                }

                return true;
            }
        }
        else
        {
            exactDieRequired = fromPoint + 1;

            if (dieValue == exactDieRequired)
            {
                return true;
            }

            if (dieValue > exactDieRequired)
            {
                for (int i = BLACK_HOME_END; i > fromPoint; i--)
                {
                    if (board.getPoint(i).getOwner() == player)
                    {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    private boolean isOnBoard(int index)
    {
        return index >= 0 && index < BOARD_SIZE;
    }
}
