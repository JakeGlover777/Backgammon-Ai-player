package game;

import java.util.Random;

/**
 * Represents the two dice used during a Backgammon turn.
 *
 * <p>The dice can be rolled randomly or assigned specific values. Each die
 * must have a value between one and six.
 */

public class Dice
{
    private static final int NUMBER_OF_SIDES = 6;

    private final Random random;
    private int dieOne;
    private int dieTwo;

    /**
     * Creates a new pair of dice ready to be rolled.
     */

    public Dice()
    {
        random = new Random();
        dieOne = 0;
        dieTwo = 0;
    }

    /**
     * Creates a pair of dice with the specified values.
     *
     * @param dieOne the value of the first die
     * @param dieTwo the value of the second die
     * @throws IllegalArgumentException if either value is outside the range 1 to 6
     */

    public Dice(int dieOne, int dieTwo)
    {
        random = new Random();

        setDice(dieOne, dieTwo);
    }

    /**
     * Rolls both dice and assigns each a random value between one and six.
     */

    public void roll()
    {
        dieOne = random.nextInt(NUMBER_OF_SIDES) + 1;
        dieTwo = random.nextInt(NUMBER_OF_SIDES) + 1;
    }

    /**
     * Sets both dice to the specified values.
     *
     * @param dieOne the value of the first die
     * @param dieTwo the value of the second die
     * @throws IllegalArgumentException if either value is outside the range 1 to 6
     */

    public void setDice(int dieOne, int dieTwo)
    {
        if (!isValidDieValue(dieOne) || !isValidDieValue(dieTwo))
        {
            throw new IllegalArgumentException("Dice values must be between 1 and 6.");
        }

        this.dieOne = dieOne;
        this.dieTwo = dieTwo;
    }

    /**
     * Returns the value of the first die.
     *
     * @return the first die value
     */

    public int getDieOne()
    {
        return dieOne;
    }

    /**
     * Returns the value of the second die.
     *
     * @return the second die value
     */

    public int getDieTwo()
    {
        return dieTwo;
    }

    /**
     * Determines whether both dice have the same value.
     *
     * @return true if both dice have the same value, otherwise false
     */

    public boolean isDouble()
    {
        return dieOne == dieTwo;
    }

    /**
     * Returns a textual representation of the current dice values.
     *
     * @return the dice values as a string
     */

    @Override
    public String toString()
    {
        return "Die 1: " + dieOne + " | Die 2: " + dieTwo;
    }

    private boolean isValidDieValue(int value)
    {
        return value >= 1 && value <= NUMBER_OF_SIDES;
    }
}
