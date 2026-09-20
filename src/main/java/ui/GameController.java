package ui;

import game.Board;
import game.Dice;
import game.Game;
import game.Move;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;
import game.PlayerType;
import statistics.GameStatistics;
import statistics.StatisticsRecorder;

import java.util.List;
import java.util.function.Consumer;

/**
 * Coordinates the state and flow of a Backgammon game with the user
 * interface.
 *
 * <p>The controller manages human and AI turns, applies completed moves,
 * updates the board view and interface labels, and records game statistics.
 */

public class GameController
{
    private final Game game;
    private final Board board;
    private final MoveGenerator moveGenerator;
    private final BoardView boardView;
    private final HumanTurnController humanTurnController;
    private final AiTurnController aiTurnController;

    private final Consumer<String> currentPlayerUpdater;
    private final Consumer<String> diceUpdater;
    private final Consumer<String> instructionUpdater;

    private final StatisticsRecorder statisticsRecorder;
    private final GameStatistics gameStatistics;

    private boolean diceRolled;
    private boolean gameOver;
    private boolean active;

    /**
     * Creates a game controller for the supplied game and player configuration.
     *
     * @param game the game being controlled
     * @param boardView the view used to display and interact with the board
     * @param whitePlayerType the controller type assigned to White
     * @param blackPlayerType the controller type assigned to Black
     * @param currentPlayerUpdater updates the displayed current player
     * @param diceUpdater updates the displayed dice values
     * @param instructionUpdater updates the displayed player instructions
     */

    public GameController(Game game, BoardView boardView, PlayerType whitePlayerType,
                          PlayerType blackPlayerType, Consumer<String> currentPlayerUpdater,
                          Consumer<String> diceUpdater, Consumer<String> instructionUpdater)
    {
        this.game = game;
        this.board = game.getBoard();
        this.boardView = boardView;
        this.currentPlayerUpdater = currentPlayerUpdater;
        this.diceUpdater = diceUpdater;
        this.instructionUpdater = instructionUpdater;

        moveGenerator = new MoveGenerator();

        statisticsRecorder = new StatisticsRecorder();
        gameStatistics = new GameStatistics(1, whitePlayerType,
                blackPlayerType, game.getCurrentPlayer());

        diceRolled = false;
        gameOver = false;
        active = true;

        humanTurnController = new HumanTurnController(board, boardView,
                instructionUpdater, this::applySelectedMove);

        aiTurnController = new AiTurnController(game, whitePlayerType, blackPlayerType,
                diceUpdater, instructionUpdater);

        boardView.setOnPointClicked(this::handleBoardClick);
    }

    /**
     * Starts the game controller and schedules an AI turn if the current player
     * is controlled by an AI.
     */

    public void start()
    {
        scheduleAiTurn();
    }

    /**
     * Stops the game controller and prevents further AI turns from being
     * processed.
     */

    public void stop()
    {
        active = false;
        aiTurnController.stop();
    }

    /**
     * Rolls the dice and begins the current human player's turn.
     *
     * <p>The opening dice are retained when processing the first turn. If no
     * legal move sequences are available, the turn is skipped automatically.
     */


    public void rollDice()
    {
        if (!active || gameOver || diceRolled || aiTurnController.isAiTurn())
        {
            return;
        }

        Dice dice = game.getDice();

        if (game.isOpeningRoll())
        {
            game.completeOpeningRoll();
        }
        else
        {
            dice.roll();
        }

        updateDiceLabel(dice);

        List<MoveSequence> legalSequences = moveGenerator.generateMoveSequences(board,
                game.getCurrentPlayer(), dice);

        if (legalSequences.isEmpty())
        {
            handleNoLegalMoves();
            return;
        }

        diceRolled = true;
        humanTurnController.startTurn(legalSequences);
    }

    /**
     * Completes the current turn when no legal moves are available.
     *
     * <p>The turn count is incremented, control passes to the opposing player,
     * and the interface is reset for the next turn.
     */

    private void handleNoLegalMoves()
    {
        if (!active || gameOver)
        {
            return;
        }

        instructionUpdater.accept("No legal moves. Turn skipped.");

        gameStatistics.incrementTurnCount();

        game.switchPlayer();

        updateCurrentPlayerLabel();
        resetDiceLabel();
        resetHumanTurnState();

        scheduleAiTurn();
    }

    private void handleBoardClick(int pointIndex)
    {
        if (!active || gameOver || aiTurnController.isAiTurn() || !diceRolled)
        {
            return;
        }

        humanTurnController.handleBoardClick(pointIndex, game.getCurrentPlayer());
    }

    /**
     * Schedules the current player's turn when that player is controlled by an
     * AI.
     */

    private void scheduleAiTurn()
    {
        if (!active || gameOver)
        {
            return;
        }

        aiTurnController.scheduleTurn(
                this::completeAiTurn,
                this::handleNoLegalMoves);
    }

    /**
     * Completes an AI turn using the result produced by the AI turn controller.
     *
     * <p>The selected move sequence is applied to the board, statistics are
     * recorded, and play either ends if a winner is found or passes to the
     * opposing player.
     *
     * @param result the completed AI turn result
     */

    private void completeAiTurn(AiTurnController.AiTurnResult result)
    {
        if (!active || gameOver)
        {
            return;
        }

        gameStatistics.recordDecision(result.statistics());

        for (Move move : result.sequence().getMoves())
        {
            board.applyMove(move);
        }

        gameStatistics.incrementTurnCount();

        boardView.clearHighlights();
        boardView.refresh();

        Player winner = game.getWinner();

        if (winner != Player.NONE)
        {
            showWinner(winner);
            return;
        }

        game.switchPlayer();

        updateCurrentPlayerLabel();
        resetDiceLabel();
        instructionUpdater.accept("Turn complete.");

        scheduleAiTurn();
    }

    /**
     * Applies a move selected by the human player and updates the current turn.
     *
     * <p>If the move completes the game, the winner is recorded. If it completes
     * the current turn, play passes to the opposing player. Otherwise, the human
     * player is prompted to continue the current move sequence.
     *
     * @param selectedMove the move selected by the human player
     */

    private void applySelectedMove(Move selectedMove)
    {
        board.applyMove(selectedMove);
        humanTurnController.recordMove(selectedMove);

        boardView.clearHighlights();
        boardView.refresh();

        Player winner = game.getWinner();

        if (winner != Player.NONE)
        {
            gameStatistics.incrementTurnCount();

            showWinner(winner);
            resetHumanTurnState();

            return;
        }

        if (humanTurnController.turnIsComplete())
        {
            gameStatistics.incrementTurnCount();

            game.switchPlayer();

            updateCurrentPlayerLabel();
            resetDiceLabel();
            instructionUpdater.accept("Turn complete. Roll the dice.");

            resetHumanTurnState();
            scheduleAiTurn();

            return;
        }

        if (humanTurnController.currentMoveRequiresBarEntry())
        {
            instructionUpdater.accept("Select your checker on the bar.");
        }
        else
        {
            instructionUpdater.accept("Select your next checker.");
        }
    }

    /**
     * Ends the game and records the winning player and completed game statistics.
     *
     * @param winner the player that won the game
     */

    private void showWinner(Player winner)
    {
        gameOver = true;

        gameStatistics.setWinner(winner);
        statisticsRecorder.recordGame(gameStatistics);

        currentPlayerUpdater.accept("Winner: " + winner);
        resetDiceLabel();
        instructionUpdater.accept(winner + " wins the game!");
    }

    private void resetHumanTurnState()
    {
        diceRolled = false;
        humanTurnController.reset();
    }

    private void updateCurrentPlayerLabel()
    {
        currentPlayerUpdater.accept("Current Player: "
                + game.getCurrentPlayer());
    }

    private void updateDiceLabel(Dice dice)
    {
        diceUpdater.accept("Dice: "
                + dice.getDieOne()
                + " | "
                + dice.getDieTwo());
    }

    private void resetDiceLabel()
    {
        diceUpdater.accept("Dice: - | -");
    }
}
