package ui;

import game.Board;
import game.Player;
import game.Point;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class BoardView extends Pane
{
    private static final double BOARD_WIDTH = 800;
    private static final double BOARD_HEIGHT = 500;

    private static final double BAR_WIDTH = 50;
    private static final double POINT_WIDTH = 55;
    private static final double POINT_HEIGHT = 200;

    private static final double CHECKER_RADIUS = 20;
    private static final double CHECKER_SPACING = 42;

    private final Board board;

    public BoardView(Board board)
    {
        this.board = board;

        setPrefSize(BOARD_WIDTH, BOARD_HEIGHT);

        drawBoard();
    }

    private void drawBoard()
    {
        getChildren().clear();

        Rectangle background = new Rectangle(
                0,
                0,
                BOARD_WIDTH,
                BOARD_HEIGHT
        );

        background.setFill(Color.BURLYWOOD);

        getChildren().add(background);

        drawBar();
        drawPoints();
        drawCheckers();
    }

    private void drawBar()
    {
        double barX =
                (BOARD_WIDTH / 2) - (BAR_WIDTH / 2);

        Rectangle bar = new Rectangle(
                barX,
                0,
                BAR_WIDTH,
                BOARD_HEIGHT
        );

        bar.setFill(Color.SADDLEBROWN);

        getChildren().add(bar);
    }

    private void drawPoints()
    {
        for (int pointIndex = 0; pointIndex < 24; pointIndex++)
        {
            double x = getPointX(pointIndex);

            if (isTopPoint(pointIndex))
            {
                drawTopPoint(x, pointIndex);
            }
            else
            {
                drawBottomPoint(x, pointIndex);
            }
        }
    }

    private void drawTopPoint(double x, int index)
    {
        Polygon triangle = new Polygon();

        triangle.getPoints().addAll(
                x, 0.0,
                x + POINT_WIDTH, 0.0,
                x + (POINT_WIDTH / 2), POINT_HEIGHT
        );

        triangle.setFill(getPointColor(index));

        getChildren().add(triangle);
    }

    private void drawBottomPoint(double x, int index)
    {
        Polygon triangle = new Polygon();

        triangle.getPoints().addAll(
                x, BOARD_HEIGHT,
                x + POINT_WIDTH, BOARD_HEIGHT,
                x + (POINT_WIDTH / 2),
                BOARD_HEIGHT - POINT_HEIGHT
        );

        triangle.setFill(getPointColor(index));

        getChildren().add(triangle);
    }

    private void drawCheckers()
    {
        for (int pointIndex = 0; pointIndex < 24; pointIndex++)
        {
            Point point = board.getPoint(pointIndex);

            if (point.isEmpty())
            {
                continue;
            }

            for (int checkerIndex = 0;
                 checkerIndex < point.getCheckerCount();
                 checkerIndex++)
            {
                drawChecker(
                        pointIndex,
                        checkerIndex,
                        point.getOwner()
                );
            }
        }
    }

    private void drawChecker(
            int pointIndex,
            int checkerIndex,
            Player player)
    {
        double x =
                getPointX(pointIndex)
                        + (POINT_WIDTH / 2);

        double y;

        if (isTopPoint(pointIndex))
        {
            y = CHECKER_RADIUS
                    + (checkerIndex * CHECKER_SPACING);
        }
        else
        {
            y = BOARD_HEIGHT
                    - CHECKER_RADIUS
                    - (checkerIndex * CHECKER_SPACING);
        }

        Circle checker = new Circle(
                x,
                y,
                CHECKER_RADIUS
        );

        if (player == Player.WHITE)
        {
            checker.setFill(Color.WHITE);
            checker.setStroke(Color.BLACK);
        }
        else
        {
            checker.setFill(Color.BLACK);
            checker.setStroke(Color.WHITE);
        }

        checker.setStrokeWidth(2);

        getChildren().add(checker);
    }

    private boolean isTopPoint(int pointIndex)
    {
        return pointIndex >= 12;
    }

    private double getPointX(int pointIndex)
    {
        int position;

        if (pointIndex < 6)
        {
            position = pointIndex;

            return 20
                    + (position * POINT_WIDTH);
        }

        if (pointIndex < 12)
        {
            position = pointIndex - 6;

            return (BOARD_WIDTH / 2)
                    + (BAR_WIDTH / 2)
                    + 10
                    + (position * POINT_WIDTH);
        }

        if (pointIndex < 18)
        {
            position = 17 - pointIndex;

            return (BOARD_WIDTH / 2)
                    + (BAR_WIDTH / 2)
                    + 10
                    + (position * POINT_WIDTH);
        }

        position = 23 - pointIndex;

        return 20
                + (position * POINT_WIDTH);
    }

    private Color getPointColor(int index)
    {
        if (index % 2 == 0)
        {
            return Color.DARKRED;
        }

        return Color.BEIGE;
    }
}