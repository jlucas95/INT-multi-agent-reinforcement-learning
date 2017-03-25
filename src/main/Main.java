package main;

import com.sun.xml.internal.ws.encoding.MimeMultipartParser;
import exploration.RandomExploration;
import player.*;
import policy.Policy;

import java.io.*;

public class Main {

	public static void main(String[] args) {

		//training();

		Policy MM = loadPolicy("MM");
		Policy QR = loadPolicy("QR");
		Policy QQ = loadPolicy("QQ");

		Player MMplayer = new PolicyPlayer(MM);
		Player MMplayer2 = new PolicyPlayer(MM);
		PlayMatch(MMplayer, MMplayer2);

//		Player QRplayer = new PolicyPlayer(QR);
//		Player QRplayer2 = new PolicyPlayer(QR);
//		PlayMatch(QRplayer, QRplayer2);

//		Player QQplayer = new PolicyPlayer(QQ);
//		PlayMatch(QQplayer, QQplayer);



//		//QLearningPlayer p1 = new QLearningPlayer(State.FIRST_PLAYER, 0.9, new RandomExploration());
//		Player p1 = new DeterministicPlayer(State.FIRST_PLAYER);
//		MinimaxQPlayer p2 = new MinimaxQPlayer(State.SECOND_PLAYER,0.9, 0.9999954, new RandomExploration());
//
//		//Player p1 = new ProbabilisticPlayer(State.FIRST_PLAYER);
//		//MinimaxQPlayer p2 = new MinimaxQPlayer(State.SECOND_PLAYER, 0.8, 0.9, new RandomExploration());
//		//Player p2 = new RandomPlayer(State.SECOND_PLAYER);
//
//		Simulator sim = new Simulator(p1, p2);
//
//        sim.simulate(1_000_000, 0.1);
//		System.out.println("Training QR finished");
//		//Policy QR1 = p1.getPolicy();
//		Policy QR2 = p2.getPolicy();
//
//		//sim.setP1(new PolicyPlayer(QR1));
//		sim.setP2(new PolicyPlayer(QR2));
//
//        System.out.print("running qplayer against probabilistic: 1.000.000 steps");
//		sim.simulate(1_000_000, 0.1);
//		System.out.println("run finished");
//		System.out.println("deterministic:" + sim.getGoalsP1());
//		System.out.println("minimaxQ:" + sim.getGoalsP2());
//		System.out.println("");
	}

	private static void PlayMatch(Player p1, Player p2){
		Simulator sim = new Simulator(p1, p2);
        sim.simulate(1_000_000, 0.1);
		System.out.println("run finished");
		System.out.println("p1:" + sim.getGoalsP1());
		System.out.println("p2:" + sim.getGoalsP2());
		System.out.println("");

	}

	private static Policy loadPolicy(String fileName){
		try{
			FileInputStream f = new FileInputStream(fileName);
			ObjectInputStream s = new ObjectInputStream(f);
			try{
				return (Policy) s.readObject();
			}
			catch (ClassNotFoundException e){
				System.out.println("error reading file");
				e.printStackTrace();

			}
			catch (ClassCastException e){
				System.out.println("error casting object");
				e.printStackTrace();
			}
			s.close();
			f.close();
		}
		catch( IOException e){
			System.out.println("error accessing file");
			e.printStackTrace();
		}
		return null;
	}

	private static void training(){
//		// QD
//		QLearningPlayer QDp1 = new QLearningPlayer(State.FIRST_PLAYER,0.9, new RandomExploration());
//		Player QDp2 = new DeterministicPlayer(State.SECOND_PLAYER);
//		Policy QD = train(QDp1, QDp2);
//		System.out.println("saving QD");
//		savePolicy(QD, "QD");
//		// QP
//		QLearningPlayer QPp1 = new QLearningPlayer(State.FIRST_PLAYER,0.9, new RandomExploration());
//		Player QPp2 = new DeterministicPlayer(State.SECOND_PLAYER);
//		Policy QP = train(QPp1, QPp2);
//		System.out.println("saving QD");
//		savePolicy(QP, "QP");
//		// QR
//		QLearningPlayer QRp1 = new QLearningPlayer(State.FIRST_PLAYER,0.9, new RandomExploration());
//		Player QRp2 = new RandomPlayer(State.SECOND_PLAYER);
//		Policy QR = train(QRp1, QRp2);
//		System.out.println("saving QR");
//		savePolicy(QR, "QR");
		// QQ
//		QLearningPlayer QQp1 = new QLearningPlayer(State.FIRST_PLAYER,0.9, new RandomExploration());
//		QLearningPlayer QQp2 = new QLearningPlayer(State.FIRST_PLAYER,0.9, new RandomExploration());
//		Policy QQ = train(QQp1, QQp2);
//		System.out.println("saving QQ");
//		savePolicy(QQ, "QQ");
//		// MR
//		MinimaxQPlayer MRp1 = new MinimaxQPlayer(State.FIRST_PLAYER,0.9, 0.9999954, new RandomExploration());
//		Player MRp2 = new RandomPlayer(State.SECOND_PLAYER);
//		Policy MR = train(MRp1, MRp2);
//		System.out.println("saving MR");
//		savePolicy(MR, "MR");
//		// MM
//		MinimaxQPlayer MMp1 = new MinimaxQPlayer(State.FIRST_PLAYER,0.9, 0.9999954, new RandomExploration());
//		MinimaxQPlayer MMp2 = new MinimaxQPlayer(State.SECOND_PLAYER,0.9, 0.9999954, new RandomExploration());
//		Policy MM = train(MMp1, MMp2);
//		System.out.println("saving MM");
//		savePolicy(MM, "MM");
	}

	// trains 2 players and returns the policy of player 1
	private static Policy train(MinimaxQPlayer p1, Player p2){
		_train(p1, p2);
		return p1.getPolicy();
	}

	private static Policy train(QLearningPlayer p1, Player p2){
		_train(p1, p2);
		return p1.getPolicy();
	}

	private static void _train(Player p1, Player p2){
		Simulator sim = new Simulator(p1, p2);
		sim.simulate(1_000_000, 0.1);
		System.out.println("Training finished");
	}

	private static void savePolicy(Policy p, String fileName){
		FileOutputStream s;
		try{
			s = new FileOutputStream(fileName);
			ObjectOutputStream os = new ObjectOutputStream(s);
			os.writeObject(p);
			os.close();
			s.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}

	}
}
