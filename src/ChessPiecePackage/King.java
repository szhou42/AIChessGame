package ChessPiecePackage;
import java.lang.Math.*;

import ChessBoardPackage.ChessBoard;

/*
 * King class, a child of ChessPiece. All it does is checking whether the specified move conforms to the king's rule
 * */
public class King extends ChessPiece {
	public King(boolean color, int x, int y, int id) {
		super(color, x, y);
	}
	/*
	 * Moving rule for this chess piece
	 * board: chess board instance
	 * dstX: which row
	 * dstY: which column
	 * return val: return true when the move conform the piece's move rule
	 * */
	public boolean conformPieceRule(ChessBoard board, int x, int y) {
		// King should only move in his own 3*3 square
		return (Math.abs(x - this.x) <= 1 && Math.abs(y - this.y) <= 1);
	}
	
}
