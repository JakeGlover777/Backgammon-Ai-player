import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameTest
{
    @Test
    void shouldStartWithoutWinner()
    {
        Game game = new Game();

        assertEquals(Player.NONE, game.getWinner());
    }
}