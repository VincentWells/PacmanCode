package pacman;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

/**
 * 
 * @author Vincent This class contains my code for the 'octopi'. As a reference
 *         to their doubling, I named they Hydrae instead. This class contains
 *         the operations that all the Hydrae perform, while Hydra.java contains
 *         the code for the individual Hydrae. notes: I made the Hyrae appear as
 *         'M''s normally and 'W''s when Pac-man has eaten a power dot, because
 *         I wasn't able to find a good way to make the 'M''s blue in the output
 *         font.  It's quite possible that the sequence this program finds isn't
 *         strictly an ideal sequence, as it may unnecessarily go out of it's way
 *         to avoid Hydra's, but it will successfully complete the program without 
 *         losing any lives (and it uses an iterative search).  It's possible that
 *         I could achieve a better sequence by tweaking the heuristics at small 
 *         levels, but the original method (multiplying the negative sightings 
 *         score by 0.5) led to an unnecessary death, and manually testing all
 *         intermittent values seems pointless.
 */

class HydraeOperator {
	Board b;
	Pacman p;
	LinkedList<Hydra> hydrae = new LinkedList<Hydra>();
	private int spawnX;
	private int spawnY;
	private int seed;
	Random RNG;
	
	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

	public HydraeOperator(Pacman pac, Board board) {
		b = board;
		p = pac;
	}
	
	/**
	 * creates the hydra list, resets it's size, and generates the first hydra
	 */
	
	public void initHydrae() {
		hydrae = new LinkedList<Hydra>();
		hydrae.clear();
		Hydra h = new Hydra(b);
		spawnX = b.getHx();
		spawnY = b.getHy();
		h.setXLocation(spawnX);
		h.setYLocation(spawnY);
		hydrae.addFirst(h);
	}

	/**
	 * this method adds a new hydra to the board, so I use it to spawn each
	 * hydra at the beginning of the game and to duplicate them when one is
	 * killed. i represents which Hydrae is spawning.
	 * 
	 * @param i
	 */
	
	void spawn() {
		Hydra h = new Hydra(b);
		hydrae.add(h);
		h.setXLocation(spawnX);
		h.setYLocation(spawnY);
	}

	/**
	 * this method moves each of the hydrae in my linked list.  It begins by
	 * replacing any stored pacman characters with ' ', so if it has eaten 
	 * pacman, a Hydra will not leave a trail of pacman characters behind
	 * it.  Then it iterates through the list, checking whether it has seen
	 * pacman and responding appropriately.  Each hydra will check for vision
	 * of pacman before and after moving in order to make sure pacman will
	 * never pass by a hydra without being seen.
	 */
	
	void makeMove() {
		RNG = new Random(seed/3);
		ListIterator<Hydra> it = hydrae.listIterator();
		Hydra h = it.next();
		while (it.hasNext()) {
			switch (h.getPrev()) {
			case 'v':
			case '^':
			case '<':
			case '>':
				h.setPrev(' ');
			}
			b.board[h.getXLocation()][h.getYLocation()] = h.getPrev();
			h = it.next();
		}
		it = hydrae.listIterator();
		h = it.next();
		while (it.hasNext()) {
			if (sweeper(h)) {
				seekMove(h);
			} else {
				defaultMove(h);
			}
////			sweeper(h);
			h = it.next();
		}
	}
	
	/**
	 * This is the move made by Hydra who has sighted pacman. As per specifications,
	 * if a Hydra come within line of sight of pacman, it will go to pacman's
	 * last known location.  Since it updates this location every turn, it will
	 * pursue pacman until line of sight is broken.
	 * @param h
	 */
	
	private void seekMove(Hydra h) {
		if (h.getXLocation() > h.getSeekX()) {
			h.mover(0);
			h.simMoves.push(0);
		} else if (h.getXLocation() < h.getSeekX()) {
			h.mover(2);
			h.simMoves.push(2);
		} else if (h.getYLocation() > h.getSeekY()) {
			h.mover(3);
			h.simMoves.push(3);
		} else if (h.getYLocation() < h.getSeekY()) {
			h.mover(1);
			h.simMoves.push(1);
		} else {
			h.setSeeker(false);
			defaultMove(h);
		}
		h.setPrev(b.board[h.getXLocation()][h.getYLocation()]);
	}
	
	/**
	 * This method makes moves when pacman has not been sighted.  It will choose a 
	 * random direction, and continue in that direction until either it hits an
	 * intersection or a dead end. At intersections, it will randomly choose a new
	 * direction until it finds a direction in which it can legally move.
	 * @param h
	 */
	
	private void defaultMove(Hydra h) {
		boolean moveMade = false;
		while (moveMade == false) {
			int m = (int) (RNG.nextInt(4));
			if (intersection(h) == 2) {
				if (h.mover(h.getDir())) {
					moveMade = true;
					h.setPrev(h.b.board[h.getXLocation()][h.getYLocation()]);
					h.simMoves.push(h.getDir());
				} else {
					if (h.mover(m)) {
						h.setDir(m);
						moveMade = true;
						h.setPrev(h.b.board[h.getXLocation()][h.getYLocation()]);
						h.simMoves.push(m);
					} else
						moveMade = false;
				}
			} else {
				if (h.mover(m)) {
					h.setDir(m);
					moveMade = true;
					h.setPrev(h.b.board[h.getXLocation()][h.getYLocation()]);
					h.simMoves.push(m);
				} else {
					moveMade = false;
				}
			}
		}
	}

	/**
	 * this method tests how many illegal moves there are. When there are 2
	 * illegal moves, there are 2 legal moves and therefore there is no
	 * intersection. If there are 3 or 4 legal moves at any point, there the
	 * Hydra is at an intersection.  It is possible to have 2 legal moves at 
	 * a corner intersection, but if the Hydra cannot continue to move ahead,
	 * it will choose a new direction anyway.
	 */
	
	private int intersection(Hydra h) {
		int x = 4;
		if (h.getYLocation() == 0 || h.getYLocation() == b.board[0].length - 1) {
			x = 2;
		} else {
			if (b.board[h.getXLocation() + 1][h.getYLocation()] == '='
					|| b.board[h.getXLocation() + 1][h.getYLocation()] == '_') {
				x--;
			}
			if (b.board[h.getXLocation() - 1][h.getYLocation()] == '='
					|| b.board[h.getXLocation() - 1][h.getYLocation()] == '_') {
				x--;
			}
			if (b.board[h.getXLocation()][h.getYLocation() + 1] == '='
					|| b.board[h.getXLocation()][h.getYLocation() + 1] == '_') {
				x--;
			}
			if (b.board[h.getXLocation()][h.getYLocation() - 1] == '='
					|| b.board[h.getXLocation()][h.getYLocation() - 1] == '_') {
				x--;
			}
		}
		return x;
	}

	/**
	 * twice per turn I call this method to register if Pac-man moves on top of
	 * a Hydra or if a Hydra moves on top of Pac-man
	 */
	
	void checkCollision() {
		int count = 0;
		ListIterator<Hydra> it = hydrae.listIterator();
		Hydra h = it.next();
		while (it.hasNext()) {
			if (p.getXLocation() == h.getXLocation() && p.getYLocation() == h.getYLocation()) {
				if (p.blue == false) {
					p.setXLocation(b.board.length - 2);
					p.setYLocation(1);
					p.setDir(2);
					p.setLives(p.getLives() - 1);
//					System.out.println("EATEN");
				} else {
					h.setXLocation(spawnX);
					h.setYLocation(spawnY);
					h.setPrev(' ');
					count++;
				}
				h.setSeeker(false);
			}
			
			h = it.next();
		}
		for (int i = 0; i < count; i++) {
			spawn();
		}
	}

	/**
	 * this method checks to see whether a given hydra can "see" pacman by
	 * seeing if any hydra is on the same row or column as pacman. If so, the
	 * method checks whether there are any obstructions between pacman and the
	 * hydra. If there are no obstructions, pacman is "seen" and the hydra
	 * records his position.
	 */
	
	boolean sweeper(Hydra h) {
		int start;
		int finish;
		boolean sighted = true;
		if (h.getXLocation() == p.getXLocation()) {
			if (h.getYLocation() > p.getYLocation()) {
				start = p.getYLocation();
				finish = h.getYLocation();
			} else {
				start = h.getYLocation();
				finish = p.getYLocation();
			}
			for (int i = start; i < finish; i++) {
				if (b.board[h.getXLocation()][i] == '=' || b.board[h.getXLocation()][i] == '_') {
					sighted = false;
					break;
				}
			}
		} else if (h.getYLocation() == p.getYLocation()) {
			if (h.getXLocation() > p.getXLocation()) {
				start = p.getXLocation();
				finish = h.getXLocation();
			} else {
				start = h.getXLocation();
				finish = p.getXLocation();
			}
			for (int i = start; i < finish; i++) {
				if (b.board[i][h.getYLocation()] == '=' || b.board[i][h.getYLocation()] == '_') {
					sighted = false;
					break;
				}
			}
		} else {
			sighted = false;
		}
		if (sighted == true) {
			
			h.setSeekX(p.getXLocation());
			h.setSeekY(p.getYLocation());
		}
		return sighted;
	}

	/**
	 * Since in a real pacman game, IIRC, the octopi cannot move back into their
	 * box, and neither can Pacman, I wanted to make the Hydrae exit their box
	 * without letting them move through the walls. My solution is this method,
	 * which every turn will cast out any Hydrae inside the box for longer than
	 * a certain number of turns.
	 */
	
	void chucker() {
		ListIterator<Hydra> it = hydrae.listIterator();
		Hydra h = it.next();
		while (it.hasNext()) {
			if (h.getXLocation() == spawnX) {
				for (int j = spawnY - 3; j <= spawnY + 3; j++) {
					if (h.getYLocation() == j) {
						if (h.getBoxTime() < 3) {
							h.setBoxTime(h.getBoxTime() + 1);
						} else {
							b.board[h.getXLocation()][h.getYLocation()] = h.getPrev();
							h.setPrev(b.board[spawnX - 2][j]);
							h.setXLocation(spawnX - 2);
							h.setBoxTime(0);
						}
					}
				}
			}
			h = it.next();
		}
	}

	/**
	 * this method puts the hydra's on the board so their locations can be shown
	 * to the user. Since the hydrae should not overwrite the board, and I don't
	 * want them to store each other and leave a trail of "M"'s or "W"'s all
	 * behind them, I keep them off the board until printing time, and remove
	 * them again before they move.
	 */
	
	void drawer() {
		char Hydra;
		if (p.blue == true) {
			Hydra = 'W';
		} else {
			Hydra = 'M';
		}
		ListIterator<Hydra> it = hydrae.listIterator();
		Hydra h = it.next();
		while (it.hasNext()) {
			b.board[h.getXLocation()][h.getYLocation()] = Hydra;
			h = it.next();
		}
	}
	/**
	 * A method I used in debugging to find the locations of all current hydrae
	 * at a given time
	 */
	void locator(){
		Hydra hy;
		Iterator<Hydra> it = hydrae.iterator();
		while(it.hasNext()){
			hy = it.next();
			System.out.println("x" + hy.getXLocation() + " y" + hy.getYLocation());
		}
	}
}