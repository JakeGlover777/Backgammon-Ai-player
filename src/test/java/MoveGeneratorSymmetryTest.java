import game.Board;
import game.Dice;
import game.Move;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoveGeneratorSymmetryTest
{
    @Test
    public void startingPositionProducesSameNumberOfSequencesForBothPlayers()
    {
        Board board = new Board();
        Dice dice = new Dice(3, 5);
        MoveGenerator moveGenerator = new MoveGenerator();

        List<MoveSequence> whiteSequences =
                moveGenerator.generateMoveSequences(board, Player.WHITE, dice);

        List<MoveSequence> blackSequences =
                moveGenerator.generateMoveSequences(board, Player.BLACK, dice);

        assertEquals(
                whiteSequences.size(), blackSequences.size());
    }

    @Test
    public void startingPositionWithDoublesProducesSameNumberOfSequencesForBothPlayers()
    {
        Board board = new Board();
        Dice dice = new Dice(4, 4);
        MoveGenerator moveGenerator = new MoveGenerator();

        List<MoveSequence> whiteSequences =
                moveGenerator.generateMoveSequences(board, Player.WHITE, dice);

        List<MoveSequence> blackSequences =
                moveGenerator.generateMoveSequences(board, Player.BLACK, dice);

        assertEquals(
                whiteSequences.size(), blackSequences.size());
    }

    @Test
    public void barEntryProducesSameNumberOfSequencesForBothPlayers()
    {
        Board whiteBoard = new Board(false);
        Board blackBoard = new Board(false);

        whiteBoard.getPoint(10).addChecker(Player.WHITE);
        whiteBoard.getPoint(11).addChecker(Player.BLACK);
        whiteBoard.applyMove(
                new Move(Player.BLACK, 11, 10, 1));

        blackBoard.getPoint(13).addChecker(Player.BLACK);
        blackBoard.getPoint(12).addChecker(Player.WHITE);
        blackBoard.applyMove(
                new Move(Player.WHITE, 12, 13, 1));

        assertEquals(1, whiteBoard.getBarCount(Player.WHITE));
        assertEquals(1, blackBoard.getBarCount(Player.BLACK));

        Dice dice = new Dice(3, 5);
        MoveGenerator moveGenerator = new MoveGenerator();

        List<MoveSequence> whiteSequences =
                moveGenerator.generateMoveSequences(whiteBoard, Player.WHITE, dice);

        List<MoveSequence> blackSequences =
                moveGenerator.generateMoveSequences(blackBoard, Player.BLACK, dice);

        assertEquals(
                whiteSequences.size(), blackSequences.size());
    }

    @Test
    public void bearingOffProducesSameNumberOfSequencesForBothPlayers()
    {
        Board whiteBoard = new Board(false);
        Board blackBoard = new Board(false);

        whiteBoard.getPoint(21).addChecker(Player.WHITE);
        whiteBoard.getPoint(22).addChecker(Player.WHITE);
        whiteBoard.getPoint(23).addChecker(Player.WHITE);

        blackBoard.getPoint(2).addChecker(Player.BLACK);
        blackBoard.getPoint(1).addChecker(Player.BLACK);
        blackBoard.getPoint(0).addChecker(Player.BLACK);

        Dice dice = new Dice(3, 5);
        MoveGenerator moveGenerator = new MoveGenerator();

        List<MoveSequence> whiteSequences =
                moveGenerator.generateMoveSequences(whiteBoard, Player.WHITE, dice);

        List<MoveSequence> blackSequences =
                moveGenerator.generateMoveSequences(blackBoard, Player.BLACK, dice);

        assertEquals(
                whiteSequences.size(), blackSequences.size());
    }
}