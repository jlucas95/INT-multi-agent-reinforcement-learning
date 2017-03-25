package player;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import exploration.ExplorationStrategy;
import main.Action;
import main.State;
import main.Triple;
import main.Tuple;
import policy.Policy;
import policy.ProbabilisticPolicy;
import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.constraints.LinearEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LinearProgram;

public class MinimaxQPlayer implements Player {
	private boolean player;
	private State s;
	private Action a;

	private Map<Triple<State, Action, Action>, Double> qValues;
	private Map<State, double[]> pi;
	private Map<State, Double> V;
	private ExplorationStrategy strat;

	double alpha = 1.0;
	double explor = 0.2;
	double gamma;
	double decay;

	public MinimaxQPlayer(boolean player, double discountFactor, double decay, ExplorationStrategy es) {
		initialize();
		gamma = discountFactor;
		this.decay = decay;
		this.player = player;
		strat = es;
	}

	private void initialize() {
		double[] pi_val = new double[Action.values().length];
		for(int i = 0; i < Action.values().length; i++){
			pi_val[i] = 1.0/Action.values().length;
		}

//		For all s in S, a in A, and o in O,
//				Let Q[s,a,o] := 1
//		For all s in S,
//				Let V[s] := 1
//		For all s in S, a in A,
//		Let pi[s,a] := 1/|A|
//				Let alpha := 1.0

		this.qValues = new HashMap<Triple<State, Action, Action>, Double>();
		this.V = new HashMap<State, Double>();
		this.pi = new HashMap<State, double[]>();
		for (int x1 = State.MIN_X - 1; x1 <= State.MAX_X + 1; x1++) {
			for (int y1 = State.MIN_Y - 1; y1 <= State.MAX_Y + 1; y1++) {
				for (int x2 = State.MIN_X - 1; x2 <= State.MAX_X + 1; x2++) {
					for (int y2 = State.MIN_Y - 1; y2 <= State.MAX_Y + 1; y2++) {
						Point p1 = new Point(x1, y1);
						Point p2 = new Point(x2, y2);
						State s1 = new State(p1, p2, true);
						State s2 = new State(p1, p2, false);
						this.V.put(s1, 1.0);
						this.V.put(s2, 1.0);
						for (Action a : Action.values())
						{
							this.pi.put(s1, pi_val);
							this.pi.put(s2, pi_val);

							for(Action o : Action.values()){
								this.qValues.put(new Triple<>(s1, a, o), 0.0);
								this.qValues.put(new Triple<>(s2, a, o), 0.0);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public Action chooseAction(State state) {
		s = state;
		if(Math.random() < explor){
			a =  strat.selectAction(state, player);
		}
		else{
			double[] probabiilities = pi.get(state);
			Action[] actions = Action.values();
			double sum = 0.0;
			double r = Math.random();

			for (int i = 0; i < probabiilities.length; i++) {
				sum = sum + probabiilities[i];
				if (r <= sum) {
					a = actions[i];
					break;
				}
			}
		}
		// With probability explor, return an action uniformly at random.
		// Otherwise, if current state is s,
		// Return action a with probability pi[s,a]
		return a;
	}

	@Override
	public void receiveReward(double reward, State newState, Action opponentAction) {
//		After receiving reward rew for moving from state s to s’
//		via action a and opponent’s action o,
//				Let Q[s,a,o] := (1-alpha) * Q[s,a,o] + alpha * (rew + gamma * V[s’])
		Triple sao = new Triple<>(newState, a, opponentAction);
		double newq = (1 - alpha) * qValues.get(sao) + alpha * (reward + gamma * V.get(newState));
		qValues.put(sao, newq);
		
//		Use linear programming to find pi[s,.] such that:
//		pi[s,.] := argmaxfpi’[s,.], minfo’, sumfa’, pi[s,a’] * Q[s,a’,o’]ggg
		linearProgramming();

//		Let V[s] := minfo’, sumfa’, pi[s,a’] * Q[s,a’,o’]gg
		V.put(s, getNewV());

//		Let alpha := alpha * decay
		alpha *= decay;
	}

	private double getNewV(){
		// min{o’, sum{a’, pi[s,a’] * Q[s,a’,o’] }}
		// for every own and opponent action in state s get the sum of the pi and q value multiplied
		// then get the lowest value
		Action[] actions = Action.values();
		double[] doubles = new double[actions.length];
		for(int i = 0; i < actions.length; i++){
			double val = 0.0;
			double[] cur_pi = pi.get(s);
			for(int j = 0; j < actions.length; j++){
				val += cur_pi[j] * qValues.get(new Triple<>(s, actions[j], actions[i]));
			}
			doubles[i] = val;
		}
		// get the smallest
		double smallest = -1;
		for(double v: doubles){
			if(v < smallest){
				smallest = v;
			}
		}
		return smallest;
	}

	public void linearProgramming() {
		Action[] allActions = Action.values();

		// maximize slack variable (index 0), which represents the value of the
		// inner minimization
		double[] maxObjective = new double[allActions.length + 1];
		maxObjective[0] = 1.0;
		LinearProgram lp = new LinearProgram(maxObjective);

		// all probabilities must add up to 1
		double[] sum1 = new double[allActions.length + 1];
		for (int i = 1; i < sum1.length; i++) {
			sum1[i] = 1.0;
		}
		lp.addConstraint(new LinearEqualsConstraint(sum1, 1.0, "a"));

		for (int i = 0; i < allActions.length; i++) {
			// all probabilities must be positive
			double[] arr1 = new double[maxObjective.length];
			arr1[i + 1] = 1.0;
			lp.addConstraint(new LinearBiggerThanEqualsConstraint(arr1, 0, "b" + (i + 1)));

			// v <= sum( p(s, action_i) * Q(s,action_i, action_opponent) )
			// v - sum( p(s, action_i) * Q(s,action_i, action_opponent) ) <= 0
			double[] arr2 = new double[maxObjective.length];
			arr2[0] = 1.0;
			for (int j = 0; j < allActions.length; j++) {
				arr2[i + 1] = -this.qValues
						.get(new Triple<>(this.s, allActions[i], allActions[j]));
			}
			lp.addConstraint(new LinearSmallerThanEqualsConstraint(arr2, 0, "c" + (i + 1)));
		}
		lp.setMinProblem(false);

		LinearProgramSolver solver = SolverFactory.newDefault();

		double[] sol = solver.solve(lp);
		double[] newPi = new double[allActions.length];
		System.arraycopy(sol, 1, newPi, 0, newPi.length);
		this.pi.put(this.s, newPi);
	}

	public Policy getPolicy() {
		return new ProbabilisticPolicy(this.pi);
	}

}
