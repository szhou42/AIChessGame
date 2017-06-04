package ChessPiecePackage;
import java.lang.Math.*;


/*
 * Queen class, a child of ChessPiece. All it does is checking whether the specified move conforms to the queen's rule
 * */
import ChessBoardPackage.ChessBoard;
public class Queen extends ChessPiece {
	public Queen(boolean color, int x, int y, int id) {
		super(color, x, y);
	}
	/*
	 * Moving rule for this chess piece
	 * Make sure Queen moves only vertically/horizontally/diagonally
	 * board: chess board instance
	 * dstX: which row
	 * dstY: which column
	 * return val: return true when the move conform the piece's move rule
	 * */
	public boolean conformPieceRule(ChessBoard board, int dstX, int dstY) {
		return ChessBoard.isVertical(this.x, this.y, dstX, dstY) || ChessBoard.isHorizontal(this.x, this.y, dstX, dstY) 
				|| ChessBoard.isDiagonal(this.x, this.y, dstX, dstY);
	}
}
