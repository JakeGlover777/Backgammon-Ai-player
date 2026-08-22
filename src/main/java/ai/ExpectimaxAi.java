package ai;

import game.Board;
import game.Dice;
import game.Move;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;

import java.util.List;

public class ExpectimaxAi implements AiPlayer
{
    private static final int DIE_SIDES = 6;
    private static final double DICE_OUTCOMES = DIE_SIDES * DIE_SIDES;

    private final MoveGenerator moveGenerator;
    private final BoardEvaluator boardEvaluator;

    public ExpectimaxAi()
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
            applySequence(simulatedBoard, sequence);

            double score = calculateExpectedOpponentScore(simulatedBoard, player);

            if (score > bestScore)
            {
                bestScore = score;
                bestSequence = sequence;
            }
        }

        return bestSequence;
    }

    private double calculateExpectedOpponentScore(Board board, Player originalPlayer)
    {
        Player opponent = getOpponent(originalPlayer);
        double totalScore = 0;

        for (int dieOne = 1; dieOne <= DIE_SIDES; dieOne++)
        {
            for (int dieTwo = 1; dieTwo <= DIE_SIDES; dieTwo++)
            {
                Dice dice = new Dice(dieOne, dieTwo);

                List<MoveSequence> opponentMoves =
                        moveGenerator.generateMoveSequences(board, opponent, dice);

                if (opponentMoves.isEmpty())
                {
                    totalScore += boardEvaluator.evaluate(board, originalPlayer);
                    continue;
                }

                double worstScore = Double.POSITIVE_INFINITY;

                for (MoveSequence opponentMove : opponentMoves)
                {
                    Board futureBoard = new Board(board);
                    applySequence(futureBoard, opponentMove);

                    double score = boardEvaluator.evaluate(futureBoard, originalPlayer);

                    if (score < worstScore)
                    {
                        worstScore = score;
                    }
                }

                totalScore += worstScore;
            }
        }

        return totalScore / DICE_OUTCOMES;
    }

    private void applySequence(Board board, MoveSequence sequence)
    {
        for (Move move : sequence.getMoves())
        {
            board.applyMove(move);
        }
    }

    private Player getOpponent(Player player)
    {
        if (player == Player.WHITE)
        {
            return Player.BLACK;
        }

        return Player.WHITE;
    }
}
