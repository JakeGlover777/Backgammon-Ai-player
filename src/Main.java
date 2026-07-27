public class Main
{
    public static void main(String[] args)
    {
        Game game = new Game();

        System.out.println(game.getCurrentPlayer());

        game.switchPlayer();
        System.out.println(game.getCurrentPlayer());

        game.switchPlayer();
        System.out.println(game.getCurrentPlayer());
    }
}