package ChessPiecePackage;
import java.lang.Math.*;


/*
 * Rook class, a child of ChessPiece. All it does is checking whether the specified move conforms to the rook's rule
 * */
import ChessBoardPackage.ChessBoard;
public class Rook extends ChessPiece {
	public Rook(boolean color, int x, int y, int id) {
		super(color, x, y);
	}
	/*
	 * Moving rule for this chess piece
	 * Make sure Rook moves only vertically/horizontally
	 * board: chess board instance
	 * dstX: which row
	 * dstY: which column
	 * return val: return true when the move conform the piece's move rule
	 * */
	public boolean conformPieceRule(ChessBoard board, int dstX, int dstY) {
		return ChessBoard.isHorizontal(this.x, this.y, dstX, dstY) || ChessBoard.isVertical(this.x, this.y, dstX, dstY);
	}
}
