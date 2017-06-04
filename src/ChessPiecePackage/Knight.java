package ChessPiecePackage;
import java.lang.Math.*;

import ChessBoardPackage.ChessBoard;
/*
 * Knight class, a child of ChessPiece. All it does is checking whether the specified move conforms to the knight's rule
 * */
public class Knight extends ChessPiece {
	public Knight(boolean color, int x, int y, int id) {
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
		// Only 8 possible movements for a knight
		return (x == this.x - 2 && y == this.y - 1) || (x == this.x - 2 && y == this.y + 1) || 
				(x == this.x - 1 && y == this.y - 2) || (x == this.x - 1 && y == this.y + 2) ||
				(x == this.x + 1 && y == this.y - 2) || (x == this.x + 1 && y == this.y + 2) || 
				(x == this.x + 2 && y == this.y - 1) || (x == this.x + 2 && y == this.y + 1);
	}
}
