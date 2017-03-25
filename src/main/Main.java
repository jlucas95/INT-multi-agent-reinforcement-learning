package main;

import exploration.RandomExploration;
import player.*;
import policy.Policy;

public class Main {

	public static void main(String[] args) {
		QLearningPlayer p1 = new QLearningPlayer(State.FIRST_PLAYER, 0.9, new RandomExploration());
		MinimaxQPlayer p2 = new MinimaxQPlayer(State.SECOND_PLAYER,0.9, 0.9, new RandomExploration());

		//Player p1 = new DeterministicPlayer(State.FIRST_PLAYER);
		//Player p1 = new ProbabilisticPlayer(State.FIRST_PLAYER);
		//MinimaxQPlayer p2 = new MinimaxQPlayer(State.SECOND_PLAYER, 0.8, 0.9, new RandomExploration());
		//Player p2 = new RandomPlayer(State.SECOND_PLAYER);

		Simulator sim = new Simulator(p1, p2);

        sim.simulate(1_000_000, 0.1);
		System.out.println("Training QR finished");
		Policy QR1 = p1.getPolicy();
		Policy QR2 = p2.getPolicy();
		sim.setP1(new PolicyPlayer(QR1));
		sim.setP2(new PolicyPlayer(QR2));

        System.out.print("running qplayer against probabilistic: 1.000.000 steps");
		sim.simulate(1_000_000, 0.1);
		System.out.println("run finished");
		System.out.println("Qlearner:" + sim.getGoalsP1());
		System.out.println("minimaxQ:" + sim.getGoalsP2());
		System.out.println("");
	}
}
