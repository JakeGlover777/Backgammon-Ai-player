import game.Board;
import game.Move;
import game.Player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardTest
{
    @Test
    void shouldCreateStandardStartingPosition()
    {
        Board board = new Board();

        assertEquals(Player.WHITE, board.getPoint(0).getOwner());
        assertEquals(2, board.getPoint(0).getCheckerCount());

        assertEquals(Player.BLACK, board.getPoint(5).getOwner());
        assertEquals(5, board.getPoint(5).getCheckerCount());
    }

    @Test
    void shouldApplyNormalMove()
    {
        Board board = new Board();

        Move move = new Move(Player.WHITE, 0, 1, 1);
        board.applyMove(move);

        assertEquals(1, board.getPoint(0).getCheckerCount());
        assertEquals(1, board.getPoint(1).getCheckerCount());
        assertEquals(Player.WHITE, board.getPoint(1).getOwner());
    }

    @Test
    void shouldHitOpponentCheckerAndAddItToBar()
    {
        Board board = new Board(false);

        board.getPoint(0).addChecker(Player.WHITE);
        board.getPoint(1).addChecker(Player.BLACK);

        Move move = new Move(Player.WHITE, 0, 1, 1);
        board.applyMove(move);

        assertEquals(1, board.getBarCount(Player.BLACK));
        assertEquals(Player.WHITE, board.getPoint(1).getOwner());
        assertEquals(1, board.getPoint(1).getCheckerCount());
    }

    @Test
    void shouldBearOffChecker()
    {
        Board board = new Board(false);

        board.getPoint(23).addChecker(Player.WHITE);

        Move move = new Move(Player.WHITE, 23, 1);
        board.applyMove(move);

        assertEquals(1, board.getBorneOffCount(Player.WHITE));
        assertTrue(board.getPoint(23).isEmpty());
    }

    @Test
    void shouldCreateIndependentBoardCopy()
    {
        Board original = new Board();
        Board copy = new Board(original);

        Move move = new Move(Player.WHITE, 0, 1, 1);

        copy.applyMove(move);

        assertEquals(2, original.getPoint(0).getCheckerCount());
        assertTrue(original.getPoint(1).isEmpty());

        assertEquals(1, copy.getPoint(0).getCheckerCount());
        assertEquals(1, copy.getPoint(1).getCheckerCount());
    }
}