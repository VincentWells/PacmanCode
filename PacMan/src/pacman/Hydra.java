package pacman;

/**
 * 
 * @author Vincent 
 * 		   Since I wanted most operations to apply to all the Hydrae,
 *         not individuals, I kept the Hydra class to a minimum. I contains
 *         getter/setters for its location, and the string *underneath* (so to
 *         speak) each Hydra so it can be replaced after each move so they won't
 *         help Pac-man eat dots.
 */
class Hydra extends Unit {

	private int boxTime = 0;
	private int seekX = 0;
	private int seekY = 0;
	private boolean seeker = false;
	private int dir = 0;
	Stack<Integer> simMoves;
	
	public int getSeekX() {
		return seekX;
	}

	public void setSeekX(int seekX) {
		this.seekX = seekX;
	}

	public int getSeekY() {
		return seekY;
	}

	public void setSeekY(int seekY) {
		this.seekY = seekY;
	}

	public boolean getSeeker() {
		return seeker;
	}

	public void setSeeker(boolean seeker) {
		this.seeker = seeker;
	}

	public Hydra(Board b) {
		super(b);
		simMoves = new Stack<Integer>();
	}

	public int getBoxTime() {
		return boxTime;
	}

	public void setBoxTime(int boxTime) {
		this.boxTime = boxTime;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}
}
