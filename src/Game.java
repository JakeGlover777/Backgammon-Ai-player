
public class Game
{
    private final Board board;
    private final Dice dice;
    private Player currentPlayer;

    public Game()
    {
        this.board = new Board();
        this.dice  = new Dice();
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
        if(currentPlayer == Player.WHITE)
        {
            currentPlayer = Player.BLACK;
        }
        else
        {
            currentPlayer = Player.WHITE;
        }
    }

}
