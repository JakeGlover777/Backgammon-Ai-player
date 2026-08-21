package ui;

import ai.AiPlayer;
import ai.RandomAi;
import game.Board;
import game.Dice;
import game.Game;
import game.Move;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameUi extends Application
{
    private Integer selectedPoint = null;
    private List<MoveSequence> candidateSequences;

    private boolean diceRolled = false;
    private int moveIndex = 0;

    private String whitePlayerType;
    private String blackPlayerType;
    private boolean gameOver = false;

    @Override
    public void start(Stage stage)
    {
        showSetupScreen(stage);
    }

    private void showSetupScreen(Stage stage)
    {
        Label title = new Label("Backgammon AI");

        Label whiteLabel = new Label("White Player");

        ComboBox<String> whitePlayerBox =
                new ComboBox<>();

        whitePlayerBox.getItems().addAll(
                "Human",
                "Random AI",
                "Heuristic AI",
                "Expectimax AI"
        );

        whitePlayerBox.setValue("Human");

        Label blackLabel =
                new Label("Black Player");

        ComboBox<String> blackPlayerBox =
                new ComboBox<>();

        blackPlayerBox.getItems().addAll(
                "Human",
                "Random AI",
                "Heuristic AI",
                "Expectimax AI"
        );

        blackPlayerBox.setValue(
                "Heuristic AI"
        );

        Button startButton =
                new Button("Start Game");

        startButton.setOnAction(event ->
                showGameScreen(
                        stage,
                        whitePlayerBox.getValue(),
                        blackPlayerBox.getValue()
                )
        );

        VBox layout = new VBox(
                15,
                title,
                whiteLabel,
                whitePlayerBox,
                blackLabel,
                blackPlayerBox,
                startButton
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Scene scene =
                new Scene(layout, 900, 650);

        stage.setTitle("Backgammon AI");
        stage.setScene(scene);
        stage.show();
    }

    private void showGameScreen(
            Stage stage,
            String whitePlayer,
            String blackPlayer)
    {
        Game game = new Game();

        whitePlayerType = whitePlayer;
        blackPlayerType = blackPlayer;
        gameOver = false;

        Board board =
                game.getBoard();

        MoveGenerator moveGenerator =
                new MoveGenerator();

        selectedPoint = null;
        candidateSequences = null;
        diceRolled = false;
        moveIndex = 0;

        Label playerTypesLabel =
                new Label(
                        "White: "
                                + whitePlayer
                                + " | Black: "
                                + blackPlayer
                );

        Label currentPlayerLabel =
                new Label(
                        "Current Player: "
                                + game.getCurrentPlayer()
                );

        Label diceLabel =
                new Label("Dice: - | -");

        Label instructionLabel =
                new Label("Roll the dice.");

        Button rollButton =
                new Button("Roll Dice");

        BoardView boardView =
                new BoardView(board);

        rollButton.setOnAction(event ->
        {
            if (gameOver
                    || diceRolled
                    || isRandomAiTurn(game))
            {
                return;
            }

            Dice dice =
                    game.getDice();

            dice.roll();

            diceLabel.setText(
                    "Dice: "
                            + dice.getDieOne()
                            + " | "
                            + dice.getDieTwo()
            );

            candidateSequences =
                    moveGenerator
                            .generateMoveSequences(
                                    board,
                                    game.getCurrentPlayer(),
                                    dice
                            );

            moveIndex = 0;
            selectedPoint = null;

            boardView.clearHighlights();

            if (candidateSequences.isEmpty())
            {
                instructionLabel.setText(
                        "No legal moves. Turn skipped."
                );

                game.switchPlayer();

                currentPlayerLabel.setText(
                        "Current Player: "
                                + game.getCurrentPlayer()
                );

                diceLabel.setText(
                        "Dice: - | -"
                );

                scheduleAiTurn(
                        game,
                        board,
                        boardView,
                        currentPlayerLabel,
                        diceLabel,
                        instructionLabel
                );

                return;
            }

            diceRolled = true;

            if (currentMoveRequiresBarEntry())
            {
                instructionLabel.setText(
                        "You have a checker on the bar. "
                                + "Select it to re-enter."
                );
            }
            else
            {
                instructionLabel.setText(
                        "Select a checker."
                );
            }
        });

        boardView.setOnPointClicked(
                pointIndex ->
                {
                    if (gameOver
                            || isRandomAiTurn(game)
                            || !diceRolled
                            || candidateSequences == null
                            || candidateSequences.isEmpty())
                    {
                        return;
                    }

                    Player currentPlayer =
                            game.getCurrentPlayer();

                    if (currentMoveRequiresBarEntry())
                    {
                        int correctBar =
                                currentPlayer
                                        == Player.WHITE
                                        ? BoardView.WHITE_BAR
                                        : BoardView.BLACK_BAR;

                        if (selectedPoint == null)
                        {
                            if (pointIndex
                                    != correctBar)
                            {
                                instructionLabel.setText(
                                        "You must enter your checker "
                                                + "from the bar first."
                                );

                                return;
                            }

                            selectedPoint =
                                    correctBar;

                            Set<Integer> destinations =
                                    findBarDestinations();

                            boardView.highlightPoints(
                                    destinations
                            );

                            instructionLabel.setText(
                                    "Select a highlighted entry point."
                            );

                            return;
                        }

                        if (selectedPoint
                                == correctBar)
                        {
                            Move selectedMove =
                                    findBarMove(
                                            pointIndex
                                    );

                            if (selectedMove != null)
                            {
                                applySelectedMove(
                                        selectedMove,
                                        game,
                                        board,
                                        boardView,
                                        currentPlayerLabel,
                                        diceLabel,
                                        instructionLabel
                                );

                                return;
                            }
                        }

                        instructionLabel.setText(
                                "That is not a legal entry point."
                        );

                        return;
                    }

                    if (selectedPoint == null)
                    {
                        if (pointIndex < 0
                                || board.getPoint(pointIndex)
                                .getOwner()
                                != currentPlayer)
                        {
                            instructionLabel.setText(
                                    "Select one of your own checkers."
                            );

                            return;
                        }

                        Set<Integer> destinations =
                                findDestinations(
                                        pointIndex
                                );

                        if (destinations.isEmpty())
                        {
                            instructionLabel.setText(
                                    "That checker cannot move."
                            );

                            return;
                        }

                        selectedPoint =
                                pointIndex;

                        boardView.highlightPoints(
                                destinations
                        );

                        instructionLabel.setText(
                                "Select a highlighted destination."
                        );

                        return;
                    }

                    if (pointIndex
                            == BoardView.WHITE_BAR
                            || pointIndex
                            == BoardView.BLACK_BAR)
                    {
                        instructionLabel.setText(
                                "Select a legal destination."
                        );

                        return;
                    }

                    Move selectedMove =
                            findMove(
                                    selectedPoint,
                                    pointIndex
                            );

                    if (selectedMove != null)
                    {
                        applySelectedMove(
                                selectedMove,
                                game,
                                board,
                                boardView,
                                currentPlayerLabel,
                                diceLabel,
                                instructionLabel
                        );

                        return;
                    }

                    if (pointIndex < 0)
                    {
                        instructionLabel.setText(
                                "That is not a legal destination."
                        );

                        return;
                    }

                    if (board.getPoint(pointIndex)
                            .getOwner()
                            == currentPlayer)
                    {
                        Set<Integer> destinations =
                                findDestinations(
                                        pointIndex
                                );

                        if (!destinations.isEmpty())
                        {
                            selectedPoint =
                                    pointIndex;

                            boardView.highlightPoints(
                                    destinations
                            );

                            instructionLabel.setText(
                                    "Select a highlighted destination."
                            );

                            return;
                        }
                    }

                    instructionLabel.setText(
                            "That is not a legal destination."
                    );
                });

        Button backButton =
                new Button("Back");

        backButton.setOnAction(event ->
                showSetupScreen(stage)
        );

        VBox topSection = new VBox(
                8,
                playerTypesLabel,
                currentPlayerLabel,
                diceLabel,
                rollButton,
                instructionLabel
        );

        topSection.setAlignment(
                Pos.CENTER
        );

        HBox bottomSection =
                new HBox(backButton);

        bottomSection.setAlignment(
                Pos.CENTER
        );

        BorderPane layout =
                new BorderPane();

        layout.setTop(topSection);
        layout.setCenter(boardView);
        layout.setBottom(bottomSection);

        layout.setPadding(
                new Insets(20)
        );

        Scene scene =
                new Scene(
                        layout,
                        900,
                        720
                );

        stage.setScene(scene);

        scheduleAiTurn(
                game,
                board,
                boardView,
                currentPlayerLabel,
                diceLabel,
                instructionLabel
        );
    }

    private boolean isRandomAiTurn(
            Game game)
    {
        if (game.getCurrentPlayer()
                == Player.WHITE)
        {
            return "Random AI".equals(
                    whitePlayerType
            );
        }

        return "Random AI".equals(
                blackPlayerType
        );
    }

    private void scheduleAiTurn(
            Game game,
            Board board,
            BoardView boardView,
            Label currentPlayerLabel,
            Label diceLabel,
            Label instructionLabel)
    {
        if (gameOver
                || !isRandomAiTurn(game))
        {
            return;
        }

        PauseTransition pause =
                new PauseTransition(
                        Duration.millis(500)
                );

        pause.setOnFinished(event ->
                playRandomAiTurn(
                        game,
                        board,
                        boardView,
                        currentPlayerLabel,
                        diceLabel,
                        instructionLabel
                )
        );

        pause.play();
    }

    private void playRandomAiTurn(
            Game game,
            Board board,
            BoardView boardView,
            Label currentPlayerLabel,
            Label diceLabel,
            Label instructionLabel)
    {
        if (gameOver)
        {
            return;
        }

        Player player =
                game.getCurrentPlayer();

        Dice dice =
                game.getDice();

        dice.roll();

        diceLabel.setText(
                "Dice: "
                        + dice.getDieOne()
                        + " | "
                        + dice.getDieTwo()
        );

        instructionLabel.setText(
                player
                        + " Random AI is moving."
        );

        AiPlayer randomAi =
                new RandomAi();

        MoveSequence sequence =
                randomAi.chooseMove(
                        board,
                        player,
                        dice
                );

        if (!sequence.isEmpty())
        {
            for (Move move
                    : sequence.getMoves())
            {
                board.applyMove(move);
            }
        }

        boardView.clearHighlights();
        boardView.refresh();

        Player winner =
                game.getWinner();

        if (winner != Player.NONE)
        {
            gameOver = true;

            currentPlayerLabel.setText(
                    "Winner: " + winner
            );

            diceLabel.setText(
                    "Dice: - | -"
            );

            instructionLabel.setText(
                    winner + " wins the game!"
            );

            return;
        }

        game.switchPlayer();

        currentPlayerLabel.setText(
                "Current Player: "
                        + game.getCurrentPlayer()
        );

        diceLabel.setText(
                "Dice: - | -"
        );

        instructionLabel.setText(
                "Turn complete."
        );

        scheduleAiTurn(
                game,
                board,
                boardView,
                currentPlayerLabel,
                diceLabel,
                instructionLabel
        );
    }

    private void applySelectedMove(
            Move selectedMove,
            Game game,
            Board board,
            BoardView boardView,
            Label currentPlayerLabel,
            Label diceLabel,
            Label instructionLabel)
    {
        board.applyMove(
                selectedMove
        );

        filterSequences(
                selectedMove
        );

        moveIndex++;
        selectedPoint = null;

        boardView.clearHighlights();
        boardView.refresh();

        Player winner =
                game.getWinner();

        if (winner != Player.NONE)
        {
            gameOver = true;

            currentPlayerLabel.setText(
                    "Winner: " + winner
            );

            diceLabel.setText(
                    "Dice: - | -"
            );

            instructionLabel.setText(
                    winner + " wins the game!"
            );

            diceRolled = false;
            candidateSequences = null;
            moveIndex = 0;

            return;
        }

        if (turnIsComplete())
        {
            game.switchPlayer();

            currentPlayerLabel.setText(
                    "Current Player: "
                            + game.getCurrentPlayer()
            );

            diceLabel.setText(
                    "Dice: - | -"
            );

            instructionLabel.setText(
                    "Turn complete. Roll the dice."
            );

            diceRolled = false;
            candidateSequences = null;
            moveIndex = 0;

            scheduleAiTurn(
                    game,
                    board,
                    boardView,
                    currentPlayerLabel,
                    diceLabel,
                    instructionLabel
            );

            return;
        }

        if (currentMoveRequiresBarEntry())
        {
            instructionLabel.setText(
                    "Select your checker on the bar."
            );
        }
        else
        {
            instructionLabel.setText(
                    "Select your next checker."
            );
        }
    }

    private Set<Integer> findDestinations(
            int fromPoint)
    {
        Set<Integer> destinations =
                new HashSet<>();

        for (MoveSequence sequence
                : candidateSequences)
        {
            if (sequence.size()
                    <= moveIndex)
            {
                continue;
            }

            Move move =
                    sequence.getMoves()
                            .get(moveIndex);

            if (move.isEnteringFromBar())
            {
                continue;
            }

            if (move.isBearingOff()
                    && move.getFromPoint()
                    == fromPoint)
            {
                int bearOffDestination =
                        move.getPlayer()
                                == Player.WHITE
                                ? BoardView.WHITE_BEAR_OFF
                                : BoardView.BLACK_BEAR_OFF;

                destinations.add(
                        bearOffDestination
                );

                continue;
            }

            if (move.getFromPoint()
                    == fromPoint)
            {
                destinations.add(
                        move.getToPoint()
                );
            }
        }

        return destinations;
    }

    private Move findMove(
            int fromPoint,
            int toPoint)
    {
        for (MoveSequence sequence
                : candidateSequences)
        {
            if (sequence.size()
                    <= moveIndex)
            {
                continue;
            }

            Move move =
                    sequence.getMoves()
                            .get(moveIndex);

            if (move.isEnteringFromBar())
            {
                continue;
            }

            if (move.isBearingOff())
            {
                int bearOffDestination =
                        move.getPlayer()
                                == Player.WHITE
                                ? BoardView.WHITE_BEAR_OFF
                                : BoardView.BLACK_BEAR_OFF;

                if (move.getFromPoint()
                        == fromPoint
                        && toPoint
                        == bearOffDestination)
                {
                    return move;
                }

                continue;
            }

            if (move.getFromPoint()
                    == fromPoint
                    && move.getToPoint()
                    == toPoint)
            {
                return move;
            }
        }

        return null;
    }

    private Set<Integer> findBarDestinations()
    {
        Set<Integer> destinations =
                new HashSet<>();

        for (MoveSequence sequence
                : candidateSequences)
        {
            if (sequence.size()
                    <= moveIndex)
            {
                continue;
            }

            Move move =
                    sequence.getMoves()
                            .get(moveIndex);

            if (move.isEnteringFromBar())
            {
                destinations.add(
                        move.getToPoint()
                );
            }
        }

        return destinations;
    }

    private Move findBarMove(
            int toPoint)
    {
        for (MoveSequence sequence
                : candidateSequences)
        {
            if (sequence.size()
                    <= moveIndex)
            {
                continue;
            }

            Move move =
                    sequence.getMoves()
                            .get(moveIndex);

            if (move.isEnteringFromBar()
                    && move.getToPoint()
                    == toPoint)
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

        for (MoveSequence sequence
                : candidateSequences)
        {
            if (sequence.size()
                    <= moveIndex)
            {
                continue;
            }

            Move move =
                    sequence.getMoves()
                            .get(moveIndex);

            if (move.isEnteringFromBar())
            {
                return true;
            }
        }

        return false;
    }

    private void filterSequences(
            Move selectedMove)
    {
        List<MoveSequence> filtered =
                new ArrayList<>();

        for (MoveSequence sequence
                : candidateSequences)
        {
            if (sequence.size()
                    <= moveIndex)
            {
                continue;
            }

            Move move =
                    sequence.getMoves()
                            .get(moveIndex);

            if (movesMatch(
                    move,
                    selectedMove))
            {
                filtered.add(
                        sequence
                );
            }
        }

        candidateSequences =
                filtered;
    }

    private boolean movesMatch(
            Move first,
            Move second)
    {
        if (first.isEnteringFromBar()
                != second.isEnteringFromBar())
        {
            return false;
        }

        if (first.isBearingOff()
                != second.isBearingOff())
        {
            return false;
        }

        if (first.getDieValue()
                != second.getDieValue())
        {
            return false;
        }

        if (!first.isEnteringFromBar()
                && first.getFromPoint()
                != second.getFromPoint())
        {
            return false;
        }

        if (!first.isBearingOff()
                && first.getToPoint()
                != second.getToPoint())
        {
            return false;
        }

        return true;
    }

    private boolean turnIsComplete()
    {
        for (MoveSequence sequence
                : candidateSequences)
        {
            if (sequence.size()
                    > moveIndex)
            {
                return false;
            }
        }

        return true;
    }
}