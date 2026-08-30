package statistics;

import game.Player;
import game.PlayerType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameStatistics
{
    private final int gameId;
    private final PlayerType whitePlayerType;
    private final PlayerType blackPlayerType;
    private final Player startingPlayer;
    private final List<DecisionStatistics> decisions;

    private Player winner;
    private int turnCount;

    public GameStatistics(int gameId, PlayerType whitePlayerType,
                          PlayerType blackPlayerType, Player startingPlayer)
    {
        this.gameId = gameId;
        this.whitePlayerType = whitePlayerType;
        this.blackPlayerType = blackPlayerType;
        this.startingPlayer = startingPlayer;

        decisions = new ArrayList<>();
        winner = Player.NONE;
        turnCount = 0;
    }

    public void recordDecision(DecisionStatistics decision)
    {
        decisions.add(decision);
    }

    public void incrementTurnCount()
    {
        turnCount++;
    }

    public void setWinner(Player winner)
    {
        this.winner = winner;
    }

    public int getGameId()
    {
        return gameId;
    }

    public PlayerType getWhitePlayerType()
    {
        return whitePlayerType;
    }

    public PlayerType getBlackPlayerType()
    {
        return blackPlayerType;
    }

    public Player getStartingPlayer()
    {
        return startingPlayer;
    }

    public Player getWinner()
    {
        return winner;
    }

    public int getTurnCount()
    {
        return turnCount;
    }

    public List<DecisionStatistics> getDecisions()
    {
        return Collections.unmodifiableList(decisions);
    }
}
