package main;

import exploration.RandomExploration;
import player.*;
import policy.Policy;

public class Main {

	public static void main(String[] args) {
		//QLearningPlayer qPlayer = new QLearningPlayer(State.FIRST_PLAYER, 0.9, new RandomExploration());
		Player p1 = new DeterministicPlayer(State.FIRST_PLAYER);
		Player p2 = new RandomPlayer(State.SECOND_PLAYER);

		Simulator sim = new Simulator(p1, p2);
		/*
        sim.simulate(1_000_000, 0.1);
		System.out.println("Training QR finished");
        */
		//Policy QR = qPlayer.getPolicy();
		//sim.setP1(new PolicyPlayer(QR));
        System.out.print("running deterministic against random: 1.000.000 steps");
		sim.simulate(1_000_000, 0.1);
		System.out.println("run finished");
		System.out.println("deterministic:" + sim.getGoalsP1());
		System.out.println("random:" + sim.getGoalsP2());
		System.out.println("");
	}
}
