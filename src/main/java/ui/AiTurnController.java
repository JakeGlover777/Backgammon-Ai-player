package ui;

import ai.AiPlayer;
import ai.ExpectimaxAi;
import ai.HeuristicAi;
import ai.RandomAi;
import game.Board;
import game.Dice;
import game.Game;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;
import game.PlayerType;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import statistics.DecisionRecorder;
import statistics.DecisionResult;
import statistics.DecisionStatistics;

import java.util.function.Consumer;

/**
 * Manages turns for AI-controlled players in the user interface.
 *
 * <p>The controller schedules AI turns, handles dice rolls, creates the
 * appropriate AI implementation, records decision statistics, and returns
 * completed AI decisions to the game controller.
 */

public class AiTurnController
{
    private static final double AI_DELAY_MILLISECONDS = 500;
    private static final int EXPECTIMAX_SEARCH_DEPTH = 2;

    private final Game game;
    private final Board board;
    private final MoveGenerator moveGenerator;
    private final DecisionRecorder decisionRecorder;

    private final PlayerType whitePlayerType;
    private final PlayerType blackPlayerType;

    private final Consumer<String> diceUpdater;
    private final Consumer<String> instructionUpdater;

    private PauseTransition aiPause;
    private boolean active;

    /**
     * Creates an AI turn controller for the supplied game and player
     * configuration.
     *
     * @param game the game being controlled
     * @param whitePlayerType the controller type assigned to White
     * @param blackPlayerType the controller type assigned to Black
     * @param diceUpdater updates the displayed dice values
     * @param instructionUpdater updates the displayed player instructions
     */

    public AiTurnController(Game game, PlayerType whitePlayerType, PlayerType blackPlayerType,
                            Consumer<String> diceUpdater, Consumer<String> instructionUpdater)
    {
        this.game = game;
        this.board = game.getBoard();
        this.whitePlayerType = whitePlayerType;
        this.blackPlayerType = blackPlayerType;
        this.diceUpdater = diceUpdater;
        this.instructionUpdater = instructionUpdater;

        moveGenerator = new MoveGenerator();
        decisionRecorder = new DecisionRecorder();

        active = true;
    }

    /**
     * Determines whether the current player is controlled by an AI.
     *
     * @return true if the current player is AI-controlled, otherwise false
     */

    public boolean isAiTurn()
    {
        return getPlayerType(game.getCurrentPlayer()) != PlayerType.HUMAN;
    }

    /**
     * Schedules a turn for the current AI-controlled player.
     *
     * <p>The turn begins after a short interface delay. When processing is
     * complete, the appropriate handler is invoked depending on whether a legal
     * move was available.
     *
     * @param turnCompleteHandler handles a completed AI turn
     * @param noLegalMovesHandler handles a turn in which no legal moves are available
     */

    public void scheduleTurn(Consumer<AiTurnResult> turnCompleteHandler,
                             Runnable noLegalMovesHandler)
    {
        if (!active || !isAiTurn())
        {
            return;
        }

        aiPause = new PauseTransition(Duration.millis(AI_DELAY_MILLISECONDS));

        aiPause.setOnFinished(event ->
                playTurn(turnCompleteHandler, noLegalMovesHandler));

        aiPause.play();
    }

    /**
     * Stops the AI turn controller and cancels any currently scheduled turn.
     */

    public void stop()
    {
        active = false;

        if (aiPause != null)
        {
            aiPause.stop();
        }
    }

    /**
     * Processes a turn for the current AI-controlled player.
     *
     * <p>The method prepares the dice, checks for available legal moves, records
     * the AI decision, and passes the completed result to the supplied handler.
     *
     * @param turnCompleteHandler handles a completed AI turn
     * @param noLegalMovesHandler handles a turn in which no legal moves are available
     */

    private void playTurn(Consumer<AiTurnResult> turnCompleteHandler,
                          Runnable noLegalMovesHandler)
    {
        if (!active)
        {
            return;
        }

        Player player = game.getCurrentPlayer();
        AiPlayer aiPlayer = getAiPlayer();

        if (aiPlayer == null)
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
        instructionUpdater.accept(player + " AI is moving.");

        int legalSequenceCount = moveGenerator.generateMoveSequences(
                board, player, dice).size();

        if (legalSequenceCount == 0)
        {
            noLegalMovesHandler.run();
            return;
        }

        DecisionResult decisionResult = decisionRecorder.recordDecision(
                board, player, dice, aiPlayer, getPlayerType(player));

        DecisionStatistics statistics = decisionResult.getStatistics();

        AiTurnResult turnResult = new AiTurnResult(decisionResult.getMoveSequence(), statistics);

        turnCompleteHandler.accept(turnResult);
    }

    /**
     * Creates the AI implementation assigned to the current player.
     *
     * @return the AI player for the current player, or null if the current player
     *         is human-controlled
     */

    private AiPlayer getAiPlayer()
    {
        PlayerType playerType = getPlayerType(game.getCurrentPlayer());

        return switch (playerType)
        {
            case RANDOM_AI -> new RandomAi();
            case HEURISTIC_AI -> new HeuristicAi();
            case EXPECTIMAX_AI -> new ExpectimaxAi(EXPECTIMAX_SEARCH_DEPTH);
            case HUMAN -> null;
        };
    }

    private PlayerType getPlayerType(Player player)
    {
        return player == Player.WHITE ? whitePlayerType : blackPlayerType;
    }

    private void updateDiceLabel(Dice dice)
    {
        diceUpdater.accept("Dice: "
                + dice.getDieOne()
                + " | "
                + dice.getDieTwo());
    }

    /**
     * Contains the move sequence and statistics produced by a completed AI turn.
     *
     * @param sequence the move sequence selected by the AI
     * @param statistics the statistics recorded for the AI decision
     */

    public record AiTurnResult(MoveSequence sequence, DecisionStatistics statistics)
    {
    }
}
