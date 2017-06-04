package AIPlayer;

public class Movement {
	public int srcX, srcY;
	public int dstX, dstY;
	@Override
	public String toString() {
		return "Move piece from (" + Integer.toString(srcX) + ", " + Integer.toString(srcY) + ") to (" + Integer.toString(dstX) + ", " + Integer.toString(dstY) + ")"; 
	}
}