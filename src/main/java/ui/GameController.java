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
import statistics.DecisionStatistics;
import statistics.GameStatistics;
import statistics.SearchStatistics;
import statistics.StatisticsRecorder;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class GameController
{
    private static final String HUMAN = "Human";
    private static final String RANDOM_AI = "Random AI";
    private static final String HEURISTIC_AI = "Heuristic AI";
    private static final String EXPECTIMAX_AI = "Expectimax AI";

    private static final double AI_DELAY_MILLISECONDS = 500;
    private static final int EXPECTIMAX_SEARCH_DEPTH = 2;

    private final Game game;
    private final Board board;
    private final MoveGenerator moveGenerator;
    private final BoardView boardView;

    private final String whitePlayerType;
    private final String blackPlayerType;

    private final Consumer<String> currentPlayerUpdater;
    private final Consumer<String> diceUpdater;
    private final Consumer<String> instructionUpdater;

    private final StatisticsRecorder statisticsRecorder;
    private final GameStatistics gameStatistics;

    private Integer selectedPoint;
    private List<MoveSequence> candidateSequences;

    private boolean diceRolled;
    private int moveIndex;
    private boolean gameOver;
    private boolean active;

    private PauseTransition aiPause;

    public GameController(Game game, BoardView boardView, String whitePlayerType,
                          String blackPlayerType, Consumer<String> currentPlayerUpdater,
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

        selectedPoint = null;
        candidateSequences = null;
        diceRolled = false;
        moveIndex = 0;
        gameOver = false;
        active = true;

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

        candidateSequences = moveGenerator.generateMoveSequences(board, game.getCurrentPlayer(),
                dice);

        moveIndex = 0;
        selectedPoint = null;
        boardView.clearHighlights();

        if (candidateSequences.isEmpty())
        {
            handleNoLegalMoves();
            return;
        }

        diceRolled = true;

        if (currentMoveRequiresBarEntry())
        {
            instructionUpdater.accept(
                    "You have a checker on the bar. Select it to re-enter.");
        }
        else
        {
            instructionUpdater.accept("Select a checker.");
        }
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
        if (!active || gameOver || isAiTurn() || !diceRolled || candidateSequences == null
                || candidateSequences.isEmpty())
        {
            return;
        }

        Player currentPlayer = game.getCurrentPlayer();

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
                instructionUpdater.accept(
                        "You must enter your checker from the bar first.");
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
                applySelectedMove(selectedMove);
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
            applySelectedMove(selectedMove);
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

    private boolean isAiTurn()
    {
        String playerType;

        if (game.getCurrentPlayer() == Player.WHITE)
        {
            playerType = whitePlayerType;
        }
        else
        {
            playerType = blackPlayerType;
        }

        return !HUMAN.equals(playerType);
    }

    private AiPlayer getAiPlayer()
    {
        String playerType;

        if (game.getCurrentPlayer() == Player.WHITE)
        {
            playerType = whitePlayerType;
        }
        else
        {
            playerType = blackPlayerType;
        }

        if (RANDOM_AI.equals(playerType))
        {
            return new RandomAi();
        }

        if (HEURISTIC_AI.equals(playerType))
        {
            return new HeuristicAi();
        }

        if (EXPECTIMAX_AI.equals(playerType))
        {
            return new ExpectimaxAi(EXPECTIMAX_SEARCH_DEPTH);
        }

        return null;
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

        int legalSequenceCount = moveGenerator.generateMoveSequences(board, player, dice).size();

        long startTime = System.nanoTime();

        MoveSequence sequence = aiPlayer.chooseMove(board, player, dice);

        long decisionTimeNanoseconds = System.nanoTime() - startTime;

        SearchStatistics searchStatistics = createSearchStatistics(aiPlayer);

        Dice recordedDice = new Dice(dice.getDieOne(), dice.getDieTwo());

        DecisionStatistics decisionStatistics = new DecisionStatistics(player, getPlayerType(player),
                recordedDice, legalSequenceCount, decisionTimeNanoseconds, searchStatistics);

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

        return new SearchStatistics(expectimaxAi.getNodesEvaluated(), expectimaxAi.getSearchDepth(),
                expectimaxAi.getNodeBudget(), expectimaxAi.wasBudgetReached());
    }

    private String getPlayerType(Player player)
    {
        if (player == Player.WHITE)
        {
            return whitePlayerType;
        }

        return blackPlayerType;
    }

    private void applySelectedMove(Move selectedMove)
    {
        board.applyMove(selectedMove);
        filterSequences(selectedMove);

        moveIndex++;
        selectedPoint = null;

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

        if (turnIsComplete())
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

        if (currentMoveRequiresBarEntry())
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
        candidateSequences = null;
        moveIndex = 0;
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

    private Set<Integer> findDestinations(int fromPoint)
    {
        Set<Integer> destinations = new HashSet<>();

        for (MoveSequence sequence : candidateSequences)
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
        for (MoveSequence sequence : candidateSequences)
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

        for (MoveSequence sequence : candidateSequences)
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
        for (MoveSequence sequence : candidateSequences)
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

    private boolean currentMoveRequiresBarEntry()
    {
        if (candidateSequences == null)
        {
            return false;
        }

        for (MoveSequence sequence : candidateSequences)
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

    private void filterSequences(Move selectedMove)
    {
        List<MoveSequence> filtered = new ArrayList<>();

        for (MoveSequence sequence : candidateSequences)
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

        candidateSequences = filtered;
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

    private boolean turnIsComplete()
    {
        for (MoveSequence sequence : candidateSequences)
        {
            if (sequence.size() > moveIndex)
            {
                return false;
            }
        }

        return true;
    }
}
