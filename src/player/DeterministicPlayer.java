package player;
import main.*;

import java.awt.Point;
import java.util.ArrayList;


/**
 * Created by Jan on 23-3-2017.
 */

public class DeterministicPlayer implements Player {
    boolean player;
    Point[] goal;
    Point[] enemyGoal;
    Point position;
    Point enemyPosition;

    public DeterministicPlayer(boolean first){
        this.player = first ? State.FIRST_PLAYER : State.SECOND_PLAYER;
        goal = getGoal(first);
        enemyGoal = getGoal(!first);
    }

    public Action chooseAction(State state){
        position = player ? state.getP1() : state.getP2();
        enemyPosition = !player ? state.getP1() : state.getP2();
        boolean inPosession = state.getPossession();
        Action action;
        // check if in posession of ball
        if (inPosession){// if true -> attack
            // move towards enemy goal
            // for every action calculate distance to goal
            action = moveToGoal(state, enemyGoal);
            // add avoid other player
        }
        else{
            // try to intercept
            //action = intercept(state);
            //if(action == null){
                // fall back to moving to own goal
                action = moveToGoal(state, goal);
            //}

        }
        return action;

    }

    public void receiveReward(double reward, State state, Action action){
        //ignore reward
    }

    private Action intercept(State state){
        // if between own goal and enemy
        if(intercepting(position)){
            // stay
            return Action.STAND;
        }
        // else
        else {
            for (Action a : Action.values()) {
                if (intercepting(move(position, a))) {
                    return a;
                }
            }
        }
        return null;
    }

    private boolean intercepting(Point pos){
        return pos.y == enemyPosition.y && (pos.x < enemyPosition.x == player);
    }

    private Action moveToGoal(State state, Point[] goal){
        Point target = goalPoint(goal);
        Action[] actions = Action.values();
        double[] distances = new double[actions.length];
        for(int i = 0; i < actions.length; i++){
            if(state.isActionPossible(player, actions[i])){
                Point next = move(position, actions[i]);
                distances[i] = target.distance(next);
            }
            else{
                distances[i] = 400;
            }
        }

        double smallest = Double.MAX_VALUE;
        Action a = null;
        for(int i = 0; i < actions.length; i++){
            if(distances[i] < smallest){
                a = actions[i];
                smallest = distances[i];
            }
        }

        return a;
    }

    private Point goalPoint(Point[] p){
        if(p[0].distance(position) < p[1].distance(position)){
            return p[0];
        }
        else{
            return p[1];
        }
    }

    private Point[] getGoal(boolean first){
        int x = first ? State.MIN_X +1 : State.MAX_X - 1;

        return new Point[] {new Point(x, 1), new Point(x, 2)};
    }

    private Point move(Point pos, Action action){
        Point newPos = new Point(pos);
        switch (action) {
            case EAST:
                newPos.x += 1;
                break;
            case NORTH:
                newPos.y -= 1;
                break;
            case SOUTH:
                newPos.y += 1;
                break;
            case WEST:
                newPos.x -= 1;
                break;
            case STAND:
                break;
            default:
                break;
        }
        return newPos;

    }

}
