package statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatisticsRecorder
{
    private final List<GameStatistics> games;

    public StatisticsRecorder()
    {
        games = new ArrayList<>();
    }

    public void recordGame(GameStatistics gameStatistics)
    {
        games.add(gameStatistics);
    }

    public List<GameStatistics> getGames()
    {
        return Collections.unmodifiableList(games);
    }

    public int getGameCount()
    {
        return games.size();
    }

    public void clear()
    {
        games.clear();
    }
}
