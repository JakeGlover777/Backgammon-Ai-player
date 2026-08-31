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

    public boolean isAiTurn()
    {
        return getPlayerType(game.getCurrentPlayer()) != PlayerType.HUMAN;
    }

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

    public void stop()
    {
        active = false;

        if (aiPause != null)
        {
            aiPause.stop();
        }
    }

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

    public record AiTurnResult(MoveSequence sequence, DecisionStatistics statistics)
    {
    }
}
