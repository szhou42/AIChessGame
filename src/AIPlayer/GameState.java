package AIPlayer;
import ChessBoardPackage.ChessBoard;
import ChessPiecePackage.ChessPiece;

import java.util.*;
public class GameState implements Iterable<GameState>{

	ChessBoard chessboard;
	HashMap<String, Integer> weights = new HashMap<String, Integer>();
	public GameState(ChessBoard chessboard) {
		this.chessboard = chessboard;
		weights.put("King", 50);
		weights.put("Queen", 20);
		weights.put("Bishop", 7);
		weights.put("Rook", 8);
		weights.put("Knight", 7);
		weights.put("Pawn", 3);
	}

	public ChessBoard getChessboard() {
		return chessboard;
	}

	/*
	 * Is it a checkmate or stalemate ?
	 * */
	public boolean is_terminal() {
		return chessboard.isGameOver();
	}
	
	/*
	 * The heuristic function is solely determined by how many pieces that the max(default to black) player has left on chess board, 
	 * compared to red player.
	 * 
	 * Heuristic2: 
	 * Each piece is given different weights to indicate its importance, the sum of all piece weights give the utility of the state
	 * 
	 * */
	public float heuristic(int id) {
		if(id == 0) {
			int num_max_pieces = chessboard.remainingBlackPieces(), num_min_pieces = chessboard.remainingWhitePieces();
			return (10 * (float) num_max_pieces / num_min_pieces);			
		}		
		else if(id == 1){
			int blackSum = 0;
			int redSum = 0;

			// Get each of the black pieces, 
			for (int row = 0; row < 8; row++) {
				for (int column = 0; column < 8; column++) {
					ChessPiece piece = chessboard.getPiece(row, column);
					if(piece == null) continue;
					if(piece.getColor() == ChessPiece.BLACK)
						blackSum = blackSum + weights.get(piece.getClass().getSimpleName());
					else
						redSum = redSum + weights.get(piece.getClass().getSimpleName());
				}
			}
			// if(blackSum != redSum)
			//	System.out.println("sum differs......" + Integer.toString(blackSum) + " " + Integer.toString(redSum));
			return (float)blackSum / redSum;			
		}
		return 0;
	}

	@Override
	public Iterator<GameState> iterator() {
		return new GameStateIterator(this);
	}
}
