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
        this.dieOne = dieOne;
        this.dieTwo = dieTwo;
    }

    public void roll()
    {
        dieOne = random.nextInt(NUMBER_OF_SIDES) + 1;
        dieTwo = random.nextInt(NUMBER_OF_SIDES) + 1;
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
}
