package ai;

import game.Board;
import game.Dice;
import game.MoveSequence;
import game.Player;

public interface AiPlayer
{
    MoveSequence chooseMove(
            Board board,
            Player player,
            Dice dice
    );
}