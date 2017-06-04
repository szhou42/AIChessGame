package ChessPiecePackage;

/*
 * Rabbit class, a child of ChessPiece. All it does is checking whether the specified move conforms to the rabbit's rule
 * */
import java.lang.Math.*;

import ChessBoardPackage.ChessBoard;
public class Rabbit extends ChessPiece {
	public Rabbit(boolean color, int x, int y, int id) {
		super(color, x, y);
	}
	
	/*
	 * Moving rule for this chess piece
	 * Make sure Rabbit always jump 2 steps, and it can never go horizontally
	 * board: chess board instance
	 * dstX: which row
	 * dstY: which column
	 * return val: return true when the move conform the piece's move rule
	 * */
	public boolean conformPieceRule(ChessBoard board, int dstX, int dstY) {
		return Math.abs(dstX - this.x) == 2 && dstY == this.y;
	}
}
