package statistics;

public class SearchStatistics
{
    private final int nodesEvaluated;
    private final int searchDepth;
    private final int nodeBudget;
    private final boolean budgetReached;

    public SearchStatistics(int nodesEvaluated, int searchDepth, int nodeBudget,
                            boolean budgetReached)
    {
        this.nodesEvaluated = nodesEvaluated;
        this.searchDepth = searchDepth;
        this.nodeBudget = nodeBudget;
        this.budgetReached = budgetReached;
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

    public boolean isBudgetReached()
    {
        return budgetReached;
    }
}
