package player;

import main.Action;
import main.State;

/**
 * Created by Jan on 23-3-2017.
 */
public class ProbabilisticPlayer {


    boolean player;
    public ProbabilisticPlayer(boolean first){
        this.player = first ? State.FIRST_PLAYER : State.SECOND_PLAYER;
    }

    public Action chooseAction(State state){
        boolean inPosession = state.getPossession();
        return null;

    }

    public void receiveReward(double reward, State state, Action action){
        //ignore reward
    }
}
