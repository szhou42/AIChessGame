package AIPlayer;
import java.util.*;
import AIPlayer.GameState;
import ChessPiecePackage.ChessPiece;
import ChessBoardPackage.ChessBoard;

public class GameStateIterator implements Iterator<GameState> {

	public class itr_tuple {
		public int itrX, itrY, destX, destY;
		public itr_tuple(int a, int b, int c, int d) {itrX = a; itrY = b; destX = c; destY = d;}
	}	
	
	// For current player, states reachable are:
	// for every piece, examine all possible moves, each legal move would be a state
	GameState parentState;
	
	// Current iterating piece position
	int itrX, itrY;
	
	// Piece on (itrX, itrY) move to (destX, destY)
	int destX, destY;
	
	// turn (opposite to the chess)
	boolean turn;
	int counter;
	public GameStateIterator(GameState gameState) {
		parentState = gameState;
		turn = parentState.getChessboard().getTurn();
		itrX = 0;
		itrY = 0;
		destX = 0;
		destY = 0;
		counter = 64*8*2*2;
	}

	@Override
	public boolean hasNext() {		
		int row = itrX;
		int column = itrY;
		int destRow = destX;
		int destColumn = destY;
		
		while(row <= 8) {
			itr_tuple ret = updateItrInfo(row, column, destRow, destColumn);
			row = ret.itrX;
			column = ret.itrY;
			destRow = ret.destX;
			destColumn = ret.destY;
			
			ChessPiece piece = parentState.getChessboard().getPiece(row, column);
			if(piece == null) continue;
			// This piece has legal move
			if(piece.isLegalMove(parentState.getChessboard(), destRow, destColumn, false, true))				
				return true;
		}
		return false;
	}

	@Override
	public GameState next() {
		int row = itrX;
		int column = itrY;
		int destRow = destX;
		int destColumn = destY;
		
		while(itrX <= 8) {
			itr_tuple ret = updateItrInfo(itrX, itrY, destX, destY);
			itrX = ret.itrX;
			itrY = ret.itrY;
			destX = ret.destX;
			destY = ret.destY;
			
			ChessPiece piece = parentState.getChessboard().getPiece(itrX, itrY);
			if(piece == null) continue;
			// This piece has legal move
			if(piece.isLegalMove(parentState.getChessboard(), destX, destY, false, true)) {						
				// Make a new game state(with its own copies of chess board and everything) and return
				ChessBoard newBoard = new ChessBoard(parentState.getChessboard());
				newBoard.movePiece(itrX, itrY, destX, destY);	
				return new GameState(newBoard);
			}
		}
		return null;
	}

	private itr_tuple updateItrInfo(int row, int column, int destRow, int destColumn) {
		int local_itrX = row;
		int local_itrY = column;
		int local_destX = destRow;
		int local_destY = destColumn;
		
		local_destY++;
		if(local_destY >= 8) {
			local_destY = local_destY % 8;
			local_destX++;
			if(local_destX >= 8) {
				local_destX = local_destX % 8;
				local_itrY++;
				if(local_itrY >= 8) {
					local_itrY = local_itrY % 8;
					local_itrX++;
				}
			}
		}
		return new itr_tuple(local_itrX, local_itrY, local_destX, local_destY);
	}
	
	@Override
	public void remove() {
		// Do nothing
	}
}
