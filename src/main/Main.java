package main;

import exploration.RandomExploration;
import player.*;
import policy.Policy;

public class Main {

	public static void main(String[] args) {
		QLearningPlayer p1 = new QLearningPlayer(State.FIRST_PLAYER, 0.9, new RandomExploration());
		//Player p1 = new DeterministicPlayer(State.FIRST_PLAYER);
		Player p2 = new ProbabilisticPlayer(State.SECOND_PLAYER);
		//Player p2 = new RandomPlayer(State.SECOND_PLAYER);

		Simulator sim = new Simulator(p1, p2);

        sim.simulate(1_000_000, 0.1);
		System.out.println("Training QR finished");

		Policy QR = p1.getPolicy();
		sim.setP1(new PolicyPlayer(QR));
        System.out.print("running qplayer against probabilistic: 1.000.000 steps");
		sim.simulate(1_000_000, 0.1);
		System.out.println("run finished");
		System.out.println("qplayer:" + sim.getGoalsP1());
		System.out.println("probabilistic:" + sim.getGoalsP2());
		System.out.println("");
	}
}
