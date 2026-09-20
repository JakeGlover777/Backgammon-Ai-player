package statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores statistics recorded across multiple Backgammon games.
 *
 * <p>Completed game statistics can be added to the recorder and later
 * retrieved for analysis or export.
 */

public class StatisticsRecorder
{
    private final List<GameStatistics> games;

    /**
     * Creates an empty statistics recorder.
     */

    public StatisticsRecorder()
    {
        games = new ArrayList<>();
    }

    /**
     * Adds the statistics recorded for a game.
     *
     * @param gameStatistics the game statistics to record
     */

    public void recordGame(GameStatistics gameStatistics)
    {
        games.add(gameStatistics);
    }

    /**
     * Returns an unmodifiable view of the recorded game statistics.
     *
     * @return the recorded game statistics
     */

    public List<GameStatistics> getGames()
    {
        return Collections.unmodifiableList(games);
    }

    /**
     * Returns the number of games currently recorded.
     *
     * @return the number of recorded games
     */

    public int getGameCount()
    {
        return games.size();
    }

    /**
     * Removes all recorded game statistics.
     */

    public void clear()
    {
        games.clear();
    }
}
