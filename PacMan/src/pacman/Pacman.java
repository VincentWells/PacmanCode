/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

/**
 * This class contains the logic for the player character, Pac-man. Most of the
 * methods either read the user input and then make operations, or evaluate the
 * effects of the move just made. notes: I tried to represent Pac-man's mouth
 * for my Pac-man character, as 'v ^ > <' (the mouth facing the direction he's
 * moving). It didn't turn out as well as I had hoped, but hopefully it was
 * recognizable at least.
 * 
 * @author Vincent Wells
 */
class Pacman extends Unit {
	public Pacman(Board b) {
		super(b);
	}

	/**
	 * these values x and y represent the start coordinates of Pac-man on my
	 * board.
	 */

	public void initPac() {
		super.setXLocation(b.board.length - 2);
		super.setYLocation(1);
	}

	/**
	 * this boolean, blue, will be used to indicate whether a power dot has been
	 * eaten. The name references how the octopi turned blue when they were
	 * vulnerable in the real Pac-man game.
	 */
	
	boolean blue = false;
	private int lives = 3;
	private int turnCount = 0;
	private int dir = 2;
	
	/**
	 * the int dir is used to determine how to represent which way pacman is
	 * facing.  The getter/setter's allow other class to alter this. Specifically,
	 * the HydraeOperator can set the direction if pacman is eaten.
	 * @return
	 */
	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}
	
	/**
	 * I store the turn count in pacman for convenience.  Whenever pacman attempts
	 * a move, the turn count is increased.  
	 * @return
	 */
	
	public int getTurnCount() {
		return turnCount;
	}

	public void setTurnCount(int turnCount) {
		this.turnCount = turnCount;
	}
	
	/**
	 * the getter/setter for lives allows the class Hydrae to take away lives
	 * when Pac-man is eaten, and allow the main method in PacmanRunner to test
	 * whether the game is over and output the number of lives remaining to the
	 * user.
	 * 
	 * @param i
	 */
	
	public void setLives(int i) {
		lives = i;
	}
	
	public int getLives() {
		return lives;
	}

	

	/**
	 * pacman's makeMove calls its super's, Unit, move method, but first converts
	 * the users char input to the correct corresponding integer.
	 * It is a boolean so the program can prompt the user to re-move if they enter
	 * an invalid move.
	 */
	
	private int n = 0;

	boolean makeMove(int c) {
		b.board[super.getXLocation()][super.getYLocation()] = ' ';
		switch (c) {
		case 0:
			if (super.mover(0) == false) {
//				System.out.println("Invalid move");
				return false;
			} else {
				if (b.board[super.getXLocation()][super.getYLocation()] == 'o') {
					n = turnCount + 5;

				}
				blueTrue(n);
				dir = 0;
				return true;
			}
		case 1:
			if (super.mover(1) == false) {
//				System.out.println("Invalid move");
				return false;
			} else {
				if (b.board[super.getXLocation()][super.getYLocation()] == 'o') {
					n = turnCount + 5;

				}
				blueTrue(n);
				dir = 2;
				return true;
			}
		case 2:
			if (super.mover(2) == false) {
//				System.out.println("Invalid move");
				return false;
			} else {
				if (b.board[super.getXLocation()][super.getYLocation()] == 'o') {
					n = turnCount + 5;

				}
				blueTrue(n);
				dir = 1;
				return true;
			}
		case 3:
			if (super.mover(3) == false) {
//				System.out.println("Invalid move");
				return false;
			} else {
				if (b.board[super.getXLocation()][super.getYLocation()] == 'o') {
					n = turnCount + 5;

				}
				blueTrue(n);
				dir = 3;
				return true;
			}
		default:
//			System.out.println("Invalid move");
			return false;
		}
	}

	/**
	 * this method is called every time Pac-man moves, in order to check every
	 * turn whether or not a power dot is active. If a power dot has been
	 * consumed within the last 5 turns, it will make blue = true and allow
	 * Pac-man to eat the Hydrae.
	 * 
	 * @param i
	 */
	void blueTrue(int i) {
		if (turnCount < i)
			blue = true;
		else
			blue = false;
	}
	/**
	 * this method add's pacman to the board at the end of each cycle, so he will be
	 * represented when printing without actually editing the contents of the board[][] 
	 * double array
	 */
	void drawer() {
		char pac = 'C';
		switch (dir) {
		case 0:
			pac = 'v';
			break;
		case 1:
			pac = '^';
			break;
		case 2:
			pac = '<';
			break;
		case 3:
			pac = '>';
			break;
		}
		b.board[super.getXLocation()][super.getYLocation()] = pac;
	}

	

	

}