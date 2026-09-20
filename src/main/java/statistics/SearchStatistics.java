package statistics;

/**
 * Stores statistics recorded from an Expectimax search.
 *
 * <p>The statistics describe the number of decision nodes evaluated, the
 * configured search depth and node budget, and whether the available budget
 * limited the search.
 */

public class SearchStatistics
{
    private final int nodesEvaluated;
    private final int searchDepth;
    private final int nodeBudget;
    private final boolean budgetReached;

    /**
     * Creates a set of statistics for an Expectimax search.
     *
     * @param nodesEvaluated the number of decision nodes evaluated
     * @param searchDepth the configured search depth
     * @param nodeBudget the configured node budget
     * @param budgetReached whether the available budget limited the search
     */

    public SearchStatistics(int nodesEvaluated, int searchDepth, int nodeBudget,
                            boolean budgetReached)
    {
        this.nodesEvaluated = nodesEvaluated;
        this.searchDepth = searchDepth;
        this.nodeBudget = nodeBudget;
        this.budgetReached = budgetReached;
    }

    /**
     * Returns the number of decision nodes evaluated.
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
     * Returns the configured node budget.
     *
     * @return the node budget
     */

    public int getNodeBudget()
    {
        return nodeBudget;
    }

    /**
     * Indicates whether the available budget limited the search.
     *
     * @return true if the budget limited the search, otherwise false
     */

    public boolean isBudgetReached()
    {
        return budgetReached;
    }
}
