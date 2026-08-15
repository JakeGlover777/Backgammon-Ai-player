package ui;

import game.Board;
import game.Dice;
import game.Game;
import game.Move;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;

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

    @Override
    public void start(Stage stage)
    {
        showSetupScreen(stage);
    }

    private void showSetupScreen(Stage stage)
    {
        Label title = new Label("Backgammon AI");

        Label whiteLabel = new Label("White Player");

        ComboBox<String> whitePlayerBox = new ComboBox<>();

        whitePlayerBox.getItems().addAll(
                "Human",
                "Random AI",
                "Heuristic AI",
                "Expectimax AI"
        );

        whitePlayerBox.setValue("Human");

        Label blackLabel = new Label("Black Player");

        ComboBox<String> blackPlayerBox = new ComboBox<>();

        blackPlayerBox.getItems().addAll(
                "Human",
                "Random AI",
                "Heuristic AI",
                "Expectimax AI"
        );

        blackPlayerBox.setValue("Heuristic AI");

        Button startButton = new Button("Start Game");

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

        Scene scene = new Scene(
                layout,
                900,
                650
        );

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

        Board board = game.getBoard();

        MoveGenerator moveGenerator =
                new MoveGenerator();

        selectedPoint = null;
        candidateSequences = null;
        diceRolled = false;
        moveIndex = 0;

        Label playerTypesLabel = new Label(
                "White: " + whitePlayer
                        + " | Black: "
                        + blackPlayer
        );

        Label currentPlayerLabel = new Label(
                "Current Player: "
                        + game.getCurrentPlayer()
        );

        Label diceLabel = new Label(
                "Dice: - | -"
        );

        Label instructionLabel = new Label(
                "Roll the dice."
        );

        Button rollButton =
                new Button("Roll Dice");

        BoardView boardView =
                new BoardView(board);

        rollButton.setOnAction(event ->
        {
            if (diceRolled)
            {
                return;
            }

            Dice dice = game.getDice();

            dice.roll();

            diceLabel.setText(
                    "Dice: "
                            + dice.getDieOne()
                            + " | "
                            + dice.getDieTwo()
            );

            candidateSequences =
                    moveGenerator.generateMoveSequences(
                            board,
                            game.getCurrentPlayer(),
                            dice
                    );

            moveIndex = 0;
            selectedPoint = null;

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

                return;
            }

            diceRolled = true;

            instructionLabel.setText(
                    "Select a checker."
            );
        });

        boardView.setOnPointClicked(pointIndex ->
        {
            if (!diceRolled
                    || candidateSequences == null
                    || candidateSequences.isEmpty())
            {
                return;
            }

            Player currentPlayer =
                    game.getCurrentPlayer();

            if (hasBarEntryMove())
            {
                instructionLabel.setText(
                        "Bar entry controls are the next feature."
                );

                return;
            }

            if (selectedPoint == null)
            {
                if (board.getPoint(pointIndex)
                        .getOwner() != currentPlayer)
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

                selectedPoint = pointIndex;

                boardView.highlightPoints(
                        destinations
                );

                instructionLabel.setText(
                        "Select a highlighted destination."
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
                board.applyMove(selectedMove);

                filterSequences(selectedMove);

                moveIndex++;

                selectedPoint = null;

                boardView.clearHighlights();
                boardView.refresh();

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
                }
                else
                {
                    instructionLabel.setText(
                            "Select your next checker."
                    );
                }

                return;
            }

            if (board.getPoint(pointIndex)
                    .getOwner() == currentPlayer)
            {
                Set<Integer> destinations =
                        findDestinations(
                                pointIndex
                        );

                if (!destinations.isEmpty())
                {
                    selectedPoint = pointIndex;

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
    }

    private Set<Integer> findDestinations(
            int fromPoint)
    {
        Set<Integer> destinations =
                new HashSet<>();

        for (MoveSequence sequence
                : candidateSequences)
        {
            if (sequence.size() <= moveIndex)
            {
                continue;
            }

            Move move =
                    sequence.getMoves()
                            .get(moveIndex);

            if (move.isEnteringFromBar()
                    || move.isBearingOff())
            {
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
            if (sequence.size() <= moveIndex)
            {
                continue;
            }

            Move move =
                    sequence.getMoves()
                            .get(moveIndex);

            if (move.isEnteringFromBar()
                    || move.isBearingOff())
            {
                continue;
            }

            if (move.getFromPoint() == fromPoint
                    && move.getToPoint() == toPoint)
            {
                return move;
            }
        }

        return null;
    }

    private void filterSequences(
            Move selectedMove)
    {
        List<MoveSequence> filtered =
                new ArrayList<>();

        for (MoveSequence sequence
                : candidateSequences)
        {
            if (sequence.size() <= moveIndex)
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
                filtered.add(sequence);
            }
        }

        candidateSequences = filtered;
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
            if (sequence.size() > moveIndex)
            {
                return false;
            }
        }

        return true;
    }

    private boolean hasBarEntryMove()
    {
        for (MoveSequence sequence
                : candidateSequences)
        {
            if (sequence.size() <= moveIndex)
            {
                continue;
            }

            if (sequence.getMoves()
                    .get(moveIndex)
                    .isEnteringFromBar())
            {
                return true;
            }
        }

        return false;
    }
}