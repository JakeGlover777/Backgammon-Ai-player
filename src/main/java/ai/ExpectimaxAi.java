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
    private static final int DEFAULT_SEARCH_DEPTH = 1;
    private static final int MINIMUM_SEARCH_DEPTH = 1;
    private static final int DEFAULT_NODE_BUDGET = 10_000;
    private static final int MINIMUM_NODE_BUDGET = 1;
    private static final double DICE_OUTCOMES = DIE_SIDES * DIE_SIDES;

    private final MoveGenerator moveGenerator;
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private final int nodeBudget;

    private int nodesEvaluated;
    private boolean budgetReached;

    public ExpectimaxAi()
    {
        this(DEFAULT_SEARCH_DEPTH, DEFAULT_NODE_BUDGET);
    }

    public ExpectimaxAi(int searchDepth)
    {
        this(searchDepth, DEFAULT_NODE_BUDGET);
    }

    public ExpectimaxAi(int searchDepth, int nodeBudget)
    {
        if (searchDepth < MINIMUM_SEARCH_DEPTH)
        {
            throw new IllegalArgumentException("Search depth must be at least 1.");
        }

        if (nodeBudget < MINIMUM_NODE_BUDGET)
        {
            throw new IllegalArgumentException("Node budget must be at least 1.");
        }

        moveGenerator = new MoveGenerator();
        boardEvaluator = new BoardEvaluator();

        this.searchDepth = searchDepth;
        this.nodeBudget = nodeBudget;
    }

    @Override
    public MoveSequence chooseMove(Board board, Player player, Dice dice)
    {
        nodesEvaluated = 0;
        budgetReached = false;

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

            Player opponent = getOpponent(player);
            double score = calculateExpectedScore(simulatedBoard, opponent, player, searchDepth);

            if (score > bestScore)
            {
                bestScore = score;
                bestSequence = sequence;
            }

            if (budgetReached)
            {
                break;
            }
        }

        return bestSequence;
    }

    public int getNodesEvaluated()
    {
        return nodesEvaluated;
    }

    public int getSearchDepth()
    {
        return searchDepth;
    }

    public int getNodeBudget()
    {
        return nodeBudget;
    }

    public boolean wasBudgetReached()
    {
        return budgetReached;
    }

    private double calculateExpectedScore(Board board, Player currentPlayer, Player originalPlayer,
                                          int depthRemaining)
    {
        if (shouldStopSearch(depthRemaining))
        {
            return evaluateBoard(board, originalPlayer);
        }

        double totalScore = 0;
        int outcomesEvaluated = 0;

        for (int dieOne = 1; dieOne <= DIE_SIDES; dieOne++)
        {
            for (int dieTwo = 1; dieTwo <= DIE_SIDES; dieTwo++)
            {
                if (hasReachedNodeBudget())
                {
                    budgetReached = true;
                    break;
                }

                Dice dice = new Dice(dieOne, dieTwo);

                totalScore += calculateDecisionScore(board, currentPlayer, originalPlayer,
                        dice, depthRemaining);

                outcomesEvaluated++;
            }

            if (budgetReached)
            {
                break;
            }
        }

        if (outcomesEvaluated == 0)
        {
            return evaluateBoard(board, originalPlayer);
        }

        return totalScore / outcomesEvaluated;
    }

    private double calculateDecisionScore(Board board, Player currentPlayer, Player originalPlayer,
                                          Dice dice, int depthRemaining)
    {
        if (hasReachedNodeBudget())
        {
            budgetReached = true;
            return evaluateBoard(board, originalPlayer);
        }

        nodesEvaluated++;

        List<MoveSequence> legalSequences =
                moveGenerator.generateMoveSequences(board, currentPlayer, dice);

        if (legalSequences.isEmpty())
        {
            return calculateExpectedScore(board, getOpponent(currentPlayer), originalPlayer,
                    depthRemaining - 1);
        }

        if (currentPlayer == originalPlayer)
        {
            return findMaximumScore(board, currentPlayer, originalPlayer, legalSequences,
                    depthRemaining);
        }

        return findMinimumScore(board, currentPlayer, originalPlayer, legalSequences,
                depthRemaining);
    }

    private double findMaximumScore(Board board, Player currentPlayer,
                                    Player originalPlayer, List<MoveSequence> legalSequences, int depthRemaining)
    {
        double bestScore = Double.NEGATIVE_INFINITY;

        for (MoveSequence sequence : legalSequences)
        {
            if (hasReachedNodeBudget())
            {
                budgetReached = true;
                break;
            }

            Board futureBoard = new Board(board);
            applySequence(futureBoard, sequence);

            double score = calculateExpectedScore(futureBoard, getOpponent(currentPlayer),
                    originalPlayer, depthRemaining - 1);

            if (score > bestScore)
            {
                bestScore = score;
            }
        }

        if (bestScore == Double.NEGATIVE_INFINITY)
        {
            return evaluateBoard(board, originalPlayer);
        }

        return bestScore;
    }

    private double findMinimumScore(Board board, Player currentPlayer, Player originalPlayer,
                                    List<MoveSequence> legalSequences, int depthRemaining)
    {
        double worstScore = Double.POSITIVE_INFINITY;

        for (MoveSequence sequence : legalSequences)
        {
            if (hasReachedNodeBudget())
            {
                budgetReached = true;
                break;
            }

            Board futureBoard = new Board(board);
            applySequence(futureBoard, sequence);

            double score = calculateExpectedScore(futureBoard, getOpponent(currentPlayer),
                    originalPlayer, depthRemaining - 1);

            if (score < worstScore)
            {
                worstScore = score;
            }
        }

        if (worstScore == Double.POSITIVE_INFINITY)
        {
            return evaluateBoard(board, originalPlayer);
        }

        return worstScore;
    }

    private boolean shouldStopSearch(int depthRemaining)
    {
        if (depthRemaining == 0)
        {
            return true;
        }

        if (hasReachedNodeBudget())
        {
            budgetReached = true;
            return true;
        }

        return false;
    }

    private boolean hasReachedNodeBudget()
    {
        return nodesEvaluated >= nodeBudget;
    }

    private double evaluateBoard(Board board, Player originalPlayer)
    {
        return boardEvaluator.evaluate(board, originalPlayer);
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
