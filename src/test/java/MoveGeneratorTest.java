import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import game.Board;
import game.Dice;
import game.Move;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;

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

    @Test
    void shouldUseHigherDieWhenOnlyOneDieCanBePlayed()
    {
        Board board = new Board(false);

        board.getPoint(0).addChecker(Player.WHITE);

        board.getPoint(7).addChecker(Player.BLACK);
        board.getPoint(7).addChecker(Player.BLACK);

        Dice dice = new Dice(2, 5);

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
            assertEquals(1, sequence.size());
            assertEquals(
                    5,
                    sequence.getMoves().getFirst().getDieValue()
            );
        }
    }

    @Test
    void shouldReturnNoSequencesWhenNoMoveIsPossible()
    {
        Board board = new Board(false);

        board.getPoint(0).addChecker(Player.WHITE);

        board.getPoint(1).addChecker(Player.BLACK);
        board.getPoint(1).addChecker(Player.BLACK);

        board.getPoint(2).addChecker(Player.BLACK);
        board.getPoint(2).addChecker(Player.BLACK);

        Dice dice = new Dice(1, 2);

        MoveGenerator generator = new MoveGenerator();

        List<MoveSequence> sequences =
                generator.generateMoveSequences(
                        board,
                        Player.WHITE,
                        dice
                );

        assertTrue(sequences.isEmpty());
    }

    @Test
    void shouldEnterFromBarBeforeNormalMove()
    {
        Board board = new Board(false);

        board.getPoint(0).addChecker(Player.BLACK);

        Move hitMove = new Move(
                Player.BLACK,
                0,
                1,
                1
        );

        board.getPoint(1).addChecker(Player.WHITE);
        board.applyMove(hitMove);

        board.getPoint(5).addChecker(Player.WHITE);

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
            assertTrue(
                    sequence.getMoves()
                            .getFirst()
                            .isEnteringFromBar()
            );
        }
    }

    @Test
    void shouldEnterFromBarAndThenUseSecondDie()
    {
        Board board = new Board(false);

        board.getPoint(1).addChecker(Player.WHITE);
        board.getPoint(0).addChecker(Player.BLACK);

        Move hitMove = new Move(
                Player.BLACK,
                0,
                1,
                1
        );

        board.applyMove(hitMove);

        assertEquals(1, board.getBarCount(Player.WHITE));

        Dice dice = new Dice(1, 2);

        MoveGenerator generator = new MoveGenerator();

        List<MoveSequence> sequences =
                generator.generateMoveSequences(
                        board,
                        Player.WHITE,
                        dice
                );

        assertFalse(sequences.isEmpty());

        boolean foundTwoMoveSequence = false;

        for (MoveSequence sequence : sequences)
        {
            if (sequence.size() == 2)
            {
                assertTrue(
                        sequence.getMoves()
                                .getFirst()
                                .isEnteringFromBar()
                );

                foundTwoMoveSequence = true;
            }
        }

        assertTrue(foundTwoMoveSequence);
    }

    @Test
    void shouldGenerateBearingOffSequence()
    {
        Board board = new Board(false);

        board.getPoint(22).addChecker(Player.WHITE);
        board.getPoint(23).addChecker(Player.WHITE);

        Dice dice = new Dice(1, 2);

        MoveGenerator generator = new MoveGenerator();

        List<MoveSequence> sequences =
                generator.generateMoveSequences(
                        board,
                        Player.WHITE,
                        dice
                );

        assertFalse(sequences.isEmpty());

        boolean foundBearingOffMove = false;

        for (MoveSequence sequence : sequences)
        {
            for (Move move : sequence.getMoves())
            {
                if (move.isBearingOff())
                {
                    foundBearingOffMove = true;
                }
            }
        }

        assertTrue(foundBearingOffMove);
    }
}
