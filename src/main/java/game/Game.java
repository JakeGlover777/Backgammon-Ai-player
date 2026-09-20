package game;

import java.util.Random;

/**
 * Represents the state of a Backgammon game.
 *
 * <p>The game maintains the board, dice, current player, and opening-roll
 * state. It also determines the starting player and identifies when a player
 * has won the game.
 */

public class Game
{
    private static final int CHECKERS_PER_PLAYER = 15;
    private static final int NUMBER_OF_DIE_SIDES = 6;

    private final Board board;
    private final Dice dice;
    private final Random random;

    private Player currentPlayer;
    private boolean openingRoll;

    /**
     * Creates a new Backgammon game and determines the starting player using the
     * opening roll.
     */

    public Game()
    {
        board = new Board();
        dice = new Dice();
        random = new Random();

        determineStartingPlayer();
    }

    /**
     * Returns the board used by this game.
     *
     * @return the current board
     */

    public Board getBoard()
    {
        return board;
    }

    /**
     * Returns the dice used by this game.
     *
     * @return the game dice
     */

    public Dice getDice()
    {
        return dice;
    }

    /**
     * Returns the player whose turn it currently is.
     *
     * @return the current player
     */

    public Player getCurrentPlayer()
    {
        return currentPlayer;
    }

    /**
     * Determines whether the game is currently using the opening roll.
     *
     * @return true if the opening roll has not yet been completed, otherwise false
     */

    public boolean isOpeningRoll()
    {
        return openingRoll;
    }

    /**
     * Marks the opening roll as completed.
     */

    public void completeOpeningRoll()
    {
        openingRoll = false;
    }

    /**
     * Switches the current player between WHITE and BLACK.
     */

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

    /**
     * Returns the winner of the game if either player has borne off all of their
     * checkers.
     *
     * @return the winning player, or player none if the game has not
     *         been won
     */

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

    /**
     * Determines the starting player using the standard opening-roll procedure.
     *
     * <p>Each player rolls one die until different values are produced. The
     * player with the higher value starts, and the two opening values are retained
     * as the dice for the first turn.
     */

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
