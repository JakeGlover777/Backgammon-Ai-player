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

    /**
     * Stores statistics recorded for a single Backgammon game.
     *
     * <p>The recorded data includes the player types, starting player, winner,
     * number of turns, and the individual AI decisions made during the game.
     */

    public GameStatistics(int gameId, PlayerType whitePlayerType, PlayerType blackPlayerType,
                          Player startingPlayer)
    {
        this.gameId = gameId;
        this.whitePlayerType = whitePlayerType;
        this.blackPlayerType = blackPlayerType;
        this.startingPlayer = startingPlayer;

        decisions = new ArrayList<>();
        winner = Player.NONE;
        turnCount = 0;
    }

    /**
     * Adds the statistics for a recorded AI decision.
     *
     * @param decision the decision statistics to record
     */

    public void recordDecision(DecisionStatistics decision)
    {
        decisions.add(decision);
    }

    /**
     * Increments the number of turns recorded for the game.
     */

    public void incrementTurnCount()
    {
        turnCount++;
    }

    /**
     * Sets the winner of the game.
     *
     * @param winner the winning player
     */

    public void setWinner(Player winner)
    {
        this.winner = winner;
    }

    /**
     * Returns the identifier assigned to the game.
     *
     * @return the game identifier
     */

    public int getGameId()
    {
        return gameId;
    }

    /**
     * Returns the controller type assigned to White.
     *
     * @return White's player type
     */

    public PlayerType getWhitePlayerType()
    {
        return whitePlayerType;
    }

    /**
     * Returns the controller type assigned to Black.
     *
     * @return Black's player type
     */

    public PlayerType getBlackPlayerType()
    {
        return blackPlayerType;
    }

    /**
     * Returns the player that started the game.
     *
     * @return the starting player
     */

    public Player getStartingPlayer()
    {
        return startingPlayer;
    }

    /**
     * Returns the winner of the game.
     *
     * @return the winning player, or Player.NONE if no winner has been recorded
     */

    public Player getWinner()
    {
        return winner;
    }

    /**
     * Returns the number of turns recorded for the game.
     *
     * @return the number of turns
     */

    public int getTurnCount()
    {
        return turnCount;
    }

    /**
     * Returns an unmodifiable view of the decision statistics recorded for the
     * game.
     *
     * @return the recorded decision statistics
     */

    public List<DecisionStatistics> getDecisions()
    {
        return Collections.unmodifiableList(decisions);
    }
}
