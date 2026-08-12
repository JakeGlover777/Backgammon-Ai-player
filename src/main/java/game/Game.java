package game;

public class Game
{
    private final Board board;
    private final Dice dice;
    private Player currentPlayer;

    public Game()
    {
        this.board = new Board();
        this.dice = new Dice();
        this.currentPlayer = Player.WHITE;
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
        if (board.getBorneOffCount(Player.WHITE) == 15)
        {
            return Player.WHITE;
        }

        if (board.getBorneOffCount(Player.BLACK) == 15)
        {
            return Player.BLACK;
        }

        return Player.NONE;
    }
}