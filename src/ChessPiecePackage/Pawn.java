package ChessPiecePackage;
import java.lang.Math.*;


/*
 * Pawn class, a child of ChessPiece. All it does is checking whether the specified move conforms to the pawn's rule
 * */
import ChessBoardPackage.ChessBoard;
public class Pawn extends ChessPiece {
	private int firstMove;
	public Pawn(boolean color, int x, int y, int id) {
		super(color, x, y);
		firstMove = 1;
	}
	// For testing only
	public void setFirstMove() {
		firstMove = 0;
	}
	/*
	 * Moving rule for this chess piece
	 * board: chess board instance
	 * dstX: which row
	 * dstY: which column
	 * return val: return true when the move conform the piece's move rule
	 * */
	public boolean conformPieceRule(ChessBoard board, int x, int y) {
		// Black pawn's x increase, whereas White pawn's x decrease
		int direction = this.color ? -1 : 1;
		
		if(this.y == y) {
			// move
			if(board.getPiece(x, y) == null) {
				// A pawn can move 2 steps on first move
				if(firstMove == 1)
					return x == this.x + direction * 2 || x == this.x + direction * 1;
				else
					return x == this.x + direction * 1;
			}
		}
		else {
			// capture
			if(board.getPiece(x, y) != null && Math.abs(y - this.y) == 1) {
				return x == this.x + direction * 1;
			}
		}
		return false;
	}
}