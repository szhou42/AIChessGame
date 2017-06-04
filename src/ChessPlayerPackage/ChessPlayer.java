package ChessPlayerPackage;

/*
 * Record player statistics
 * */
public class ChessPlayer {
	
	public static final boolean BLACK = false;
	public static final boolean WHITE = true;
	public static final boolean RED = true;
	public static final boolean YELLOW = true;
	
	private boolean color;
	private int score;
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public void incrementScore() {
		score++;
	}

	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void clearStats() {
		score = 0;
	}
	public ChessPlayer(boolean color) {
		this.color = color;
		this.score = 0;
		this.name = (color == RED) ? "Mr Red" : "Mr Black";
	}
	
	
	
	
}
