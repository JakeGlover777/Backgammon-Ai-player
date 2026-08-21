package game;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator
{
    private static final int BOARD_SIZE = 24;
    private static final int MAX_DOUBLE_MOVES = 4;
    private static final int WHITE_HOME_START = 18;
    private static final int BLACK_HOME_END = 5;

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

    public List<MoveSequence> generateMoveSequences(Board board, Player player, Dice dice)
    {
        List<MoveSequence> sequences = new ArrayList<>();

        int dieOne = dice.getDieOne();
        int dieTwo = dice.getDieTwo();

        if (dieOne == dieTwo)
        {
            generateDoubleSequences(sequences, board, player, dieOne,
                    new MoveSequence(),
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

            generateDoubleSequences(sequences, copiedBoard, player, dieValue,
                    copiedSequence, movesRemaining - 1);
        }
    }

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

    private boolean isLegalDestination(Point destination, Player player)
    {
        return destination.isEmpty()
                || destination.getOwner() == player
                || (destination.getOwner() != player && destination.getCheckerCount() == 1);
    }

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
