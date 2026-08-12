package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameUi extends Application
{
    @Override
    public void start(Stage stage)
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

        Scene scene = new Scene(layout, 500, 400);

        stage.setTitle("Backgammon AI");
        stage.setScene(scene);
        stage.show();
    }
}