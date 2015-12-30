package pacman;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class PacmanOperator {

	private Board b = new Board();
	private HydraeOperator h;
	private Pacman p;
	private int initialDots;
	private int initialLives;
	private char[][] q;
	int prev;
	private int turn;

	/**
	 * In the constructor, I set up my algorithm my creating copies of the
	 * various parts of the game state so I can manipulate them without
	 * affecting the real game.
	 * 
	 * @param b
	 *            real game's board
	 * @param h
	 *            real game's HydraeOperator
	 * @param p
	 *            real game's Pacman
	 * @param a
	 *            previous move made
	 * @param t
	 *            current turn count (used to seed the random number generator
	 *            for the hydrae
	 */
	public PacmanOperator(Board b, HydraeOperator h, Pacman p, int a, int t) {
		q = new char[b.board.length][b.board[0].length];
		for (int i = 0; i < b.board.length; i++) {
			System.arraycopy(b.board[i], 0, q[i], 0, b.board[0].length);
		}
		this.b.board = q;
		this.b.setHx(b.getHx());
		this.b.setHy(b.getHy());
		this.p = new Pacman(this.b);
		this.p.setXLocation(p.getXLocation());
		this.p.setYLocation(p.getYLocation());
		this.p.setLives(p.getLives());
		this.h = new HydraeOperator(this.p, this.b);
		Hydra hy;
		Iterator<Hydra> it = h.hydrae.iterator();
		while (it.hasNext()) {
			hy = new Hydra(this.b);
			Hydra hy2 = it.next();
			hy.setXLocation(hy2.getXLocation());
			hy.setYLocation(hy2.getYLocation());
			this.h.hydrae.add(hy);
		}
		initialDots = b.dotCount();
		prev = a;
		turn = t;
	}

	/**
	 * this is the primary method of my self-runner. It finds the best path and
	 * then returns the first move of that path as the next move for the "real"
	 * Pacman. In order to choose the best path, I use a variety of methods.
	 * First, I store a stack of moves, and characters each moves eats. These
	 * stacks allow me to backtrack and explore all possiblities. I also
	 * maintain an ArrayList of boolean indicating whether or not the pacman in
	 * this method is within the line of sight of any Hydrae. Everytime the
	 * stack of moves reaches the desired depth (10), the method calls another
	 * method, which calculates a score for this path. The score is based on how
	 * many dots are consumed, vs how many times pacman is spotted (obviously,
	 * points are good and spottings are bad). I did not disable the seek
	 * function on the Hydrae, because frankly that seemed boring. This meant
	 * that I had to attribute fairly heavy penalties to being spotted in order
	 * for the algorithm to properly complete the level without getting pacman
	 * killed. If a path leads pacman's death, it is ignored (and therefore
	 * never executed unless there are no paths which allow him to live). The
	 * points algorithm ignores power dots; I planned on changing that after I
	 * got it working on a simple map, but the same algorithm still worked fine
	 * so I didn't. Because it wants to avoid sightings, the program never eats
	 * the Hydrae, even though it does iterate out the possiblity of doing so.
	 * Apparently, the cost I put on being sighted outweighs the potential
	 * points gained in all scenarios for this map. Since generally multiplying
	 * the Hydrae would be bad, I like this and didn't change anything to
	 * account for power dots--I neither count them as points, nor try to kill
	 * vulnerable Hydrae (as that is usually a bad idea due to the duplication,
	 * I think).
	 * 
	 * @return the next move
	 */
	int Predictor() {

		initialLives = p.getLives();
		Stack<Integer> moves = new Stack<Integer>();
		Stack<Character> chars = new Stack<Character>();
		ArrayList<Boolean> spotted = new ArrayList<Boolean>();
		double maxPoints = -10;
		int nextMove = -1;
		int fm = 0;
		int lastX = 0;
		int lastY = 0;
		boolean compared = false;

		while (!(moves.size() == 1 && moves.peek() == 4)) {
			for (int m = 0; m < 5; m++) {
				//this if condition tests whether all possible move for a given
				//stage of the tree have been tried.  If all that remains in the
				//the stack is a 4, the while loop termina
				if (m == 4) {
					if (moves.isEmpty()) {
						moves.push(4);
						break;
					}
					int n = moves.pop();
					undoHydraeMove();
					undoPacMove(n, chars.pop());
					spotted.remove(spotted.size() - 1);
					m = n;
					continue;
				}
				//this if statement adds valid moves to the tree
				if (p.mover(m)) {
					p.setPrev(b.board[p.getXLocation()][p.getYLocation()]);
					b.board[p.getXLocation()][p.getYLocation()] = ' ';
					lastX = p.getXLocation();
					lastY = p.getYLocation();
					moves.push(m);
					chars.push(p.getPrev());
					Run();
					if (moves.size() == 1) {
						fm = moves.peek();
						if (nextMove == -1) {
							nextMove = fm;
						}
					}
					Iterator<Hydra> it = h.hydrae.iterator();
					Hydra hy;
					boolean b = false;
					while (it.hasNext()) {
						hy = it.next();
						if (h.sweeper(hy)) {
							b = true;
							break;
						}
					}
					spotted.add(b);
					m = -1;
				}
				//this if statement undoes and reject moves that get the pacman killed
				if (p.getLives() < initialLives) {
					compared = true;
					p.setLives(initialLives);
					if (!moves.isEmpty()) {
						int n = moves.pop();
						undoHydraeMove();
						p.setXLocation(lastX);
						p.setYLocation(lastY);
						undoPacMove(n, chars.pop());
						spotted.remove(spotted.size() - 1);
						m = n;
						continue;
					} else {
						// System.out.println("EMPTY");
					}
				}
				//this if statement determine the maximum depth the search reaches
				if (moves.size() > 11) {
					if (maxPoints < getPoints(chars, spotted)) {
						maxPoints = getPoints(chars, spotted);
						nextMove = fm;
					}
					int n = moves.pop();
					undoHydraeMove();
					undoPacMove(n, chars.pop());
					spotted.remove(spotted.size() - 1);
					m = n;
					continue;
				}
			}
		} 
		
		if (!compared && maxPoints <= 0) {
			int j = prev;
			if (p.mover(j % 4)) {
				nextMove = j % 4;
			} else if (p.mover((j + 1) % 4)) {
				nextMove = (j + 1) % 4;

			} else if (p.mover((j + 3) % 4)) {
				nextMove = (j + 3) % 4;
			} else {
				nextMove = (j + 2) % 4;
			}
		}
		
		return nextMove;
	}

	/**
	 * as mentioned above, this method computes the score. The score is based on
	 * the number of dots eaten and the number of moves which lead to pacman
	 * being sighted. I weigh dots eaten immediately more heavily than dots
	 * eaten later on because I got some bugs where pacman would move back and
	 * forth next to a small dot island because the max score would be the same
	 * for move away and then eating the island vs just eating it ASAP, which
	 * was obviously bad.
	 * 
	 * @param s
	 *            is the characters eaten in the path
	 * @param p
	 *            is a list of whether pacman was in vision of a hydra at each
	 *            turn.
	 * @return a score to determine how "good" a certain path is
	 */
	double getPoints(Stack<Character> s, ArrayList<Boolean> p) {
		double count = 0;
		Stack<Character> t = new Stack<Character>();
		while (!s.isEmpty()) {
			char n = s.pop();
			if (n == '.') {
				count += 1 + (1 - (s.size() / 10.0));
			}
			t.push(n);
		}
		while (!t.isEmpty()) {
			s.push(t.pop());
		}
		double sighted = 0;
		for (int i = 0; i < p.size(); i++) {
			if (p.get(i)) {
				sighted++;
			}
		}
		return count - sighted;
	}

	/**
	 * the backtracking method. undoes the last move on the stack and replaces
	 * the last character on the square so the dotcount for the score won't be
	 * ruined.
	 * 
	 * @param i
	 * @param c
	 */
	void undoHydraeMove() {
		if (!h.hydrae.isEmpty()) {
			Iterator<Hydra> it = h.hydrae.iterator();
			Hydra hy;
			while (it.hasNext()) {
				hy = it.next();
				if (!hy.simMoves.isEmpty()) {
					undoMove(hy, hy.simMoves.pop());
				}
			}
		}
	}

	void undoPacMove(int i, char c) {
		b.board[p.getXLocation()][p.getYLocation()] = c;
		undoMove(p, i);
		turn--;
	}

	void undoMove(Unit u, int i) {
		switch (i) {
		case 0: {
			u.setXLocation(u.getXLocation() + 1);
			break;
		}
		case 2: {
			u.setXLocation(u.getXLocation() - 1);
			break;
		}
		case 1: {
			if (u.getYLocation() == 0) {
				u.setYLocation(b.board[0].length - 1);
				break;
			} else {
				u.setYLocation(u.getYLocation() - 1);
				break;
			}

		}
		case 3: {
			if (u.getYLocation() == b.board[0].length - 1) {
				u.setYLocation(0);
				break;
			} else {
				u.setYLocation(u.getYLocation() + 1);
				break;
			}
		}
		}
	}

	/**
	 * simulates the moves of the Hydrae, and the collision they cause, etc. not
	 * I don't ever bother spawning the hydrae at the start because at such
	 * early moves, they aren't factors in the game anyways, and the runtime
	 * cost was considerable. I would also have had to despawn them via some
	 * marker which was too much work.
	 */
	void Run() {
		h.setSeed(turn);
		int dotTotal = b.dotCount();
		h.checkCollision();
		// h.makeMove();
		h.chucker();
		h.checkCollision();
		if (dotTotal == 0) {
		}
		turn++;
	}

	/**
	 * These 2 methods were purely for debugging. Used them to trace the stacks
	 * at early stages of this simulation to make sure everything was working
	 * properly. (probably should have made a single method do both things with
	 * generics, but I was too lazy).
	 * 
	 * @param s
	 *            stack being passed in
	 */
	private void stackPrint(Stack<Integer> s) {
		Stack<Integer> t = new Stack<Integer>();
		int n;
		while (!s.isEmpty()) {
			n = s.pop();
			System.out.print(n + " ");
			t.push(n);
		}
		while (!t.isEmpty()) {
			s.push(t.pop());
		}
		System.out.println();
		System.out.println("x" + p.getXLocation() + " y" + p.getYLocation());
	}

	private void stackPrint2(Stack<Character> s) {
		Stack<Character> t = new Stack<Character>();
		char n;
		while (!s.isEmpty()) {
			n = s.pop();
			System.out.print(n + " ");
			t.push(n);
		}
		while (!t.isEmpty()) {
			s.push(t.pop());
		}
		System.out.println();
	}
}
