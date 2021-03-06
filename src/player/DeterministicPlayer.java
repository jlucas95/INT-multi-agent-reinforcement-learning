package player;
import jdk.nashorn.internal.runtime.regexp.joni.Warnings;
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
        boolean inPosession = player && state.getPossession();
        Action action;
        // check if in posession of ball
        if (inPosession == player){// if true -> attack
            // move towards enemy goal
            // for every action calculate distance to goal
            action = moveToGoal(state, enemyGoal);
            // add avoid other player
        }
        else{
            // try to intercept
            action = intercept(state);
            if(action == null){
                //fall back to moving to own goal
                action = moveToGoal(state, goal);
            }

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
//               if player first step is up. Then the D player will always lose, it should aiming to
                if (intercepting(move(position, a))) {
                    return a;
                }
            }
        }
        return null;
    }

    private boolean intercepting(Point pos){
        //  Players are in the same row &&    player are between in other player & its goal
        return pos.y == enemyPosition.y && (pos.x < enemyPosition.x == player);
    }

    private Action moveToGoal(State state, Point[] goal){
        // Only consider p[0] p[1], but there has 4 goal position
        // If they are not in the same row, moving forward
        if (position.y != enemyPosition.y){
            if (player == true){
                return Action.WEST;
            }
            else return Action.EAST;
        }else {
            // Else moving up / down
            if (position.y != 0){
                return Action.NORTH;
            }
            return  Action.SOUTH;
        }









        //Original code
//        Point target = goalPoint(goal);
//        Action[] actions = Action.values();
//        double[] distances = new double[actions.length];
//        for(int i = 0; i < actions.length; i++){
//            if(state.isActionPossible(player, actions[i])){
//                Point next = move(position, actions[i]);
//                distances[i] = distance(target, next);
//            }
//            else{
//                distances[i] = 400;
//            }
//        }
//
//        double smallest = Double.MAX_VALUE;
//        Action a = null;
//        for(int i = 0; i < actions.length; i++){
//            if(distances[i] < smallest){
//                a = actions[i];
//                smallest = distances[i];
//            }
//        }
//
//        return a;
    }

    private double distance(Point p1, Point p2){
        double p = Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2);
        return Math.sqrt(p);
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
        int x = first ? State.MIN_X -1 : State.MAX_X + 1;

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
