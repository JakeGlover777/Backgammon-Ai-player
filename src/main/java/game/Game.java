package game;

public class Game
{
    private static final int CHECKERS_PER_PLAYER = 15;

    private final Board board;
    private final Dice dice;
    private Player currentPlayer;

    public Game()
    {
        board = new Board();
        dice = new Dice();
        currentPlayer = Player.WHITE;
    }

    public Board getBoard()
    {
        return board;
    }

    public Dice getDice()
    {
        return dice;
    }

    public Player getCurrentPlayer()
    {
        return currentPlayer;
    }

    public void switchPlayer()
    {
        if (currentPlayer == Player.WHITE)
        {
            currentPlayer = Player.BLACK;
        }
        else
        {
            currentPlayer = Player.WHITE;
        }
    }

    public Player getWinner()
    {
        if (board.getBorneOffCount(Player.WHITE) == CHECKERS_PER_PLAYER)
        {
            return Player.WHITE;
        }

        if (board.getBorneOffCount(Player.BLACK) == CHECKERS_PER_PLAYER)
        {
            return Player.BLACK;
        }

        return Player.NONE;
    }
}
