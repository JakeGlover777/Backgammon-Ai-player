import ai.BoardEvaluator;
import game.Board;
import game.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardEvaluatorTest
{
    private static final double TOLERANCE = 0.000001;

    @Test
    public void startingBoardIsEvaluatedSymmetrically()
    {
        Board board = new Board();
        BoardEvaluator evaluator = new BoardEvaluator();

        double whiteScore = evaluator.evaluate(board, Player.WHITE);
        double blackScore = evaluator.evaluate(board, Player.BLACK);

        assertEquals(whiteScore, -blackScore, TOLERANCE);
    }

    @Test
    public void startingBoardGivesBothPlayersEqualEvaluation()
    {
        Board board = new Board();
        BoardEvaluator evaluator = new BoardEvaluator();

        assertEquals(0.0, evaluator.evaluate(board, Player.WHITE),
                TOLERANCE);

        assertEquals(0.0, evaluator.evaluate(board, Player.BLACK),
                TOLERANCE);
    }
}
