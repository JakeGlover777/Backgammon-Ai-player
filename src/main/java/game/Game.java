package game;

import java.util.Random;

public class Game
{
    private static final int CHECKERS_PER_PLAYER = 15;
    private static final int NUMBER_OF_DIE_SIDES = 6;

    private final Board board;
    private final Dice dice;
    private final Random random;

    private Player currentPlayer;
    private boolean openingRoll;

    public Game()
    {
        board = new Board();
        dice = new Dice();
        random = new Random();

        determineStartingPlayer();
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

    public boolean isOpeningRoll()
    {
        return openingRoll;
    }

    public void completeOpeningRoll()
    {
        openingRoll = false;
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

    private void determineStartingPlayer()
    {
        int whiteRoll;
        int blackRoll;

        do
        {
            whiteRoll = random.nextInt(NUMBER_OF_DIE_SIDES) + 1;
            blackRoll = random.nextInt(NUMBER_OF_DIE_SIDES) + 1;
        }
        while (whiteRoll == blackRoll);

        if (whiteRoll > blackRoll)
        {
            currentPlayer = Player.WHITE;
        }
        else
        {
            currentPlayer = Player.BLACK;
        }

        dice.setDice(whiteRoll, blackRoll);
        openingRoll = true;
    }
}
