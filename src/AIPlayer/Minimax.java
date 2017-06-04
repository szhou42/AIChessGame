package AIPlayer;
import java.util.*;
import ChessBoardPackage.ChessBoard;

public class Minimax {

	private GameState tmpState;
	
	
	/*
	 * Returns the move that maximize chances of winning
	 * */
	public Movement minimax_move(ChessBoard chessboard) {
		GameState game_state = new GameState(chessboard);
		tmpState = null;
		boolean maxPlayer = !chessboard.getTurn();
		int ret = minimax(game_state, 3, maxPlayer, true);
		System.out.println("best move will have ranking: " + Integer.toString(ret));
		return state2Movement(game_state, tmpState);
	}
	
	private Movement state2Movement(GameState originalState, GameState currState) {
		if(currState == null) return null;
		Movement m = new Movement();
		ChessBoard orgb = originalState.getChessboard();
		ChessBoard curb = currState.getChessboard();
		boolean sourceFound, destFound;
		// If some piece on original state is missing in curr_state, that's the source
		// If some piece on original state that has nothing or something, but curr state says it has something else, that's the dest
		for(int row = 0; row < 8; row++) {
			for(int col = 0; col < 8; col++) {
				if(orgb.getPiece(row, col) != null && curb.getPiece(row, col) == null) {
					m.srcX = row;
					m.srcY = col;
				}
				if(orgb.getPiece(row, col) == null && curb.getPiece(row, col) != null) {
					m.dstX = row;
					m.dstY = col;
				}
				if(orgb.getPiece(row, col) != null && curb.getPiece(row, col) != null && orgb.getPiece(row, col).getColor() != curb.getPiece(row, col).getColor()) {
					m.dstX = row;
					m.dstY = col;
				}
			}
		}
		return m;
	}
	
	public int minimax(GameState game_state, int depth, boolean max_player, boolean firstRecur) {
		int best_val;
		//System.out.println(Boolean.toString(max_player) + " turn");
		// Base Case
		if(depth == 0 || game_state.is_terminal()) {
			return game_state.heuristic();
		}
		
		// Recursive Case
		if(max_player) {
			best_val = Integer.MIN_VALUE;
			for(GameState child_state : game_state) {
				int ret = minimax(child_state, depth - 1, false, false);
				best_val = Math.max(best_val, ret);
				if(firstRecur && best_val >= ret)
					tmpState = child_state;
			}
		}
		else {
			best_val = Integer.MAX_VALUE;
			for(GameState child_state : game_state) {
				int ret = minimax(child_state, depth - 1, true, false);
				best_val = Math.min(best_val, ret);
			}
		}
		if(firstRecur) {
			System.out.println(tmpState.getChessboard());
		}
		return best_val;
	}
	
	public static void main(String[] args) {
		ChessBoard b = new ChessBoard();
		// Black(Max) goes first
		b.setTurn(false);
		//System.out.println(b);
		Minimax m = new Minimax();
		System.out.println(m.minimax_move(b));		
	}
}