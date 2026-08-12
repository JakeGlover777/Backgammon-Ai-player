package ui;

import game.Board;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameUi extends Application
{
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

        Scene scene = new Scene(layout, 900, 650);

        stage.setTitle("Backgammon AI");
        stage.setScene(scene);
        stage.show();
    }

    private void showGameScreen(
            Stage stage,
            String whitePlayer,
            String blackPlayer)
    {
        Board board = new Board();

        Label topLabel = new Label(
                "White: " + whitePlayer
                        + " | Black: " + blackPlayer
        );

        BoardView boardView = new BoardView(board);

        Button backButton = new Button("Back");

        backButton.setOnAction(event ->
                showSetupScreen(stage)
        );

        BorderPane layout = new BorderPane();

        layout.setTop(topLabel);
        layout.setCenter(boardView);
        layout.setBottom(backButton);

        BorderPane.setAlignment(topLabel, Pos.CENTER);
        BorderPane.setAlignment(boardView, Pos.CENTER);
        BorderPane.setAlignment(backButton, Pos.CENTER);

        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 900, 650);

        stage.setScene(scene);
    }
}