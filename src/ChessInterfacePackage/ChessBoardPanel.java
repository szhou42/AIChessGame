package ChessInterfacePackage;
import ChessPlayerPackage.ChessPlayer;

import ChessBoardPackage.ChessBoard;
import ChessBoardPackage.ChessBoard.MoveRecord;
import ChessNetworkPackage.ChessNetwork;
import ChessPiecePackage.ChessPiece;
import ChessPlayerPackage.ChessPlayer;
import AIPlayer.Minimax;
import AIPlayer.Movement;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
/*
 * Chess Board window, it initializes and displays height * width cells/tiles.
 * */
public class ChessBoardPanel extends JPanel {
	
	public static final int GRID_SIZE = 75;
	
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
	private static final String[][] customBoard = 
		{ 
		{ "BR", "BH", "BB", "BQ", "BK", "BB", "BH", "BR" },
		{ "BC", "BP", "BP", "BA", "BA", "BP", "BP", "BP" }, 
		{ "NN", "NN", "NN", "NN", "NN", "NN", "NN", "NN" },
		{ "NN", "NN", "NN", "NN", "NN", "NN", "NN", "NN" }, 
		{ "NN", "NN", "NN", "NN", "NN", "NN", "NN", "NN" },
		{ "NN", "NN", "NN", "NN", "NN", "NN", "NN", "NN" }, 
		{ "WC", "WP", "WP", "WA", "WA", "WP", "WP", "WC" },
		{ "WR", "WH", "WB", "WQ", "WK", "WB", "WH", "WR" },
		};
	

	
	// 2d array storing references to each tile
	private TilePanel[][] tiles;

	// The chess board
	private ChessBoard board;

	// Current pressed piece
	private TilePanel currPressedTile;

	// The tile entered most recently
	private TilePanel lastEnteredTile;

	// Drag state, is mouse currently dragging something?
	private boolean dragging;
	
	// Array list for recording all colored tiles
	ArrayList<TilePanel> coloredTiles;
	

	
    // The game panel
    ChessGamePanel gamePanel;
	/*
	 * Create tiles and add them to ChessBoardPanel
	 * board: the chess board with initialized pieces on it
	 * */
	public ChessBoardPanel(ChessGamePanel gamePanel) {
		super(new GridLayout(ChessBoard.BOARD_WIDTH, ChessBoard.BOARD_HEIGHT));
		setPreferredSize(new Dimension(ChessBoard.BOARD_WIDTH * GRID_SIZE, ChessBoard.BOARD_WIDTH * GRID_SIZE));
		this.gamePanel = gamePanel;
		
		
		setupChessData(true);
		setupChessInterface();
	}
	
	private void setupChessData(boolean gameType) {

		// Setup chess board
		String[][] whichBoard = gameType ? standardBoard : customBoard;
		board = new ChessBoard(whichBoard, ChessPlayer.BLACK);
		board.setState(ChessBoard.STATE_PLAYING);
		
		// Setup tiles linked list(for highlighting a series of tiles)
		coloredTiles = new ArrayList<TilePanel>();

		// Create a board of tiles
		tiles = new TilePanel[ChessBoard.BOARD_HEIGHT][ChessBoard.BOARD_HEIGHT];
		for(int row = 0; row < ChessBoard.BOARD_HEIGHT; row++) {
			for(int column = 0; column < ChessBoard.BOARD_HEIGHT; column++) { 
				tiles[row][column] = new TilePanel(row, column, this, board.getPiece(row, column));
				
			}
		}
	}
	
	private void setupChessInterface() {
		this.setBackground(ChessGamePanel.gameBGColor);
		addAllPieceButton();
	}

	private void addAllPieceButton() {
		for(int row = 0; row < ChessBoard.BOARD_HEIGHT; row++) {
			for(int column = 0; column < ChessBoard.BOARD_HEIGHT; column++) { 
				add(tiles[row][column]);
			}
		}
	}

	/*
	 * Move a piece from (srcX, srcY) to (dstX, dstY)
	 * If the caller of this function is the local GUI, it will send the move operation to the other player
	 * If the caller comes from the network controller, it only update the GUI and the models
	 * */
    public void MovePiece(int srcX, int srcY, int dstX, int dstY, boolean fromNetwork) {
    	TilePanel srcTile = tiles[srcX][srcY];
    	TilePanel dstTile = tiles[dstX][dstY];
    	
		dstTile.setIcon(srcTile.getIcon());
		dstTile.currPiece = srcTile.currPiece;
		
		srcTile.setIcon(null);
		srcTile.currPiece = null;
		
		if(board.movePiece(srcX, srcY, dstX, dstY)) {
			if(fromNetwork) {
				displayVisualFeedBack();
			}
		}
		if(!fromNetwork && gamePanel.getNetworkController().getmode() == ChessNetwork.MODE_NETWORK) {
			gamePanel.getNetworkController().networkSendMove(srcX, srcY, dstX, dstY);
		}
    }

    public void AIMove() {
    	Minimax m = new Minimax();
    	Movement movement = m.minimax_move(board);
    	if(movement == null) return;
    	System.out.println(movement);
    	MovePiece(movement.srcX, movement.srcY, movement.dstX, movement.dstY, false);
    }
    
    public void undo(boolean fromNetwork) {

    	// If current game is over, you cannot undo anymore
    	if(board.getState() == ChessBoard.STATE_ENDING) return;
    	// Undo GUI (You should only undo, when it's not your turn)
    	MoveRecord record = board.peekMoveRecord();
    	if(record != null) {
    		ChessPiece capturedPiece = record.getCapturedPiece();
    		ChessPiece srcPiece = tiles[record.getDstX()][record.getDstY()].currPiece;
    		
    		Icon capturedIcon = computeIconFromChessPiece(capturedPiece);
    		Icon sourceIcon = computeIconFromChessPiece(srcPiece);
    		
    		tiles[record.getDstX()][record.getDstY()].currPiece = capturedPiece;
    		tiles[record.getSrcX()][record.getSrcY()].currPiece = srcPiece;
    		
    		tiles[record.getDstX()][record.getDstY()].setIcon(capturedIcon);
    		tiles[record.getSrcX()][record.getSrcY()].setIcon(sourceIcon);
    		
    		// Update the back-end too
    		board.undo();
    		
    		this.resetColoredTile();
    		displayVisualFeedBack();
    		if(!fromNetwork && gamePanel.getNetworkController().getmode() == ChessNetwork.MODE_NETWORK) {
    			gamePanel.getNetworkController().networkSendUndo();;
    		}
    	}
    }
    
    
    public void restart(boolean fromNetwork) {
    	// Clear all the player stats here
    	gamePanel.redPlayer.clearStats();
		gamePanel.blackPlayer.clearStats();
    	newClassicGame(true);
    	
		if(!fromNetwork && gamePanel.getNetworkController().getmode() == ChessNetwork.MODE_NETWORK) {
			gamePanel.getNetworkController().networkSendStartOver();
		}
    }
    
    public void newClassicGame(boolean fromNetwork) {
    	clearAllTiles();
		this.removeAll();
		//this.revalidate();		
		setupChessData(true);
    	addAllPieceButton();
    	this.validate();
    	this.repaint();
    	displayVisualFeedBack();
    	
		if(!fromNetwork && gamePanel.getNetworkController().getmode() == ChessNetwork.MODE_NETWORK) {
			gamePanel.getNetworkController().networkSendNewGame1();
		}
    }
    
    public void newCustomGame(boolean fromNetwork) {
    	
    	clearAllTiles();
		this.removeAll();
		//this.revalidate();		
		setupChessData(false);
    	addAllPieceButton();
    	this.validate();
    	this.repaint();
    	displayVisualFeedBack();
    	
		if(!fromNetwork && gamePanel.getNetworkController().getmode() == ChessNetwork.MODE_NETWORK) {
			gamePanel.getNetworkController().networkSendNewGame2();
		}
    } 
   
    public void forfeit(String whichPlayer, boolean fromNetwork) {
    	if(whichPlayer.equals(gamePanel.redPlayer.getName())) {
    		gamePanel.blackPlayer.incrementScore();
    	}
    	else {
    		gamePanel.redPlayer.incrementScore();
    	}
    	gamePanel.getBoardPanel().newClassicGame(fromNetwork);
		if(!fromNetwork && gamePanel.getNetworkController().getmode() == ChessNetwork.MODE_NETWORK) {
			gamePanel.getNetworkController().networkSendSurrender(whichPlayer);
		}
    }

    public void tie(String whichPlayer, boolean fromNetwork) {
    	// Only ask if whichPlayer is opposite to network player
    	if((whichPlayer.equals("Mr Red") && gamePanel.getNetworkController().getPlayer()) ||
        		(whichPlayer.equals("Mr Black") && !gamePanel.getNetworkController().getPlayer())) {
    		//gamePanel.getBoardPanel().newClassicGame(fromNetwork);
    		// Change app title to "Tie request sent, waiting..."
    		gamePanel.f.setTitle("Tie request sent, waiting...");
    	}
    	else {
    		int opcion = JOptionPane.showConfirmDialog(null, whichPlayer + " requests a new game, would you agree ?", "New Game", JOptionPane.YES_NO_OPTION);
    		if(opcion == 0) {
    			gamePanel.getNetworkController().networkSendAgreeTie();
    			gamePanel.getBoardPanel().newClassicGame(fromNetwork);
    		}
    	}
    	
		if(!fromNetwork && gamePanel.getNetworkController().getmode() == ChessNetwork.MODE_NETWORK) {
			gamePanel.getNetworkController().networkSendTie(whichPlayer);;
		}
    }
    
    /*
     * Network controller will call this function when the other player agrees to start a new game
     * */
    public void tieAgree() {
    	// Change app title to "Chess Game"
    	gamePanel.f.setTitle("Request was approved, starting a new game...");
    	try {
			Thread.sleep(2300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	gamePanel.f.setTitle("Chess Game");
    	gamePanel.getBoardPanel().newClassicGame(true);
    }
    
    public void clearAllTiles() {
		for(int row = 0; row < ChessBoard.BOARD_HEIGHT; row++) {
			for(int column = 0; column < ChessBoard.BOARD_HEIGHT; column++) { 
				if(tiles[row][column] != null)
					tiles[row][column].setIcon(null);
			}
		}
    }
    
    public void Quit(boolean fromNetwork) {
		if(!fromNetwork && gamePanel.getNetworkController().getmode() == ChessNetwork.MODE_NETWORK) {
			gamePanel.getNetworkController().networkSendQuit();
		}
    	// Don't need to clean up anything, just quit!
    	System.exit(0);
    }
    
    
	public Icon computeIconFromChessPiece(ChessPiece piece) {
		if(piece != null) {
			String className = piece.getClass().getSimpleName().toLowerCase();
			String filename = "/" + className + "_" + (piece.getColor() ? "yellow":"black") + ".png";
			
			Image img = new ImageIcon(this.getClass().getResource(filename)).getImage();
			return new ImageIcon(img);
		}
		return null;
	}
	
	
	public void displayVisualFeedBack() {
		if(board.getState() == ChessBoard.STATE_ENDING) return;
		// If after this move, some king is in danger, mark the check path in red color
		ArrayList<Integer> list  = board.getCheckPath();
		if(list != null) {
			if(board.isInCheckmate()) {
				this.highLightPath(list.get(0), list.get(1), list.get(2), list.get(3), ChessGamePanel.lightRed);
			}
			else {
				this.highLightPath(list.get(0), list.get(1), list.get(2), list.get(3), ChessGamePanel.lightYellow);
			}
			if(board.getTurn())
				gamePanel.blackPlayer.incrementScore();
			else
				gamePanel.redPlayer.incrementScore();
			board.setState(ChessBoard.STATE_ENDING);
		}
		else if (board.isInStalemate()) {
			// Color all other tiles as gray(leave only the king as red)
			list = board.getTrappedKing();
			tiles[list.get(0)][list.get(1)].setBackground(Color.lightGray);
			board.setState(ChessBoard.STATE_ENDING);
		}
		else {
			resetColoredTile();
		}
		
		// Switch turn (graphically)
		if(board.getTurn()) {
			TitledBorder tb1 = BorderFactory.createTitledBorder(gamePanel.redPlayer.getName());
			tb1.setTitleFont(LeftBarPanel.font1);
			tb1.setTitleColor(ChessGamePanel.player1Color);
			gamePanel.getLeftBarPanel().player1.setBorder(tb1);
			
			TitledBorder tb2 = BorderFactory.createTitledBorder(gamePanel.blackPlayer.getName());
			tb2.setTitleFont(LeftBarPanel.font2);
			tb2.setTitleColor(ChessGamePanel.player2Color);
			gamePanel.getLeftBarPanel().player2.setBorder(tb2);
		}
		else {
			TitledBorder tb1 = BorderFactory.createTitledBorder(gamePanel.redPlayer.getName());
			tb1.setTitleFont(LeftBarPanel.font2);
			tb1.setTitleColor(ChessGamePanel.player1Color);
			gamePanel.getLeftBarPanel().player1.setBorder(tb1);
			
			
			TitledBorder tb2 = BorderFactory.createTitledBorder(gamePanel.blackPlayer.getName());
			tb2.setTitleFont(LeftBarPanel.font1);
			tb2.setTitleColor(ChessGamePanel.player2Color);
			gamePanel.getLeftBarPanel().player2.setBorder(tb2);
		}
		gamePanel.getLeftBarPanel().winNumLabel1.setText("score: " + gamePanel.redPlayer.getScore());
		gamePanel.getLeftBarPanel().winNumLabel2.setText("score: " + gamePanel.blackPlayer.getScore());
	}
	/*
	 * HighLight a path to indicate king in check
	 * */
	private void highLightPath(int srcX, int srcY, int dstX, int dstY, Color color) {
		// Highlight start and end square
		tiles[srcX][srcY].setBackground(color);
		tiles[dstX][dstY].setBackground(color);
		
		this.addColoredTile(tiles[srcX][srcY]);
		this.addColoredTile(tiles[dstX][dstY]);
		
		// Highlight middle squares
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
				tiles[currentX][currentY].setBackground(color);
				this.addColoredTile(tiles[currentX][currentY]);
				currentX = currentX + directionX;
				currentY = currentY + directionY;
			}
		}
		
	}
    
    /*
     * Draging getter
     * */
    public boolean isDragging() {
    	return this.dragging;
    }
    
    /*
     * Board getter
     * */
	public ChessBoard getBoard() {
		return board;
	}
    
    /*
     * Draging getter
     * */
    public void setDragging(boolean dragging) {
    	this.dragging = dragging;
    }
    
	public TilePanel getCurrPressedTile() {
		return currPressedTile;
	}

	public void setCurrPressedTile(TilePanel currPressedTile) {
		this.currPressedTile = currPressedTile;
	}
    
	public TilePanel getLastEnteredTile() {
		return lastEnteredTile;
	}

	public void setLastEnteredTile(TilePanel lastEnteredTile) {
		this.lastEnteredTile = lastEnteredTile;
	}
	
    /*
     * Add a coloredTiles
     * */
    public void addColoredTile(TilePanel tile) {
    	coloredTiles.add(tile);
    }
    /*
     * Remove all coloredTiles
     * */
    public void removeColoredTile() {
    	coloredTiles.clear();
    }
    
    public void resetColoredTile() {
    	for(TilePanel tile : coloredTiles) {
    		tile.setBackground(tile.getColor());
    	}
    	removeColoredTile();
    }


}

