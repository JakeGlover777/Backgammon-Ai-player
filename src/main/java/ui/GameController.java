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

        humanTurnController = new HumanTurnController(
                board, boardView, instructionUpdater, this::applySelectedMove);

        aiTurnController = new AiTurnController(
                game, whitePlayerType, blackPlayerType, diceUpdater, instructionUpdater);

        boardView.setOnPointClicked(this::handleBoardClick);
    }

    public void start()
    {
        scheduleAiTurn();
    }

    public void stop()
    {
        active = false;
        aiTurnController.stop();
    }

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