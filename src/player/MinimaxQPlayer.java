package player;

import java.util.Map;
import java.util.Random;

import exploration.ExplorationStrategy;
import main.Action;
import main.State;
import main.Triple;
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
	double gamma;
	double decay;

	public MinimaxQPlayer(boolean player, double discountFactor, double decay, ExplorationStrategy es) {
		gamma = discountFactor;
		decay = decay
//		For all s in S, a in A, and o in O,
//				Let Q[s,a,o] := 1
//		For all s in S,
//				Let V[s] := 1
//		For all s in S, a in A,
//		Let pi[s,a] := 1/|A|
//				Let alpha := 1.0

		this.player = player;
		strat = es;
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
		Triple sao = new Triple<State, Action, Action>(newState, a, opponentAction);
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
//		min{o’, sum{a’, pi[s,a’] * Q[s,a’,o’] }}
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
						.get(new Triple<State, Action, Action>(this.s, allActions[i], allActions[j]));
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
