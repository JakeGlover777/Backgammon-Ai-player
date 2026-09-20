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

/**
 * Manages move selection during a human player's turn.
 *
 * <p>The controller tracks the legal move sequences available for the turn,
 * processes board selections, highlights legal destinations, and narrows the
 * remaining sequences as individual moves are selected.
 */

public class HumanTurnController
{
    private final Board board;
    private final BoardView boardView;
    private final Consumer<String> instructionUpdater;
    private final Consumer<Move> moveHandler;

    private Integer selectedPoint;
    private List<MoveSequence> remainingSequences;
    private int moveIndex;

    /**
     * Creates a human turn controller for the supplied board and view.
     *
     * @param board the board containing the current game state
     * @param boardView the view used to display and highlight board positions
     * @param instructionUpdater updates the instructions displayed to the player
     * @param moveHandler handles a legal move selected by the player
     */

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

    /**
     * Starts a human turn using the supplied legal move sequences.
     *
     * <p>The current selection state is reset and the player is prompted either
     * to select a checker or to enter a checker from the bar.
     *
     * @param sequences the legal move sequences available for the turn
     */

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

    /**
     * Processes a board location selected by the human player.
     *
     * <p>The selection is handled as either a bar-entry interaction or a normal
     * checker movement depending on the currently available legal sequences.
     *
     * @param pointIndex the index of the selected board location
     * @param currentPlayer the player taking the current turn
     */

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

    /**
     * Records a move selected during the current human turn.
     *
     * <p>The remaining legal sequences are filtered to those containing the
     * selected move at the current position, before advancing to the next move
     * in the sequence.
     *
     * @param selectedMove the move selected by the player
     */

    public void recordMove(Move selectedMove)
    {
        filterSequences(selectedMove);

        moveIndex++;
        selectedPoint = null;
    }

    /**
     * Determines whether the next move in any remaining legal sequence requires
     * a checker to enter from the bar.
     *
     * @return true if the current move requires bar entry, otherwise false
     */

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

    /**
     * Determines whether all moves in the current human turn have been completed.
     *
     * @return true if no remaining legal sequence contains another move,
     *         otherwise false
     */

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

    /**
     * Resets the human turn state and clears any highlighted board locations.
     */

    public void reset()
    {
        selectedPoint = null;
        remainingSequences = null;
        moveIndex = 0;

        boardView.clearHighlights();
    }

    /**
     * Finds the legal destinations for a checker at the specified point based on
     * the remaining move sequences.
     *
     * <p>Bearing-off moves are converted to the corresponding board-view
     * destination so they can be selected through the interface.
     *
     * @param fromPoint the point containing the selected checker
     * @return the legal destination indices for the selected checker
     */

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

    /**
     * Filters the remaining legal sequences to those that contain the selected
     * move at the current move position.
     *
     * @param selectedMove the move selected by the human player
     */

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

    /**
     * Determines whether two moves represent the same playable action.
     *
     * <p>The comparison accounts for bar entry, bearing off, die value, source
     * point, and destination point as applicable to the move type.
     *
     * @param first the first move to compare
     * @param second the second move to compare
     * @return true if the moves represent the same action, otherwise false
     */

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
