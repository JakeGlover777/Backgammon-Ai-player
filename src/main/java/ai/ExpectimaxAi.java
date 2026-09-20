package ai;

import game.Board;
import game.Dice;
import game.Move;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;

import java.util.List;

/**
 * Implements an AI player that selects moves using Expectimax search.
 *
 * <p>The search models future dice rolls as chance outcomes and future move
 * choices as decision nodes. The original player attempts to maximise the
 * evaluation score, while the opponent attempts to minimise it. A node budget
 * limits the amount of search performed, with heuristic board evaluation used
 * when further search cannot be completed.
 */


public class ExpectimaxAi implements AiPlayer
{
    private static final int DIE_SIDES = 6;
    private static final int DEFAULT_SEARCH_DEPTH = 1;
    private static final int MINIMUM_SEARCH_DEPTH = 1;
    private static final int DEFAULT_NODE_BUDGET = 10_000;
    private static final int MINIMUM_NODE_BUDGET = 1;
    private static final int DICE_OUTCOMES = DIE_SIDES * DIE_SIDES;

    private final MoveGenerator moveGenerator;
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private final int nodeBudget;

    private int nodesEvaluated;
    private boolean budgetReached;

    /**
     * Creates an Expectimax AI using the default search depth and node budget.
     */

    public ExpectimaxAi()
    {
        this(DEFAULT_SEARCH_DEPTH, DEFAULT_NODE_BUDGET);
    }

    /**
     * Creates an Expectimax AI using the specified search depth and the default
     * node budget.
     *
     * @param searchDepth the number of future decision levels to search
     * @throws IllegalArgumentException if the search depth is less than one
     */

    public ExpectimaxAi(int searchDepth)
    {
        this(searchDepth, DEFAULT_NODE_BUDGET);
    }

    /**
     * Creates an Expectimax AI using the specified search depth and node budget.
     *
     * @param searchDepth the number of future decision levels to search
     * @param nodeBudget the computational budget allocated to each root move
     * @throws IllegalArgumentException if the search depth is less than one
     * @throws IllegalArgumentException if the node budget is less than one
     */

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

    /**
     * Selects the legal move sequence with the highest expected score according
     * to the configured Expectimax search.
     *
     * <p>Each legal root sequence is applied to a copy of the board before future
     * dice outcomes and move decisions are explored.
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

            double score = calculateExpectedScore(simulatedBoard, opponent, player,
                    searchDepth, nodeBudget);

            if (score > bestScore)
            {
                bestScore = score;
                bestSequence = sequence;
            }
        }

        return bestSequence;
    }

    /**
     * Returns the number of decision nodes evaluated during the most recent
     * move selection.
     *
     * @return the number of evaluated decision nodes
     */

    public int getNodesEvaluated()
    {
        return nodesEvaluated;
    }

    /**
     * Returns the configured search depth.
     *
     * @return the search depth
     */

    public int getSearchDepth()
    {
        return searchDepth;
    }

    /**
     * Returns the configured node budget used for each root move.
     *
     * @return the node budget
     */

    public int getNodeBudget()
    {
        return nodeBudget;
    }

    /**
     * Indicates whether the node budget limited any part of the most recent
     * search.
     *
     * @return true if the budget caused heuristic evaluation to be used before
     *         the configured search depth was completed, otherwise false
     */

    public boolean wasBudgetReached()
    {
        return budgetReached;
    }

    /**
     * Calculates the expected score across all possible dice outcomes.
     *
     * <p>The available budget is divided equally across the possible ordered dice
     * outcomes. If the search depth or available budget is exhausted, the current
     * board state is evaluated heuristically.
     *
     * @param board the board state being searched
     * @param currentPlayer the player whose turn is being considered
     * @param originalPlayer the player for whom the root move is being evaluated
     * @param depthRemaining the remaining search depth
     * @param budgetRemaining the remaining node budget for this branch
     * @return the expected evaluation score across the possible dice outcomes
     */

    private double calculateExpectedScore(Board board, Player currentPlayer, Player originalPlayer,
                                          int depthRemaining, int budgetRemaining)
    {
        if (depthRemaining == 0 || budgetRemaining <= 0)
        {
            if (budgetRemaining <= 0)
            {
                budgetReached = true;
            }

            return evaluateBoard(board, originalPlayer);
        }

        int budgetPerOutcome = budgetRemaining / DICE_OUTCOMES;

        if (budgetPerOutcome == 0)
        {
            budgetReached = true;
            return evaluateBoard(board, originalPlayer);
        }

        double totalScore = 0;

        for (int dieOne = 1; dieOne <= DIE_SIDES; dieOne++)
        {
            for (int dieTwo = 1; dieTwo <= DIE_SIDES; dieTwo++)
            {
                Dice dice = new Dice(dieOne, dieTwo);

                totalScore += calculateDecisionScore(board, currentPlayer, originalPlayer, dice,
                        depthRemaining, budgetPerOutcome);
            }
        }

        return totalScore / DICE_OUTCOMES;
    }

    /**
     * Calculates the score of a decision node for a particular dice outcome.
     *
     * <p>The original player selects the maximum available score, while the
     * opponent selects the minimum. If no legal move is available, play passes
     * to the opposing player.
     *
     * @param board the board state being searched
     * @param currentPlayer the player making the decision
     * @param originalPlayer the player for whom the root move is being evaluated
     * @param dice the dice available at this decision node
     * @param depthRemaining the remaining search depth
     * @param budgetRemaining the remaining node budget for this branch
     * @return the resulting score for the decision node
     */

    private double calculateDecisionScore(Board board, Player currentPlayer, Player originalPlayer,
                                          Dice dice, int depthRemaining, int budgetRemaining)
    {
        if (budgetRemaining <= 0)
        {
            budgetReached = true;
            return evaluateBoard(board, originalPlayer);
        }

        nodesEvaluated++;

        List<MoveSequence> legalSequences =
                moveGenerator.generateMoveSequences(board, currentPlayer, dice);

        int remainingBudget = budgetRemaining - 1;

        if (legalSequences.isEmpty())
        {
            return calculateExpectedScore(board, getOpponent(currentPlayer), originalPlayer,
                    depthRemaining - 1, remainingBudget);
        }

        if (currentPlayer == originalPlayer)
        {
            return findMaximumScore(board, currentPlayer, originalPlayer, legalSequences,
                    depthRemaining, remainingBudget);
        }

        return findMinimumScore(board, currentPlayer, originalPlayer, legalSequences,
                depthRemaining, remainingBudget);
    }

    /**
     * Finds the highest score available to the original player.
     *
     * <p>The remaining budget is divided between the available legal move
     * sequences before each resulting board state is explored.
     *
     * @param board the current board state
     * @param currentPlayer the player making the move
     * @param originalPlayer the player for whom the root move is being evaluated
     * @param legalSequences the legal move sequences available
     * @param depthRemaining the remaining search depth
     * @param budgetRemaining the remaining node budget
     * @return the highest score found
     */

    private double findMaximumScore(Board board, Player currentPlayer, Player originalPlayer,
                                    List<MoveSequence> legalSequences, int depthRemaining,
                                    int budgetRemaining)
    {
        if (budgetRemaining <= 0)
        {
            budgetReached = true;
            return evaluateBoard(board, originalPlayer);
        }

        int budgetPerSequence = budgetRemaining / legalSequences.size();

        if (budgetPerSequence == 0)
        {
            budgetReached = true;
            return evaluateBoard(board, originalPlayer);
        }

        double bestScore = Double.NEGATIVE_INFINITY;

        for (MoveSequence sequence : legalSequences)
        {
            Board futureBoard = new Board(board);
            applySequence(futureBoard, sequence);

            double score = calculateExpectedScore(futureBoard, getOpponent(currentPlayer), originalPlayer,
                    depthRemaining - 1, budgetPerSequence);

            if (score > bestScore)
            {
                bestScore = score;
            }
        }

        return bestScore;
    }

    /**
     * Finds the lowest score available to the opponent of the original player.
     *
     * <p>The remaining budget is divided between the available legal move
     * sequences before each resulting board state is explored.
     *
     * @param board the current board state
     * @param currentPlayer the player making the move
     * @param originalPlayer the player for whom the root move is being evaluated
     * @param legalSequences the legal move sequences available
     * @param depthRemaining the remaining search depth
     * @param budgetRemaining the remaining node budget
     * @return the lowest score found
     */

    private double findMinimumScore(Board board, Player currentPlayer, Player originalPlayer,
                                    List<MoveSequence> legalSequences, int depthRemaining,
                                    int budgetRemaining)
    {
        if (budgetRemaining <= 0)
        {
            budgetReached = true;
            return evaluateBoard(board, originalPlayer);
        }

        int budgetPerSequence = budgetRemaining / legalSequences.size();

        if (budgetPerSequence == 0)
        {
            budgetReached = true;
            return evaluateBoard(board, originalPlayer);
        }

        double worstScore = Double.POSITIVE_INFINITY;

        for (MoveSequence sequence : legalSequences)
        {
            Board futureBoard = new Board(board);
            applySequence(futureBoard, sequence);

            double score = calculateExpectedScore(futureBoard, getOpponent(currentPlayer), originalPlayer,
                    depthRemaining - 1, budgetPerSequence);

            if (score < worstScore)
            {
                worstScore = score;
            }
        }

        return worstScore;
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
        return player == Player.WHITE ? Player.BLACK : Player.WHITE;
    }
}
