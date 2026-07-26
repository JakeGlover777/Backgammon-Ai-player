import java.util.Random;

public class Dice
{
    private int dieOne;
    private int dieTwo;
    private final Random random;

    public Dice()
    {
        this.random = new Random();
        this.dieOne = 0;
        this.dieTwo = 0;
    }

    public void roll()
    {
        dieOne = random.nextInt(6) + 1;
        dieTwo = random.nextInt(6) + 1;
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