import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoveGeneratorTest
{
    @Test
    void shouldGenerateFourMovesForDoubleWhenPossible()
    {
        Board board = new Board(false);

        board.getPoint(0).addChecker(Player.WHITE);

        Dice dice = new Dice(1, 1);

        MoveGenerator generator = new MoveGenerator();

        List<MoveSequence> sequences =
                generator.generateMoveSequences(
                        board,
                        Player.WHITE,
                        dice
                );

        boolean foundFourMoveSequence = false;

        for (MoveSequence sequence : sequences)
        {
            if (sequence.size() == 4)
            {
                foundFourMoveSequence = true;
                break;
            }
        }

        assertTrue(foundFourMoveSequence);
    }

    @Test
    void shouldGenerateTwoMoveSequenceWhenBothDiceCanBeUsed()
    {
        Board board = new Board();

        Dice dice = new Dice(1, 2);

        MoveGenerator generator = new MoveGenerator();

        List<MoveSequence> sequences =
                generator.generateMoveSequences(
                        board,
                        Player.WHITE,
                        dice
                );

        assertFalse(sequences.isEmpty());

        for (MoveSequence sequence : sequences)
        {
            assertEquals(2, sequence.size());
        }
    }
}
