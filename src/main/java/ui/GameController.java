package ui;

import ai.AiPlayer;
import ai.ExpectimaxAi;
import ai.HeuristicAi;
import ai.RandomAi;
import game.Board;
import game.Dice;
import game.Game;
import game.Move;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;
import game.PlayerType;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import statistics.DecisionStatistics;
import statistics.GameStatistics;
import statistics.SearchStatistics;
import statistics.StatisticsRecorder;

import java.util.List;
import java.util.function.Consumer;

public class GameController
{
    private static final double AI_DELAY_MILLISECONDS = 500;
    private static final int EXPECTIMAX_SEARCH_DEPTH = 2;

    private final Game game;
    private final Board board;
    private final MoveGenerator moveGenerator;
    private final BoardView boardView;
    private final HumanTurnController humanTurnController;

    private final PlayerType whitePlayerType;
    private final PlayerType blackPlayerType;

    private final Consumer<String> currentPlayerUpdater;
    private final Consumer<String> diceUpdater;
    private final Consumer<String> instructionUpdater;

    private final StatisticsRecorder statisticsRecorder;
    private final GameStatistics gameStatistics;

    private boolean diceRolled;
    private boolean gameOver;
    private boolean active;

    private PauseTransition aiPause;

    public GameController(Game game, BoardView boardView, PlayerType whitePlayerType,
                          PlayerType blackPlayerType, Consumer<String> currentPlayerUpdater,
                          Consumer<String> diceUpdater, Consumer<String> instructionUpdater)
    {
        this.game = game;
        this.board = game.getBoard();
        this.boardView = boardView;
        this.whitePlayerType = whitePlayerType;
        this.blackPlayerType = blackPlayerType;
        this.currentPlayerUpdater = currentPlayerUpdater;
        this.diceUpdater = diceUpdater;
        this.instructionUpdater = instructionUpdater;

        moveGenerator = new MoveGenerator();

        statisticsRecorder = new StatisticsRecorder();
        gameStatistics = new GameStatistics(1, whitePlayerType, blackPlayerType);

        diceRolled = false;
        gameOver = false;
        active = true;

        humanTurnController = new HumanTurnController(
                board, boardView, instructionUpdater, this::applySelectedMove);

        boardView.setOnPointClicked(this::handleBoardClick);
    }

    public void start()
    {
        scheduleAiTurn();
    }

    public void stop()
    {
        active = false;

        if (aiPause != null)
        {
            aiPause.stop();
        }
    }

    public void rollDice()
    {
        if (!active || gameOver || diceRolled || isAiTurn())
        {
            return;
        }

        Dice dice = game.getDice();
        dice.roll();

        updateDiceLabel(dice);

        List<MoveSequence> legalSequences = moveGenerator.generateMoveSequences(
                board, game.getCurrentPlayer(), dice);

        if (legalSequences.isEmpty())
        {
            handleNoLegalMoves();
            return;
        }

        diceRolled = true;
        humanTurnController.startTurn(legalSequences);
    }

    private void handleNoLegalMoves()
    {
        instructionUpdater.accept("No legal moves. Turn skipped.");

        gameStatistics.incrementTurnCount();

        game.switchPlayer();

        updateCurrentPlayerLabel();
        resetDiceLabel();

        scheduleAiTurn();
    }

    private void handleBoardClick(int pointIndex)
    {
        if (!active || gameOver || isAiTurn() || !diceRolled)
        {
            return;
        }

        humanTurnController.handleBoardClick(pointIndex, game.getCurrentPlayer());
    }

    private boolean isAiTurn()
    {
        return getPlayerType(game.getCurrentPlayer()) != PlayerType.HUMAN;
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

    private void scheduleAiTurn()
    {
        if (!active || gameOver || !isAiTurn())
        {
            return;
        }

        aiPause = new PauseTransition(Duration.millis(AI_DELAY_MILLISECONDS));
        aiPause.setOnFinished(event -> playAiTurn());
        aiPause.play();
    }

    private void playAiTurn()
    {
        if (!active || gameOver)
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
            handleNoLegalMoves();
            return;
        }

        long startTime = System.nanoTime();

        MoveSequence sequence = aiPlayer.chooseMove(board, player, dice);

        long decisionTimeNanoseconds = System.nanoTime() - startTime;

        SearchStatistics searchStatistics = createSearchStatistics(aiPlayer);

        Dice recordedDice = new Dice(dice.getDieOne(), dice.getDieTwo());

        DecisionStatistics decisionStatistics = new DecisionStatistics(
                player,
                getPlayerType(player),
                recordedDice,
                legalSequenceCount,
                decisionTimeNanoseconds,
                searchStatistics);

        gameStatistics.recordDecision(decisionStatistics);

        for (Move move : sequence.getMoves())
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

    private SearchStatistics createSearchStatistics(AiPlayer aiPlayer)
    {
        if (!(aiPlayer instanceof ExpectimaxAi))
        {
            return null;
        }

        ExpectimaxAi expectimaxAi = (ExpectimaxAi) aiPlayer;

        return new SearchStatistics(
                expectimaxAi.getNodesEvaluated(),
                expectimaxAi.getSearchDepth(),
                expectimaxAi.getNodeBudget(),
                expectimaxAi.wasBudgetReached());
    }

    private PlayerType getPlayerType(Player player)
    {
        return player == Player.WHITE ? whitePlayerType : blackPlayerType;
    }

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
        currentPlayerUpdater.accept(
                "Current Player: " + game.getCurrentPlayer());
    }

    private void updateDiceLabel(Dice dice)
    {
        diceUpdater.accept(
                "Dice: " + dice.getDieOne() + " | " + dice.getDieTwo());
    }

    private void resetDiceLabel()
    {
        diceUpdater.accept("Dice: - | -");
    }
}