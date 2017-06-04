package ChessBoardPackage;
import java.util.*;
import ChessPiecePackage.*;
import ChessPlayerPackage.ChessPlayer;

/**
 * The ChessBoard class, it manages the chess board using a simple 2d-array that stores references to all the chess pieces on the board
 * It's responsible for providing interface to manipulating the board, and checking game ending condition
 * */

public class ChessBoard {
	/**
	 * moveRecord, used to record all move operations(for undo)
	 * */
	public class MoveRecord {
		
		private int srcX;
		private int srcY;
		private int dstX;
		private int dstY;
		ChessPiece capturedPiece;
		
		public int getSrcX() {
			return srcX;
		}

		public int getSrcY() {
			return srcY;
		}

		public int getDstX() {
			return dstX;
		}


		public int getDstY() {
			return dstY;
		}
		
		public ChessPiece getCapturedPiece() {
			return capturedPiece;
		}
		


		public MoveRecord(int srcX, int srcY, int dstX, int dstY, ChessPiece piece) {
			this.srcX = srcX;
			this.srcY = srcY;
			this.dstX = dstX;
			this.dstY = dstY;
			this.capturedPiece = piece;
		}
	}
	public static final int BOARD_WIDTH = 8;
	public static final int BOARD_HEIGHT = 8;
	public static final String BLACK_KING = "BK";
	public static final String BLACK_QUEEN = "BQ";
	public static final String BLACK_ROOK = "BR";
	public static final String BLACK_BISHOP = "BB";
	public static final String BLACK_KNIGHT = "BH";
	public static final String BLACK_PAWN = "BP";
	public static final String BLACK_CRAB = "BC";
	public static final String BLACK_RABBIT = "BA";

	public static final String WHITE_KING = "WK";
	public static final String WHITE_QUEEN = "WQ";
	public static final String WHITE_ROOK = "WR";
	public static final String WHITE_BISHOP = "WB";
	public static final String WHITE_KNIGHT = "WH";
	public static final String WHITE_PAWN = "WP";
	public static final String WHITE_CRAB = "WC";
	public static final String WHITE_RABBIT = "WA";	
	
	public static final int STATE_ERROR = 0;
	public static final int STATE_PLAYING = 1;	
	public static final int STATE_ENDING = 2;	

	private static final String[][] standardBoard = 
			{ 
			{ "BR", "BH", "BB", "BQ", "BK", "BB", "BH", "BR" },
			{ "BP", "BP", "BP", "BP", "BP", "BP", "BP", "BP" }, 
			{ "NN", "NN", "NN", "NN", "NN", "NN", "NN", "NN" },
			{ "NN", "NN", "NN", "NN", "NN", "NN", "NN", "NN" }, 
			{ "NN", "NN", "NN", "NN", "NN", "NN", "NN", "NN" },
			{ "NN", "NN", "NN", "NN", "NN", "NN", "NN", "NN" }, 
			{ "WP", "WP", "WP", "WP", "WP", "WP", "WP", "WP" },
			{ "WR", "WH", "WB", "WQ", "WK", "WB", "WH", "WR" },
			};
	
	
	private ChessPiece[][] board;
	private boolean turn;

	// Save a reference to the kings, to check game ending condition
	private King whiteKing, blackKing;
	boolean bCheckmate;
	boolean bStalemate;
	
	// State (error, playing, ending... etc)
	private int state;
	
	// Move record stack
	Stack<MoveRecord> moveRecord;

	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * Get current chess board state, is it in checkmate ?
	 * return val: checkmate state
	 * */
	public boolean getBCheckmate() {
		return bCheckmate;
	}

	/**
	 * Get current chess board state, is it in stalemate ?
	 * return val: stalemate state
	 * */
	public boolean getBStalemate() {
		return bStalemate;
	}

	/**
	 * Get a reference to the black king
	 * return val: black king reference
	 * */
	public King getBlackKing() {
		return blackKing;
	}
	
	/**
	 * Get a reference to the white king
	 * return val: white king reference
	 * */
	public King getWhiteKing() {
		return whiteKing;
	}
	
	/**
	 * Get current turn
	 * true: white player's turn
	 * false: black player's turn
	 * */
	public boolean getTurn() {
		return turn;
	}
	
	/**
	 * Set current turn
	 * true: white player's turn
	 * false: black player's turn
	 * */
	public void setTurn(boolean turn) {
		this.turn = turn;
	}
	
	/**
	 * Chess board constructor, initialize the board with the standard chess board
	 * */
	public ChessBoard() {
		init_game(standardBoard, ChessPlayer.WHITE);
	}
	

	/**
	 * Is the board passed in has valid size
	 * board: a 2d string array representing a chess board
	 * return val: true if the board is valid
	 * */
	public  boolean isBoardValid(String[][] board) {
		if(board == null || board.length != BOARD_HEIGHT || board[0].length != BOARD_WIDTH) {
			//System.out.println("Please supply a board of appropriate height and width :) ");
			state = STATE_ERROR;
			return false;
		}
		return true;
	}
	/**
	 * Chess board constructor, initialize the board with customized board
	 * board: a 2d string array representing a chess board
	 * */
	public ChessBoard(String[][] board) {
		if(!isBoardValid(board))
			return;
		init_game(board, ChessPlayer.WHITE);
	}

	/**
	 * Chess board constructor, initialize the board with customized board and initial turn
	 * board: a 2d string array representing a chess board
	 * turn : who's first to go
	 * */
	public ChessBoard(String[][] board, boolean turn) {
		if(!isBoardValid(board))
			return;
		init_game(board, turn);
	}
	
	public ChessBoard(ChessBoard chessboard) {
		int identifier = 0;
		board = new ChessPiece[BOARD_HEIGHT][BOARD_WIDTH];
		moveRecord = new Stack<MoveRecord>();
		this.turn = chessboard.turn;
		// Place chess pieces in the right places
		// 8 Pawns on each side
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			for (int column = 0; column < BOARD_WIDTH; column++) {
				if (chessboard.board[row][column] == null)
					board[row][column] = null;
				else {
					String classname = chessboard.board[row][column].getClass().getSimpleName();
					boolean color = chessboard.board[row][column].getColor();
					if(classname.equals("Bishop"))
						board[row][column] = new Bishop(color, row, column, identifier);
					else if(classname.equals("Crab"))
						board[row][column] = new Crab(color, row, column, identifier);
					else if(classname.equals("Knight"))
						board[row][column] = new Knight(color, row, column, identifier);
					else if(classname.equals("Pawn"))
						board[row][column] = new Pawn(color, row, column, identifier);
					else if(classname.equals("Queen"))
						board[row][column] = new Queen(color, row, column, identifier);
					else if(classname.equals("Rabbit"))
						board[row][column] = new Rabbit(color, row, column, identifier);
					else if(classname.equals("Rook"))
						board[row][column] = new Rook(color, row, column, identifier);
					else {
						// King
						board[row][column] = new King(color, row, column, identifier);
						if(color == ChessPiece.WHITE)
							whiteKing = (King) board[row][column];
						else
							blackKing = (King) board[row][column];
					}
				}
			}
		}
		state = STATE_PLAYING;
	}

	/**
	 * Getter for a piece on chess board
	 * x: row
	 * y: column
	 * return val: the chess piece on (x,y)
	 * */
	public ChessPiece getPiece(int x, int y) {
		if (this.isInChessBoard(x, y))
			return board[x][y];
		return null;
	}
	/**
	 * Setter for a piece on chess board
	 * x: row
	 * y: column
	 * return val: the chess piece on (x,y)
	 * */
	public void setPiece(ChessPiece piece, int x, int y) {
		board[x][y] = piece;
	}
	
	/**
	 * Convenient function for moving a particular piece on board
	 * dstX: destination row
	 * dstY: destination column
	 * srcX: source row
	 * srcY: source column
	 * */
	public boolean movePiece(int srcX, int srcY, int dstX, int dstY) {
		ChessPiece srcPiece = getPiece(srcX, srcY);
		ChessPiece dstPiece = getPiece(dstX, dstY);
		if(srcPiece.move(this, dstX, dstY)) {
			moveRecord.push(new MoveRecord(srcX, srcY, dstX, dstY, dstPiece));
			return true;
		}
		return false;
	}
	
	/**
	 * Undo the last move, return current turn to the player who does the undo operation
	 * */
	public boolean undo() {
		if(moveRecord.isEmpty()) return false;
		MoveRecord record = moveRecord.pop();
		
		ChessPiece srcPiece = getPiece(record.dstX, record.dstY);
		srcPiece.setX(record.srcX);
		srcPiece.setY(record.srcY);
		
		// Move whatever that's on (dstX, dstY) back to (srcX, srcY)
		setPiece(srcPiece, srcPiece.getX(), srcPiece.getY());
		// Restore the original piece to (dstX, dstY)
		setPiece(record.capturedPiece, record.dstX, record.dstY);
		// Change position back
		
		// Flip turn
		turn = !turn;
		return true;
	}

	/*
	 * Convenient function for initializing a board with a string matrix
	 * myBoard: a 2d string array representing a chess board
	 * turn : who's first to go
	 */
	private void init_game(String myBoard[][], boolean turn) {
		int identifier = 0;
		board = new ChessPiece[BOARD_HEIGHT][BOARD_WIDTH];
		moveRecord = new Stack<MoveRecord>();
		// White player goes first
		this.turn = turn;
		// Place chess pieces in the right places
		// 8 Pawns on each side
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			for (int column = 0; column < BOARD_WIDTH; column++) {
				if (myBoard[row][column] == "NN")
					board[row][column] = null;
				// White
				else if (myBoard[row][column] == WHITE_KING) {
					board[row][column] = new King(ChessPiece.WHITE, row, column, identifier);
					whiteKing = (King) board[row][column];
				} else if (myBoard[row][column] == WHITE_QUEEN)
					board[row][column] = new Queen(ChessPiece.WHITE, row, column, identifier);
				else if (myBoard[row][column] == WHITE_ROOK)
					board[row][column] = new Rook(ChessPiece.WHITE, row, column, identifier);
				else if (myBoard[row][column] == WHITE_BISHOP)
					board[row][column] = new Bishop(ChessPiece.WHITE, row, column, identifier);
				else if (myBoard[row][column] == WHITE_KNIGHT)
					board[row][column] = new Knight(ChessPiece.WHITE, row, column, identifier);
				else if (myBoard[row][column] == WHITE_PAWN)
					board[row][column] = new Pawn(ChessPiece.WHITE, row, column, identifier);
				else if (myBoard[row][column] == WHITE_CRAB)
					board[row][column] = new Crab(ChessPiece.WHITE, row, column, identifier);
				else if (myBoard[row][column] == WHITE_RABBIT)
					board[row][column] = new Rabbit(ChessPiece.WHITE, row, column, identifier);
				// Black
				else if (myBoard[row][column] == BLACK_KING) {
					board[row][column] = new King(ChessPiece.BLACK, row, column, identifier);
					blackKing = (King) board[row][column];
				} else if (myBoard[row][column] == BLACK_QUEEN)
					board[row][column] = new Queen(ChessPiece.BLACK, row, column, identifier);
				else if (myBoard[row][column] == BLACK_ROOK)
					board[row][column] = new Rook(ChessPiece.BLACK, row, column, identifier);
				else if (myBoard[row][column] == BLACK_BISHOP)
					board[row][column] = new Bishop(ChessPiece.BLACK, row, column, identifier);
				else if (myBoard[row][column] == BLACK_KNIGHT)
					board[row][column] = new Knight(ChessPiece.BLACK, row, column, identifier);
				else if (myBoard[row][column] == BLACK_PAWN)
					board[row][column] = new Pawn(ChessPiece.BLACK, row, column, identifier);
				else if (myBoard[row][column] == BLACK_CRAB)
					board[row][column] = new Crab(ChessPiece.BLACK, row, column, identifier);
				else if (myBoard[row][column] == BLACK_RABBIT)
					board[row][column] = new Rabbit(ChessPiece.BLACK, row, column, identifier);
			}
		}
		state = STATE_PLAYING;
	}

	/**
	 * Check if (x,y) is on/off the board
	 * return val: true if the position is on the board
	 * */
	public boolean isInChessBoard(int x, int y) {
		if (x < 0 || x >= this.BOARD_WIDTH)
			return false;
		if (y < 0 || y >= this.BOARD_HEIGHT)
			return false;
		return true;
	}

	/**
	 * If either there is a checkmate or stalemate, the game is over
	 * return val: true if game needs to end
	 */
	public boolean isGameOver() {
		bCheckmate = isInCheckmate();
		bStalemate = isInStalemate();
		return bCheckmate || bStalemate;
	}

	/*
	 * Check every single chess piece of the current turn, does anyone of them has legal move ?
	 * return val: true if any pieces have legal move
	 * */
	public boolean hasLegalMove() {
		boolean currentColor = this.turn;
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			for (int column = 0; column < BOARD_WIDTH; column++) {
				// Can my king be captured by any of the opponent's pieces ?
				if (getPiece(row, column) != null) {
					if (getPiece(row, column).getColor() == currentColor) {
						if(getPiece(row, column).pieceHasLegalMove(this))
							return true;
						
					}
				}
			}
		}
		return false;
	}
	
	/**
	 *  Get the piece that's checking a king
	 * */
	public ChessPiece getCheckingPiece(boolean turn) {
		King myKing = turn ? this.getWhiteKing() : this.getBlackKing(); 
		for(int row = 0; row < BOARD_HEIGHT; row++) {
			for(int column = 0; column < BOARD_WIDTH; column++) {
				// Can my king be captured by any of the opponent's pieces ?
				if(getPiece(row, column) != null) {
					if(getPiece(row, column).getColor() != turn) {
						if(getPiece(row, column).isLegalMove(this, myKing.getX(), myKing.getY(), true, false)) {
							return getPiece(row, column);
						}
					}
				}
			}
		}
		return null;
	}

	/*
	 * Is some king in checkmate ? If it's black's turn to go, we only care
	 * about whether black king is in checkmate, it's no way in this case that
	 * white king would be in check, or checkmate, this is not allowed, and vice
	 * versa.
	 * 
	 * return true if it's in check mate
	 */
	public boolean isInCheckmate() {
		if (turn == ChessPlayer.WHITE)
			return whiteKing.isMyKingInCheck(this) && !hasLegalMove();
		return blackKing.isMyKingInCheck(this) && !hasLegalMove();
	}
	
	/*
	 * Is in stalemate ?
	 * 
	 * return true if in stalemate
	 * */
	public boolean isInStalemate() {
		if (turn == ChessPlayer.WHITE)
			return !whiteKing.isMyKingInCheck(this) && !hasLegalMove();
		return !blackKing.isMyKingInCheck(this) && !hasLegalMove();
	}

	/*
	 * Is some path a vertical line
	 * dstX: destination row
	 * dstY: destination column
	 * srcX: source row
	 * srcY: source column
	 * return val: true if path is vertical
	 * */
	public static boolean isVertical(int srcX, int srcY, int dstX, int dstY) {
		return (dstX - srcX != 0 && dstY - srcY == 0);
	}

	/*
	 * Is some path a horizontal line
	 * dstX: destination row
	 * dstY: destination column
	 * srcX: source row
	 * srcY: source column
	 * return val: true if path is horizontal
	 * */
	public static boolean isHorizontal(int srcX, int srcY, int dstX, int dstY) {
		return (dstX - srcX == 0 && dstY - srcY != 0);
	}

	/*
	 * Is some path a diagonal line
	 * dstX: destination row
	 * dstY: destination column
	 * srcX: source row
	 * srcY: source column
	 * return val: true if path is diagonal
	 * */
	public static boolean isDiagonal(int srcX, int srcY, int dstX, int dstY) {
		return ((Math.abs(dstX - srcX) == Math.abs(dstY - srcY)) && (dstX - srcX != 0));
	}

	/*
	 * Check if some path is blocked(The path needs to be either horizontal,
	 * vertical, or diagonal) This serves as a helper function to canMove() to
	 * reduce code duplicate
	 * board: the chess board
	 * dstX: destination row
	 * dstY: destination column
	 * srcX: source row
	 * srcY: source column
	 * return true if path is blocked
	 */
	public static boolean isPathBlocked(ChessBoard board, int srcX, int srcY, int dstX, int dstY) {
		if (ChessBoard.isVertical(srcX, srcY, dstX, dstY) || ChessBoard.isHorizontal(srcX, srcY, dstX, dstY) 
				|| ChessBoard.isDiagonal(srcX, srcY, dstX, dstY)) {
			int directionX = 0, directionY = 0;
			int xDiff = dstX - srcX, yDiff = dstY - srcY;
			if (xDiff != 0)
				directionX = (xDiff) / Math.abs(xDiff);
			if (yDiff != 0)
				directionY = (yDiff) / Math.abs(yDiff);
		
			int currentX = srcX + directionX, currentY = srcY + directionY;
			while (currentX != dstX || currentY != dstY) {
				if (board.getPiece(currentX, currentY) != null)
					return true;
				currentX = currentX + directionX;
				currentY = currentY + directionY;
			}
		}
		return false;
	}
	
	public static boolean isPointInPath(int srcX, int srcY, int dstX, int dstY, int pointX, int pointY) {
		if (ChessBoard.isVertical(srcX, srcY, dstX, dstY) || ChessBoard.isHorizontal(srcX, srcY, dstX, dstY) 
				|| ChessBoard.isDiagonal(srcX, srcY, dstX, dstY)) {
			int directionX = 0, directionY = 0;
			int xDiff = dstX - srcX, yDiff = dstY - srcY;
			if (xDiff != 0)
				directionX = (xDiff) / Math.abs(xDiff);
			if (yDiff != 0)
				directionY = (yDiff) / Math.abs(yDiff);
		
			int currentX = srcX + directionX, currentY = srcY + directionY;
			while (currentX != dstX || currentY != dstY) {
				if (currentX == pointX && currentY == pointY)
					return true;
				currentX = currentX + directionX;
				currentY = currentY + directionY;
			}
		}
		
		return (srcX == pointX && srcY == pointY) || (dstX == pointX && dstY == pointY);
	}
	
	public MoveRecord peekMoveRecord() {
		if(!moveRecord.isEmpty())
			return moveRecord.peek();
		return null;
	}
	
	/*
	 * If king is in check, return a path
	 * */
	public ArrayList<Integer> getCheckPath() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		King king = (turn == ChessPlayer.WHITE) ? whiteKing : blackKing;
		
		if (king.isMyKingInCheck(this)) {
			ChessPiece piece = getCheckingPiece(turn);
			list.add(piece.getX());
			list.add(piece.getY());
			list.add(king.getX());
			list.add(king.getY());
			return list;
		}
		return null;
	}
	
	/*
	 * Return king(which is in stalemate now) position
	 * */
	public ArrayList<Integer> getTrappedKing() {
		King king = (turn == ChessPlayer.WHITE) ? whiteKing : blackKing; 
		ArrayList<Integer> list = new ArrayList<Integer>();
		if(!king.isMyKingInCheck(this) && !hasLegalMove()) {
			list.add(king.getX());
			list.add(king.getY());
			return list;
		}
		return null;
	}
	
	// The following functions are for stats
	
	public int remainingBlackPieces() {
		int ret = 0;
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			for (int column = 0; column < BOARD_WIDTH; column++) {
				ChessPiece piece = getPiece(row, column);
				if(piece == null) continue;
				if(piece.getColor() == ChessPiece.BLACK)
					ret++;
			}
		}
		return ret;
	}
	
	public int remainingWhitePieces() {
		int ret = 0;
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			for (int column = 0; column < BOARD_WIDTH; column++) {
				ChessPiece piece = getPiece(row, column);
				if(piece == null) continue;
				if(piece.getColor() == ChessPiece.WHITE)
					ret++;
			}
		}
		return ret;
	}
	
	
	@Override
	public String toString() {
		String ret = "";
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			for (int column = 0; column < BOARD_WIDTH; column++) {
				ChessPiece piece = getPiece(row, column);
				if(piece == null) { 
					ret = ret + "  \t";
					continue;	
				}
				String color = piece.getColor()?"红":"黑";
				String classname = color + piece.getClass().getSimpleName();
				ret = ret + classname + "\t";
			}
			ret = ret + "\n";
		}
		ret = ret + "turn: " + (turn? "white":"black") + "\n";
		return ret;
	}
}
