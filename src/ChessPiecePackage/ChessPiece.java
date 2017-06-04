package ChessPiecePackage;

import ChessBoardPackage.ChessBoard;

/*
 * The ChessPiece class, it's the parent of all child chess piece's classes
 * It provides functions to move a piece, and functions to check whether a piece has legal move, and stores data such as piece position, color
 * for the sub-classes to inherit
 * */
public class ChessPiece {
	public static final boolean WHITE = true;
	public static final boolean BLACK = false;
	// Current position of chess piece
	protected int x;
	protected int y;

	// Player(white/black)
	protected boolean color;

	/**
	 * Setter for x
	 * */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * Setter for y
	 * */
	public void setY(int y) {
		this.y = y;
	}
	
	/*
	 * Get piece color
	 * return val: piece color
	 * */
	public boolean getColor() {
		return color;
	}
	/*
	 * Get piece x coordinate
	 * return val: piece x coordinate
	 * */
	public int getX() {
		return x;
	}
	/*
	 * Get piece y coordinate
	 * return val: piece y coordinate
	 * */
	public int getY() {
		return y;
	}
	
	/*
	 * Initialize a chess piece
	 * color : which player
	 * x: initial x position
	 * y: initial y position
	 * */
	public ChessPiece(boolean color, int x, int y) {
		this.color = color;
		this.x = x;
		this.y = y;
	}

	/*
	 * is (this.x, this.y) - > (x, y) a legal move
	 * pseudo : is this a call from a pseudo move function
	 * x: destination row
	 * y: destination column
	 * enable_turn: If false, isLegalMove() will not take current turn into consideration
	 * return val: true if move is legal
	 * */
	public boolean isLegalMove(ChessBoard board, int x, int y, boolean pseudo, boolean enable_turn) {
		// Checks that are specific to different chess piece
		if(!conformPieceRule(board, x, y))
			return false;
		// Common checks
		if(enable_turn)
			if(board.getTurn() != this.color)
				return false;
		if(!board.isInChessBoard(x, y))
			return false;
		if(board.getPiece(x, y) != null && board.getPiece(x, y).getColor() == this.color)
			return false;
		if(ChessBoard.isPathBlocked(board, this.x, this.y, x, y))
			return false;
		// If pseudo is true, the piece can move and don't care whether this will put king is check, this is a pseudo move(a hack for this chicken and egg problem)
		if(!pseudo)
			if(willMyKingDie(board, x, y))
				return false;
		return true;
	}
	
	/*
	 * Move to point (x,y)
	 * If (x,y) is invalid, or other pieces are blocking, return false
	 * return val: true if move is made successfully
	 * */
	public boolean move(ChessBoard board, int x, int y) {
		if(!isLegalMove(board, x, y, false, true))
			return false;
		board.setPiece(null, this.x, this.y);
		board.setPiece(this, x, y);
		// Update current position
		this.x = x;
		this.y = y;
		// Let your opponent play 
		board.setTurn(!color);
		return true;
	}
	
	/*
	 * Define different move rule for different pieces in here
	 * return val: true move conforms to the piece's rule
	 * */
	public boolean conformPieceRule(ChessBoard board, int x, int y) {
		// Implement in subclass
		return false;
	}

	/*
	 * pseudoMove() is very similar to move()
	 * It's used to test whether certain move will result king being checked without actually affecting the current chess board
	 * board: the chess board
	 * x: destination row
	 * y: destination column
	 * */
	public ChessPiece pseudoMove(ChessBoard board, int x, int y) {
		ChessPiece targetPiece = board.getPiece(x, y);
		board.setPiece(null, this.x, this.y);
		board.setPiece(this, x, y);
		// Update current position
		this.x = x;
		this.y = y;
		// Let your opponent play 
		board.setTurn(!color);
		return targetPiece;
	}
	/*
	 * Recover from pseudoMove()
	 * These two functions should always show up in pairs.
	 * board: the chess board
	 * x: destination row
	 * y: destination column
	 * originalX: original row
	 * originmalY: original column
	 * piece: the piece that was in (x,y)
	 * */
	public void cancelPseudoMove(ChessBoard board, ChessPiece piece, int x, int y, int originalX, int originalY) {
		this.x = originalX;
		this.y = originalY;
		board.setPiece(piece, x, y);
		board.setPiece(this, this.x, this.y);
		// Still my turn
		board.setTurn(color);
	}
	
	/*
	 * Suppose the target (x,y) satisfy all other legality check
	 * Now, will moving to this position cause king to be in check? we want to predict that, my solution involves a pseudo move
	 * board: the chess board
	 * x: destination row
	 * y: destination column
	 * */
	public boolean willMyKingDie(ChessBoard board, int x, int y) {
		int originalX = this.x, originalY = this.y;
		// Suppose the user make this move to (x,y)
		ChessPiece piece = pseudoMove(board, x, y);
		// Will the move result in my king being checked?
		boolean ret = this.isMyKingInCheck(board);
		// Recover from the move, as if it didn't happen, and return the prediction result
		cancelPseudoMove(board, piece, x, y, originalX, originalY);
		return ret;
	}

	/*
	 * Get check state of king of this piece's color
	 * return true if king of this.color is in check
	 * */
	public boolean isMyKingInCheck(ChessBoard board) {
		King myKing = this.color ? board.getWhiteKing() : board.getBlackKing(); 
		for(int row = 0; row < board.BOARD_HEIGHT; row++) {
			for(int column = 0; column < board.BOARD_WIDTH; column++) {
				// Can my king be captured by any of the opponent's pieces ?
				if(board.getPiece(row, column) != null) {
					if(board.getPiece(row, column).getColor() != this.color) {
						if(board.getPiece(row, column).isLegalMove(board, myKing.getX(), myKing.getY(), true, false)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	/*
	 * Check if this piece has any legal move
	 * return true if it has legal move
	 * */
	public boolean pieceHasLegalMove(ChessBoard chessBoard) {
		for (int row = 0; row < ChessBoard.BOARD_HEIGHT; row++) {
			for (int column = 0; column < ChessBoard.BOARD_WIDTH; column++) {
				if (isLegalMove(chessBoard, row, column, false, true))
					return true;
			}
		}
		return false;
	}
}
