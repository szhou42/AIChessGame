package ChessPiecePackage;
import java.lang.Math.*;
import ChessBoardPackage.ChessBoard;

/*
 * Bishop class, a child of ChessPiece. All it does is checking whether the specified move conforms to the bishop's rule
 * */
public class Bishop extends ChessPiece {
	public Bishop(boolean color, int x, int y, int id) {
		super(color, x, y);
	}
	/*
	 * Moving rule for this chess piece
	 * board: chess board instance
	 * dstX: which row
	 * dstY: which column
	 * return val: return true when the move conform the piece's move rule
	 * */
	public boolean conformPieceRule(ChessBoard board, int dstX, int dstY) {
		return ChessBoard.isDiagonal(this.x, this.y, dstX, dstY);
	}
}
