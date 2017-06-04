package AIPlayer;
import java.util.Iterator;

import ChessBoardPackage.ChessBoard;

public class GameState implements Iterable<GameState>{

	ChessBoard chessboard;
	public GameState(ChessBoard chessboard) {
		this.chessboard = chessboard;
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
	 * */
	public int heuristic() {
		int num_max_pieces = chessboard.remainingBlackPieces(), num_min_pieces = chessboard.remainingWhitePieces();
		return (int)(10 * (float) num_max_pieces / num_min_pieces);
	}

	@Override
	public Iterator<GameState> iterator() {
		return new GameStateIterator(this);
	}
}
