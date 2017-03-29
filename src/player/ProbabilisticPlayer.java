package player;

import main.Action;
import main.State;

import java.awt.*;

/**
 * Created by Jan on 23-3-2017.
 */
public class ProbabilisticPlayer implements Player {

    boolean player;

    public ProbabilisticPlayer(boolean first){
        this.player = first ? State.FIRST_PLAYER : State.SECOND_PLAYER;
    }

    public Action chooseAction(State state) {
        boolean inPosession = state.getPossession();
        Action[] possibleActions = new Action[3];
        possibleActions[2] = Action.STAND;
        Point secondPlayer = state.getP2();
        Point firstPlayer = state.getP1();
        Point distance = new Point(firstPlayer.x - secondPlayer.x, firstPlayer.y - secondPlayer.y);

        // whether player got the ball
        if (inPosession == player) {
            // If its the first player(A)
            if (player) {
                // 3 different kinds of situation:  1. Same row 2. left-up corner(stay put & moving forward & down )
                //  In the same row ( 33.3% up & 33.3% down &  33.3% put )
                if (distance.x == 0) {
                    possibleActions[0] = Action.NORTH;
                    possibleActions[1] = Action.SOUTH;
                    return  this.chooseMoving(possibleActions);
                }
                // left-down ()
                else if (distance.x > 0 && distance.y < 0) {
                        possibleActions[0] = Action.WEST;
                        possibleActions[1] = Action.NORTH;
                        return this.chooseMoving(possibleActions);
                }
                // left-up
                else if (distance.x > 0 && distance.y > 0) {
                    possibleActions[0] = Action.WEST;
                    possibleActions[1] = Action.SOUTH;
                    return  this.chooseMoving(possibleActions);
                    }
                else{
                        return Action.WEST;
                    }
                }
                // If its the second player(B)
                // 3 different kinds of situation:  1. Same row(up & down & put)  2. right-up corner(stay put & moving forward & down ) 3. right-down corner
            else {
                    // Same row
                    if (distance.y == 0) {
                        possibleActions[0] = Action.NORTH;
                        possibleActions[1] = Action.SOUTH;
                        return this.chooseMoving(possibleActions);
                    }
                    // right-up corner   (down , right , put)
                    else if (distance.x > 0 && distance.y < 0) {
                        possibleActions[0] = Action.SOUTH;
                        possibleActions[1] = Action.EAST;
                        return this.chooseMoving(possibleActions);
                    }
                    // right-down corner (up , right ,put )
                    else if (distance.x > 0 && distance.y > 0) {
                       possibleActions[0] = Action.NORTH;
                       possibleActions[1] = Action.EAST;
                       return this.chooseMoving(possibleActions);

                    } else {
                        return Action.EAST;
                    }
                }
            }
            // when the player don't have the ball
         else {
            // if its the first player
            if (player){
                // 3 different kinds of situation:  1. Same row 2. left-up  3. left-down
                if (distance.x == 0) {
                    possibleActions[0] = Action.WEST;
                    possibleActions[1] = Action.STAND;
                    return this.chooseMoving(possibleActions);
                    // Moving forward or STAND
                }
                // left-down ()
                else if (distance.x > 0 && distance.y < 0) {
                    possibleActions[0] = possibleActions[1] = Action.SOUTH;
                    return  this.chooseMoving(possibleActions);
                    // Moving forward

                }
                // left-up
                else if (distance.x > 0 && distance.y > 0) {
                    possibleActions[0] = possibleActions[1] = Action.NORTH;
                    return  this.chooseMoving(possibleActions);
                }
                else{
                    return Action.EAST;
                }
            }
            // Its second player
            else {
                if (distance.y == 0) {
                    possibleActions[0] = Action.EAST;
                    possibleActions[1] = Action.STAND;
                    return this.chooseMoving(possibleActions);
                    // 向前
                }
                else if (distance.x > 0 && distance.y < 0) {
                    possibleActions[0] = possibleActions[1] = Action.NORTH;
                    return this.chooseMoving(possibleActions);
                    // Moving xx

                }
                // right-down corner (up , right ,put )
                else if (distance.x > 0 && distance.y > 0) {
                    possibleActions[0] = possibleActions[1] = Action.SOUTH;
                    return this.chooseMoving(possibleActions);
                } else {
                    return Action.WEST;
                }
            }

        }
    }
    private Action chooseMoving(Action[] actions){
        int direction = (int) (Math.random() / 0.3333);
        switch (direction) {
            case 0:
                return actions[0];
            case 1:
                return  actions[1];
            case 2:
                return actions[2];
            default:
                return  actions[2];
        }
    }
    public void receiveReward(double reward, State state, Action action){
        //ignore reward
    }
}
