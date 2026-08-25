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
import statistics.DecisionStatistics;
import statistics.SearchStatistics;

import java.util.function.Consumer;

public class AiTurnController
{
    private static final double AI_DELAY_MILLISECONDS = 500;
    private static final int EXPECTIMAX_SEARCH_DEPTH = 2;

    private final Game game;
    private final Board board;
    private final MoveGenerator moveGenerator;

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

        active = true;
    }

    public boolean isAiTurn()
    {
        return getPlayerType(game.getCurrentPlayer()) != PlayerType.HUMAN;
    }

    public void scheduleTurn(Consumer<AiTurnResult> turnCompleteHandler, Runnable noLegalMovesHandler)
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
        dice.roll();

        updateDiceLabel(dice);
        instructionUpdater.accept(player + " AI is moving.");

        int legalSequenceCount = moveGenerator.generateMoveSequences(
                board, player, dice).size();

        if (legalSequenceCount == 0)
        {
            noLegalMovesHandler.run();
            return;
        }

        long startTime = System.nanoTime();

        MoveSequence sequence = aiPlayer.chooseMove(board, player, dice);

        long decisionTimeNanoseconds = System.nanoTime() - startTime;

        SearchStatistics searchStatistics = createSearchStatistics(aiPlayer);

        Dice recordedDice = new Dice(dice.getDieOne(), dice.getDieTwo());

        DecisionStatistics decisionStatistics = new DecisionStatistics(player, getPlayerType(player),
                recordedDice, legalSequenceCount, decisionTimeNanoseconds, searchStatistics);

        AiTurnResult result = new AiTurnResult(sequence, decisionStatistics);

        turnCompleteHandler.accept(result);
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

    private SearchStatistics createSearchStatistics(AiPlayer aiPlayer)
    {
        if (!(aiPlayer instanceof ExpectimaxAi))
        {
            return null;
        }

        ExpectimaxAi expectimaxAi = (ExpectimaxAi) aiPlayer;

        return new SearchStatistics(expectimaxAi.getNodesEvaluated(), expectimaxAi.getSearchDepth(),
                expectimaxAi.getNodeBudget(), expectimaxAi.wasBudgetReached());
    }

    private PlayerType getPlayerType(Player player)
    {
        return player == Player.WHITE ? whitePlayerType : blackPlayerType;
    }

    private void updateDiceLabel(Dice dice)
    {
        diceUpdater.accept(
                "Dice: " + dice.getDieOne() + " | " + dice.getDieTwo());
    }

    public record AiTurnResult(MoveSequence sequence, DecisionStatistics statistics)
    {
    }
}