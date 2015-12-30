/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.util.ArrayList;

/**
 *
 * @author Vincent Wells
 */
public class PacmanGame {

	/**
	 * @param args
	 *            The main method, obviously, where I run the program. Note that
	 *            the output will work the best using the Courier font. On my
	 *            PC, the program runtime is about 23s. The move sequence is
	 *            stored in an ArrayList, which is printed at the end (this is
	 *            not counted towards run time).
	 *            Notes: I had to alter my original map plan a decent amount to
	 *            make it winnable, as having long, interrupted corridors led to
	 *            inescapable situations.  After I successfully implemented this
	 *            program for "non-chaser" ghosts, I was still unable to sucess
	 *            -fully complete the map vs chasing ghosts without splitting the
	 *            vertical intersections at 15(ish)th row into forks, as the Pacman
	 *            would be bottlenecked into one half of the map, and unable to
	 *            cross back to the other side without being spotted and chased.
	 * @throws Exception 
	 */

	public static void main(String[] args) throws Exception {
		PacmanGame game = new PacmanGame();
		game.run();
	}

	private void run() throws Exception {
		double start = System.currentTimeMillis();
		ArrayList<Integer> moves = new ArrayList<Integer>();
		Board b = new Board();
		Pacman p = new Pacman(b);
		HydraeOperator h = new HydraeOperator(p, b);
		while (b.moreLevels) {
			b.initBoard();
			if (!b.moreLevels) {
				break;
			}
			p.initPac();
			System.out.println(b.dotCount());
			b.printBoard();
			h.initHydrae();
			int dotTotal = b.dotCount();
			p.setTurnCount(0);
			int m = 0;
			while (true) {
				PacmanOperator pac = new PacmanOperator(b, h, p, m, p.getTurnCount());
				// System.out.println("Enter your moving using w, s, a, or d.
				// Enter q to quit at any time.");
				// char input = reader.next().charAt(0);
				// if (input == ('q')) {
				// System.out.println("You have quit.");
				// b.moreLevels = false;
				// break;
				// }
				h.setSeed(p.getTurnCount());
				if (p.getTurnCount() < 7) {
					if (p.getTurnCount() % 2 == 0) {
						h.spawn();
					}
				}
				m = pac.Predictor();
				if (!p.makeMove(m)) {
					p.setTurnCount(p.getTurnCount() + 1);
					System.err.println("INVALID MOVE " + m);
					continue;
				}
				moves.add(m);
				h.checkCollision();
				h.makeMove();
				h.chucker();
				h.checkCollision();
				if (p.getLives() <= 0) {
					h.drawer();
					b.printBoard();
					b.moreLevels = false;
					System.out.println("You lose :(");
					break;
				}
				p.drawer();
				dotTotal = b.dotCount();
				if (dotTotal == 0) {
					b.printBoard();
					System.out.println("Level completed!");
					b.setMap(b.getMap() + 1);
					break;
				}
				h.drawer();
				// h.locator();
				b.printBoard();
				System.out
						.println("Lives: " + p.getLives() + " Dots left: " + dotTotal + " Turns: " + p.getTurnCount());
				p.setTurnCount(p.getTurnCount() + 1);
			}
		}
		double finish = System.currentTimeMillis();
		System.out.println("Run time " + (finish - start) / 1000);
		System.out.println("Sequence: ");
		printMoves(moves);
	}

	private void printMoves(ArrayList<Integer> m) {
		if (!m.isEmpty()) {
			for (int i = 0; i < m.size(); i++) {
				switch (m.get(i)) {
				case 0:
					System.out.println("Up");
					break;
				case 1:
					System.out.println("Right");
					break;
				case 2:
					System.out.println("Down");
					break;
				case 3:
					System.out.println("Left");
					break;
				}
			}
		}
	}
}
