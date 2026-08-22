package ui;

import game.Game;

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

public class GameUi extends Application
{
    private static final String HUMAN = "Human";
    private static final String RANDOM_AI = "Random AI";
    private static final String HEURISTIC_AI = "Heuristic AI";
    private static final String EXPECTIMAX_AI = "Expectimax AI";

    private static final double SETUP_SPACING = 15;
    private static final double GAME_SPACING = 8;
    private static final double SETUP_PADDING = 30;
    private static final double GAME_PADDING = 20;

    private static final double SETUP_WIDTH = 900;
    private static final double SETUP_HEIGHT = 650;
    private static final double GAME_WIDTH = 900;
    private static final double GAME_HEIGHT = 720;

    @Override
    public void start(Stage stage)
    {
        showSetupScreen(stage);
    }

    private void showSetupScreen(Stage stage)
    {
        Label title = new Label("Backgammon AI");
        Label whiteLabel = new Label("White Player");
        Label blackLabel = new Label("Black Player");

        ComboBox<String> whitePlayerBox = createPlayerSelection(HUMAN);
        ComboBox<String> blackPlayerBox = createPlayerSelection(HEURISTIC_AI);

        Button startButton = new Button("Start Game");
        startButton.setOnAction(event ->
                showGameScreen(stage, whitePlayerBox.getValue(), blackPlayerBox.getValue()));

        VBox layout = new VBox(SETUP_SPACING, title, whiteLabel, whitePlayerBox,
                blackLabel, blackPlayerBox, startButton);

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(SETUP_PADDING));

        Scene scene = new Scene(layout, SETUP_WIDTH, SETUP_HEIGHT);

        stage.setTitle("Backgammon AI");
        stage.setScene(scene);
        stage.show();
    }

    private ComboBox<String> createPlayerSelection(String defaultPlayer)
    {
        ComboBox<String> playerBox = new ComboBox<>();

        playerBox.getItems().addAll(HUMAN, RANDOM_AI, HEURISTIC_AI, EXPECTIMAX_AI);
        playerBox.setValue(defaultPlayer);

        return playerBox;
    }

    private void showGameScreen(Stage stage, String whitePlayer, String blackPlayer)
    {
        Game game = new Game();
        BoardView boardView = new BoardView(game.getBoard());

        Label playerTypesLabel =
                new Label("White: " + whitePlayer + " | Black: " + blackPlayer);
        Label currentPlayerLabel =
                new Label("Current Player: " + game.getCurrentPlayer());
        Label diceLabel = new Label("Dice: - | -");
        Label instructionLabel = new Label("Roll the dice.");

        Button rollButton = new Button("Roll Dice");
        Button backButton = new Button("Back");

        GameController controller = new GameController(
                game,
                boardView,
                whitePlayer,
                blackPlayer,
                currentPlayerLabel::setText,
                diceLabel::setText,
                instructionLabel::setText);

        rollButton.setOnAction(event -> controller.rollDice());

        backButton.setOnAction(event ->
        {
            controller.stop();
            showSetupScreen(stage);
        });

        BorderPane layout = createGameLayout(
                playerTypesLabel,
                currentPlayerLabel,
                diceLabel,
                rollButton,
                instructionLabel,
                boardView,
                backButton);

        Scene scene = new Scene(layout, GAME_WIDTH, GAME_HEIGHT);
        stage.setScene(scene);

        controller.start();
    }

    private BorderPane createGameLayout(Label playerTypesLabel, Label currentPlayerLabel,
                                        Label diceLabel, Button rollButton, Label instructionLabel,
                                        BoardView boardView, Button backButton)
    {
        VBox topSection = new VBox(GAME_SPACING, playerTypesLabel, currentPlayerLabel, diceLabel,
                rollButton, instructionLabel);

        topSection.setAlignment(Pos.CENTER);

        HBox bottomSection = new HBox(backButton);
        bottomSection.setAlignment(Pos.CENTER);

        BorderPane layout = new BorderPane();
        layout.setTop(topSection);
        layout.setCenter(boardView);
        layout.setBottom(bottomSection);
        layout.setPadding(new Insets(GAME_PADDING));

        return layout;
    }
}
