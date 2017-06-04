package ChessPiecePackage;
import java.lang.Math.*;

import ChessBoardPackage.ChessBoard;
/*
 * Crab class, a child of ChessPiece. All it does is checking whether the specified move conforms to the crab's rule
 * */
public class Crab extends ChessPiece {
	public Crab(boolean color, int x, int y, int id) {
		super(color, x, y);
	}
	
	/* Fix: crab is wrong! fix this!
	 * Moving rule for this chess piece
	 * Make sure Crab can only move 1 or 2 steps horizontally
	 * One special case is, a crab can hop 2 steps forward/backward when they are standing on (x, y), where y is an even number 
	 * board: chess board instance
	 * dstX: which row
	 * dstY: which column
	 * return val: return true when the move conform the piece's move rule
	 * */
	public boolean conformPieceRule(ChessBoard board, int dstX, int dstY) {
		if(this.x == dstX) {
			return Math.abs(dstY - this.y) <= 2;
		}
		else {
			return Math.abs(dstX - this.x) == 2 && this.y % 2 == 0 && dstY == this.y;
		}
	}
}
