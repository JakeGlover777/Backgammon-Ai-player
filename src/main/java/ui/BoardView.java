package ui;

import game.Board;
import game.Player;
import game.Point;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class BoardView extends Pane
{
    public static final int WHITE_BAR = -1;
    public static final int BLACK_BAR = -2;
    public static final int WHITE_BEAR_OFF = -3;
    public static final int BLACK_BEAR_OFF = -4;

    private static final int BOARD_SIZE = 24;
    private static final int HALF_BOARD_SIZE = 12;
    private static final int QUADRANT_SIZE = 6;
    private static final int TOP_RIGHT_END = 18;
    private static final int LAST_POINT_INDEX = 23;

    private static final double BOARD_WIDTH = 800;
    private static final double BOARD_HEIGHT = 500;
    private static final double BAR_WIDTH = 50;
    private static final double POINT_WIDTH = 55;
    private static final double POINT_HEIGHT = 200;

    private static final double CHECKER_RADIUS = 20;
    private static final double CHECKER_SPACING = 42;
    private static final double BAR_CHECKER_OFFSET = 30;

    private static final double BEAR_OFF_WIDTH = 28;
    private static final double BEAR_OFF_HEIGHT = 180;
    private static final double BEAR_OFF_MARGIN = 2;

    private static final double BOARD_EDGE_MARGIN = 20;
    private static final double CENTRE_POINT_MARGIN = 10;
    private static final double POINT_HIGHLIGHT_WIDTH = 4;
    private static final double CHECKER_STROKE_WIDTH = 2;

    private final Board board;
    private final Set<Integer> highlightedPoints = new HashSet<>();

    private Consumer<Integer> pointClickHandler;

    public BoardView(Board board)
    {
        this.board = board;

        setPrefSize(BOARD_WIDTH, BOARD_HEIGHT);
        drawBoard();
    }

    public void refresh()
    {
        drawBoard();
    }

    public void setOnPointClicked(Consumer<Integer> handler)
    {
        pointClickHandler = handler;
    }

    public void highlightPoints(Set<Integer> points)
    {
        highlightedPoints.clear();
        highlightedPoints.addAll(points);
        drawBoard();
    }

    public void clearHighlights()
    {
        highlightedPoints.clear();
        drawBoard();
    }

    private void drawBoard()
    {
        getChildren().clear();

        Rectangle background = new Rectangle(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        background.setFill(Color.BURLYWOOD);

        getChildren().add(background);

        drawBar();
        drawPoints();
        drawBearOffAreas();
        drawCheckers();
        drawBarCheckers();
    }

    private void drawBar()
    {
        double barX = (BOARD_WIDTH / 2) - (BAR_WIDTH / 2);

        Rectangle bar = new Rectangle(barX, 0, BAR_WIDTH, BOARD_HEIGHT);
        bar.setFill(Color.SADDLEBROWN);

        getChildren().add(bar);
    }

    private void drawBearOffAreas()
    {
        Rectangle blackArea = new Rectangle(
                BEAR_OFF_MARGIN,
                (BOARD_HEIGHT - BEAR_OFF_HEIGHT) / 2,
                BEAR_OFF_WIDTH,
                BEAR_OFF_HEIGHT);

        blackArea.setFill(
                highlightedPoints.contains(BLACK_BEAR_OFF)
                        ? Color.GOLD
                        : Color.LIGHTGRAY);

        blackArea.setStroke(Color.BLACK);
        blackArea.setOnMouseClicked(event -> handlePointClick(BLACK_BEAR_OFF));

        Rectangle whiteArea = new Rectangle(
                BOARD_WIDTH - BEAR_OFF_WIDTH - BEAR_OFF_MARGIN,
                (BOARD_HEIGHT - BEAR_OFF_HEIGHT) / 2,
                BEAR_OFF_WIDTH,
                BEAR_OFF_HEIGHT);

        whiteArea.setFill(
                highlightedPoints.contains(WHITE_BEAR_OFF)
                        ? Color.GOLD
                        : Color.LIGHTGRAY);

        whiteArea.setStroke(Color.BLACK);
        whiteArea.setOnMouseClicked(event -> handlePointClick(WHITE_BEAR_OFF));

        getChildren().addAll(blackArea, whiteArea);
    }

    private void drawPoints()
    {
        for (int pointIndex = 0; pointIndex < BOARD_SIZE; pointIndex++)
        {
            double x = getPointX(pointIndex);
            Polygon triangle;

            if (isTopPoint(pointIndex))
            {
                triangle = createTopPoint(x, pointIndex);
            }
            else
            {
                triangle = createBottomPoint(x, pointIndex);
            }

            final int clickedPoint = pointIndex;
            triangle.setOnMouseClicked(event -> handlePointClick(clickedPoint));

            getChildren().add(triangle);
        }
    }

    private Polygon createTopPoint(double x, int index)
    {
        Polygon triangle = new Polygon();

        triangle.getPoints().addAll(
                x, 0.0,
                x + POINT_WIDTH, 0.0,
                x + (POINT_WIDTH / 2), POINT_HEIGHT);

        stylePoint(triangle, index);

        return triangle;
    }

    private Polygon createBottomPoint(double x, int index)
    {
        Polygon triangle = new Polygon();

        triangle.getPoints().addAll(
                x, BOARD_HEIGHT,
                x + POINT_WIDTH, BOARD_HEIGHT,
                x + (POINT_WIDTH / 2), BOARD_HEIGHT - POINT_HEIGHT);

        stylePoint(triangle, index);

        return triangle;
    }

    private void stylePoint(Polygon triangle, int index)
    {
        if (highlightedPoints.contains(index))
        {
            triangle.setFill(Color.GOLD);
            triangle.setStroke(Color.YELLOW);
            triangle.setStrokeWidth(POINT_HIGHLIGHT_WIDTH);
        }
        else
        {
            triangle.setFill(getPointColor(index));
        }
    }

    private void drawCheckers()
    {
        for (int pointIndex = 0; pointIndex < BOARD_SIZE; pointIndex++)
        {
            Point point = board.getPoint(pointIndex);

            if (point.isEmpty())
            {
                continue;
            }

            for (int checkerIndex = 0; checkerIndex < point.getCheckerCount(); checkerIndex++)
            {
                drawChecker(pointIndex, checkerIndex, point.getOwner());
            }
        }
    }

    private void drawChecker(int pointIndex, int checkerIndex, Player player)
    {
        double x = getPointX(pointIndex) + (POINT_WIDTH / 2);
        double y;

        if (isTopPoint(pointIndex))
        {
            y = CHECKER_RADIUS + (checkerIndex * CHECKER_SPACING);
        }
        else
        {
            y = BOARD_HEIGHT - CHECKER_RADIUS - (checkerIndex * CHECKER_SPACING);
        }

        Circle checker = createChecker(x, y, player);

        final int clickedPoint = pointIndex;
        checker.setOnMouseClicked(event -> handlePointClick(clickedPoint));

        getChildren().add(checker);
    }

    private void drawBarCheckers()
    {
        drawPlayerBarCheckers(Player.WHITE, WHITE_BAR);
        drawPlayerBarCheckers(Player.BLACK, BLACK_BAR);
    }

    private void drawPlayerBarCheckers(Player player, int barIdentifier)
    {
        int count = board.getBarCount(player);

        if (count == 0)
        {
            return;
        }

        double x = BOARD_WIDTH / 2;

        for (int i = 0; i < count; i++)
        {
            double y;

            if (player == Player.WHITE)
            {
                y = (BOARD_HEIGHT / 2) + BAR_CHECKER_OFFSET + (i * CHECKER_SPACING);
            }
            else
            {
                y = (BOARD_HEIGHT / 2) - BAR_CHECKER_OFFSET - (i * CHECKER_SPACING);
            }

            Circle checker = createChecker(x, y, player);
            checker.setOnMouseClicked(event -> handlePointClick(barIdentifier));

            getChildren().add(checker);
        }
    }

    private Circle createChecker(double x, double y, Player player)
    {
        Circle checker = new Circle(x, y, CHECKER_RADIUS);

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

        checker.setStrokeWidth(CHECKER_STROKE_WIDTH);

        return checker;
    }

    private void handlePointClick(int pointIndex)
    {
        if (pointClickHandler != null)
        {
            pointClickHandler.accept(pointIndex);
        }
    }

    private boolean isTopPoint(int pointIndex)
    {
        return pointIndex >= HALF_BOARD_SIZE;
    }

    private double getPointX(int pointIndex)
    {
        int position;

        if (pointIndex < QUADRANT_SIZE)
        {
            position = pointIndex;
            return BOARD_EDGE_MARGIN + (position * POINT_WIDTH);
        }

        if (pointIndex < HALF_BOARD_SIZE)
        {
            position = pointIndex - QUADRANT_SIZE;

            return (BOARD_WIDTH / 2)
                    + (BAR_WIDTH / 2)
                    + CENTRE_POINT_MARGIN
                    + (position * POINT_WIDTH);
        }

        if (pointIndex < TOP_RIGHT_END)
        {
            position = (TOP_RIGHT_END - 1) - pointIndex;

            return (BOARD_WIDTH / 2)
                    + (BAR_WIDTH / 2)
                    + CENTRE_POINT_MARGIN
                    + (position * POINT_WIDTH);
        }

        position = LAST_POINT_INDEX - pointIndex;

        return BOARD_EDGE_MARGIN + (position * POINT_WIDTH);
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
