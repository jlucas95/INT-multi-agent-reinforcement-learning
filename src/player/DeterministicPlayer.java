package player;
import main.*;


/**
 * Created by Jan on 23-3-2017.
 */
public class DeterministicPlayer implements Player {
    boolean player;
    public DeterministicPlayer(boolean first){
        this.player = first ? State.FIRST_PLAYER : State.SECOND_PLAYER;
    }

    public Action chooseAction(State state){
        boolean inPosession = state.getPossession();
        // check if in posession of ball

        // if true attack
            // move towards enemy goal
        // else defend
            // try intercept
            // or move towards own goal

    }

    public void receiveReward(double reward, State state, Action action){
        //ignore reward
    }
}
