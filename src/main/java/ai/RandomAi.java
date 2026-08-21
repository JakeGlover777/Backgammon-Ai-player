package ai;

import game.Board;
import game.Dice;
import game.MoveGenerator;
import game.MoveSequence;
import game.Player;

import java.util.List;
import java.util.Random;

public class RandomAi implements AiPlayer
{
    private final MoveGenerator moveGenerator;
    private final Random random;

    public RandomAi()
    {
        moveGenerator = new MoveGenerator();
        random = new Random();
    }

    @Override
    public MoveSequence chooseMove(Board board, Player player, Dice dice)
    {
        List<MoveSequence> legalSequences =
                moveGenerator.generateMoveSequences(board, player, dice);

        if (legalSequences.isEmpty())
        {
            return new MoveSequence();
        }

        int randomIndex = random.nextInt(legalSequences.size());

        return legalSequences.get(randomIndex);
    }
}
