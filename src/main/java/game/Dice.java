package game;

import java.util.Random;

public class Dice
{
    private static final int NUMBER_OF_SIDES = 6;

    private final Random random;
    private int dieOne;
    private int dieTwo;

    public Dice()
    {
        random = new Random();
        dieOne = 0;
        dieTwo = 0;
    }

    public Dice(int dieOne, int dieTwo)
    {
        random = new Random();

        setDice(dieOne, dieTwo);
    }

    public void roll()
    {
        dieOne = random.nextInt(NUMBER_OF_SIDES) + 1;
        dieTwo = random.nextInt(NUMBER_OF_SIDES) + 1;
    }

    public void setDice(int dieOne, int dieTwo)
    {
        if (!isValidDieValue(dieOne) || !isValidDieValue(dieTwo))
        {
            throw new IllegalArgumentException("Dice values must be between 1 and 6.");
        }

        this.dieOne = dieOne;
        this.dieTwo = dieTwo;
    }

    public int getDieOne()
    {
        return dieOne;
    }

    public int getDieTwo()
    {
        return dieTwo;
    }

    public boolean isDouble()
    {
        return dieOne == dieTwo;
    }

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