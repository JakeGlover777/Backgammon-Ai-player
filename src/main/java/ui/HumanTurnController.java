package ui;

import game.Board;
import game.Move;
import game.MoveSequence;
import game.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class HumanTurnController
{
    private final Board board;
    private final BoardView boardView;
    private final Consumer<String> instructionUpdater;
    private final Consumer<Move> moveHandler;

    private Integer selectedPoint;
    private List<MoveSequence> remainingSequences;
    private int moveIndex;

    public HumanTurnController(Board board, BoardView boardView, Consumer<String> instructionUpdater,
                               Consumer<Move> moveHandler)
    {
        this.board = board;
        this.boardView = boardView;
        this.instructionUpdater = instructionUpdater;
        this.moveHandler = moveHandler;

        selectedPoint = null;
        remainingSequences = null;
        moveIndex = 0;
    }

    public void startTurn(List<MoveSequence> sequences)
    {
        remainingSequences = sequences;
        moveIndex = 0;
        selectedPoint = null;

        boardView.clearHighlights();

        if (currentMoveRequiresBarEntry())
        {
            instructionUpdater.accept("You have a checker on the bar. Select it to re-enter.");
        }
        else
        {
            instructionUpdater.accept("Select a checker.");
        }
    }

    public void handleBoardClick(int pointIndex, Player currentPlayer)
    {
        if (remainingSequences == null || remainingSequences.isEmpty())
        {
            return;
        }

        if (currentMoveRequiresBarEntry())
        {
            handleBarSelection(pointIndex, currentPlayer);
            return;
        }

        handleNormalSelection(pointIndex, currentPlayer);
    }

    private void handleBarSelection(int pointIndex, Player currentPlayer)
    {
        int correctBar = currentPlayer == Player.WHITE
                ? BoardView.WHITE_BAR : BoardView.BLACK_BAR;

        if (selectedPoint == null)
        {
            if (pointIndex != correctBar)
            {
                instructionUpdater.accept("You must enter your checker from the bar first.");
                return;
            }

            selectedPoint = correctBar;

            Set<Integer> destinations = findBarDestinations();
            boardView.highlightPoints(destinations);
            instructionUpdater.accept("Select a highlighted entry point.");

            return;
        }

        if (selectedPoint == correctBar)
        {
            Move selectedMove = findBarMove(pointIndex);

            if (selectedMove != null)
            {
                moveHandler.accept(selectedMove);
                return;
            }
        }

        instructionUpdater.accept("That is not a legal entry point.");
    }

    private void handleNormalSelection(int pointIndex, Player currentPlayer)
    {
        if (selectedPoint == null)
        {
            selectChecker(pointIndex, currentPlayer);
            return;
        }

        if (pointIndex == BoardView.WHITE_BAR || pointIndex == BoardView.BLACK_BAR)
        {
            instructionUpdater.accept("Select a legal destination.");
            return;
        }

        Move selectedMove = findMove(selectedPoint, pointIndex);

        if (selectedMove != null)
        {
            moveHandler.accept(selectedMove);
            return;
        }

        if (pointIndex < 0)
        {
            instructionUpdater.accept("That is not a legal destination.");
            return;
        }

        if (board.getPoint(pointIndex).getOwner() == currentPlayer)
        {
            Set<Integer> destinations = findDestinations(pointIndex);

            if (!destinations.isEmpty())
            {
                selectedPoint = pointIndex;
                boardView.highlightPoints(destinations);
                instructionUpdater.accept("Select a highlighted destination.");

                return;
            }
        }

        instructionUpdater.accept("That is not a legal destination.");
    }

    private void selectChecker(int pointIndex, Player currentPlayer)
    {
        if (pointIndex < 0 || board.getPoint(pointIndex).getOwner() != currentPlayer)
        {
            instructionUpdater.accept("Select one of your own checkers.");
            return;
        }

        Set<Integer> destinations = findDestinations(pointIndex);

        if (destinations.isEmpty())
        {
            instructionUpdater.accept("That checker cannot move.");
            return;
        }

        selectedPoint = pointIndex;
        boardView.highlightPoints(destinations);
        instructionUpdater.accept("Select a highlighted destination.");
    }

    public void recordMove(Move selectedMove)
    {
        filterSequences(selectedMove);

        moveIndex++;
        selectedPoint = null;
    }

    public boolean currentMoveRequiresBarEntry()
    {
        if (remainingSequences == null)
        {
            return false;
        }

        for (MoveSequence sequence : remainingSequences)
        {
            if (sequence.size() <= moveIndex)
            {
                continue;
            }

            Move move = sequence.getMoves().get(moveIndex);

            if (move.isEnteringFromBar())
            {
                return true;
            }
        }

        return false;
    }

    public boolean turnIsComplete()
    {
        for (MoveSequence sequence : remainingSequences)
        {
            if (sequence.size() > moveIndex)
            {
                return false;
            }
        }

        return true;
    }

    public void reset()
    {
        selectedPoint = null;
        remainingSequences = null;
        moveIndex = 0;

        boardView.clearHighlights();
    }

    private Set<Integer> findDestinations(int fromPoint)
    {
        Set<Integer> destinations = new HashSet<>();

        for (MoveSequence sequence : remainingSequences)
        {
            if (sequence.size() <= moveIndex)
            {
                continue;
            }

            Move move = sequence.getMoves().get(moveIndex);

            if (move.isEnteringFromBar())
            {
                continue;
            }

            if (move.isBearingOff() && move.getFromPoint() == fromPoint)
            {
                int bearOffDestination = move.getPlayer() == Player.WHITE
                        ? BoardView.WHITE_BEAR_OFF : BoardView.BLACK_BEAR_OFF;

                destinations.add(bearOffDestination);
                continue;
            }

            if (move.getFromPoint() == fromPoint)
            {
                destinations.add(move.getToPoint());
            }
        }

        return destinations;
    }

    private Move findMove(int fromPoint, int toPoint)
    {
        for (MoveSequence sequence : remainingSequences)
        {
            if (sequence.size() <= moveIndex)
            {
                continue;
            }

            Move move = sequence.getMoves().get(moveIndex);

            if (move.isEnteringFromBar())
            {
                continue;
            }

            if (move.isBearingOff())
            {
                int bearOffDestination = move.getPlayer() == Player.WHITE
                        ? BoardView.WHITE_BEAR_OFF : BoardView.BLACK_BEAR_OFF;

                if (move.getFromPoint() == fromPoint && toPoint == bearOffDestination)
                {
                    return move;
                }

                continue;
            }

            if (move.getFromPoint() == fromPoint && move.getToPoint() == toPoint)
            {
                return move;
            }
        }

        return null;
    }

    private Set<Integer> findBarDestinations()
    {
        Set<Integer> destinations = new HashSet<>();

        for (MoveSequence sequence : remainingSequences)
        {
            if (sequence.size() <= moveIndex)
            {
                continue;
            }

            Move move = sequence.getMoves().get(moveIndex);

            if (move.isEnteringFromBar())
            {
                destinations.add(move.getToPoint());
            }
        }

        return destinations;
    }

    private Move findBarMove(int toPoint)
    {
        for (MoveSequence sequence : remainingSequences)
        {
            if (sequence.size() <= moveIndex)
            {
                continue;
            }

            Move move = sequence.getMoves().get(moveIndex);

            if (move.isEnteringFromBar() && move.getToPoint() == toPoint)
            {
                return move;
            }
        }

        return null;
    }

    private void filterSequences(Move selectedMove)
    {
        List<MoveSequence> filtered = new ArrayList<>();

        for (MoveSequence sequence : remainingSequences)
        {
            if (sequence.size() <= moveIndex)
            {
                continue;
            }

            Move move = sequence.getMoves().get(moveIndex);

            if (movesMatch(move, selectedMove))
            {
                filtered.add(sequence);
            }
        }

        remainingSequences = filtered;
    }

    private boolean movesMatch(Move first, Move second)
    {
        if (first.isEnteringFromBar() != second.isEnteringFromBar())
        {
            return false;
        }

        if (first.isBearingOff() != second.isBearingOff())
        {
            return false;
        }

        if (first.getDieValue() != second.getDieValue())
        {
            return false;
        }

        if (!first.isEnteringFromBar() && first.getFromPoint() != second.getFromPoint())
        {
            return false;
        }

        if (!first.isBearingOff() && first.getToPoint() != second.getToPoint())
        {
            return false;
        }

        return true;
    }
}
