public class Main
{
    public static void main(String[] args)
    {
        
        Board board = new Board();
        board.printBoard();


        Dice dice = new Dice();

        for (int i = 0; i < 10; i++)
        {
            dice.roll();
            System.out.println(dice);

            if (dice.isDouble())
            {
                System.out.println("Double rolled!");
            }
        }
    }
}