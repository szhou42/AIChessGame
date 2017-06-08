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
		float ret = minimax(game_state, 4, maxPlayer, true,Integer.MIN_VALUE, Integer.MAX_VALUE);
		System.out.println("best move will have ranking: " + Float.toString(ret));
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
	
	public float minimax(GameState game_state, int depth, boolean max_player, boolean firstRecur, float alpha, float beta) {
		float best_val;
		//System.out.println(Boolean.toString(max_player) + " turn");
		// Base Case
		if(depth == 0 || game_state.is_terminal()) {
			return game_state.heuristic(1);
		}
		
		// Recursive Case
		if(max_player) {
			best_val = Float.MIN_VALUE;
			for(GameState child_state : game_state) {
				// Debug condition
				//if(firstRecur && child_state.getChessboard().getPiece(4, 3).getColor() == false)
				//	System.out.println("Stop here\n");

				float ret = minimax(child_state, depth - 1, false, false, alpha, beta);
				float prevAlpha = alpha;
				alpha = Math.max(alpha, ret);
				if(firstRecur && alpha > prevAlpha)
					tmpState = child_state;
				if(alpha >= beta)
					break;
			}
			if(firstRecur) {
				System.out.println(tmpState.getChessboard());
			}
			return alpha;
		}
		else {
			best_val = Float.MAX_VALUE;
			for(GameState child_state : game_state) {
				float ret = minimax(child_state, depth - 1, true, false, alpha, beta);
				float prevBeta = beta;
				beta = Math.min(beta, ret);
				if(firstRecur && beta < prevBeta)
					tmpState = child_state;
				if(alpha >= beta)
					break;
			}
			if(firstRecur) {
				System.out.println(tmpState.getChessboard());
			}
			return beta;
		}
	}
	
	public static void main(String[] args) {
		
		String[][] strb = 
			{ 
			{ "BR", "NN", "BB", "BQ", "BK", "BB", "BH", "BR" },
			{ "BP", "BP", "BP", "BP", "BP", "BP", "BP", "BP" }, 
			{ "BH", "NN", "NN", "NN", "NN", "NN", "NN", "NN" },
			{ "NN", "NN", "NN", "NN", "NN", "NN", "NN", "NN" }, 
			{ "NN", "WP", "NN", "NN", "NN", "NN", "NN", "NN" },
			{ "NN", "NN", "NN", "NN", "NN", "NN", "NN", "NN" }, 
			{ "WP", "WP", "WP", "WP", "WP", "WP", "WP", "WP" },
			{ "WR", "WH", "WB", "WQ", "WK", "WB", "WH", "WR" },
			};
		
		ChessBoard b = new ChessBoard(strb, false);
		// Black(Max) goes first
		//b.setTurn(false);
		System.out.println("Before: ");
		System.out.println(b);
		System.out.println("After########################################: ");
		Minimax m = new Minimax();
		System.out.println(m.minimax_move(b));		
	}
}
