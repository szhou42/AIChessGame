package ChessInterfacePackage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ChessPiecePackage.*;
import java.util.*;

public class TilePanel extends JButton implements MouseListener, MouseMotionListener {
	
	public static final int  TILE_WIDTH = 50;
	public static final int  TILE_HEIGHT = 50;
	// Tile positions
	int x, y;
	// Tile default Color
	Color color;

	// Current chess piece on the tile
	ChessPiece currPiece;

	// Board reference
	ChessBoardPanel boardPanel;
	/*
	 * Constructor for a tile
	 * x: row
	 * y: column
	 * piece: initial piece on the tile
	 * */
	public TilePanel(int x, int y, ChessBoardPanel boardPanel, ChessPiece piece) {
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setTransferHandler(new TransferHandler("icon"));
		
		this.x = x;
		this.y = y;
		this.currPiece = piece;
		this.boardPanel = boardPanel;
		
		// Set not transparent
        this.setOpaque(true);
        // Don't paint the border
        this.setBorderPainted(false);
        
		// Initialize tile color according to positions
        color = computeTileColor();
		this.setBackground(color);
		
		// Construct the filename for initial chess piece, empty tile doesn't need an image
		if(piece != null)			
			this.setIcon(boardPanel.computeIconFromChessPiece(piece));
		else
			this.setIcon(null);
		
	}
	
	
	
	/*
	 * Take over all mouse events in here
	 * Piece movement design:
	 * When a piece is pressed, its color turns grey
	 * All entered pieces should turn red
	 * When mouse is released, all coloring effects disappear
	 * */
	
	@Override
	public void mousePressed(MouseEvent e) {
        TilePanel tile = getTileFromEvent(e);
        tile.setBackground(new Color(222,184,135));
        boardPanel.addColoredTile(tile);
        boardPanel.setCurrPressedTile(tile);
    }
	
    @Override
    public void mouseDragged(MouseEvent e) {
    	boardPanel.setDragging(true);
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {

    }
    @Override
    public void mouseEntered(MouseEvent e) {
    	// Change piece color only when the position conforms the rule
    	TilePanel tile = getTileFromEvent(e);
    	if(boardPanel.isDragging()) {
	    	ChessPiece currPressedPiece = boardPanel.getCurrPressedTile().currPiece;
	    	
	    	if(currPressedPiece != null && 
	    	   currPressedPiece.isLegalMove(boardPanel.getBoard(), tile.x, tile.y, false, true) && 
	    	   (boardPanel.gamePanel.getNetworkController().getmode() == false || boardPanel.getBoard().getTurn() == boardPanel.gamePanel.getNetworkController().getPlayer())) {
	    		// Do highlighting
	    		tile.setBackground(ChessGamePanel.lightGreen);
	    		boardPanel.addColoredTile(tile);	
	    	}
    	}
        boardPanel.setLastEnteredTile(tile);
    }
    @Override
    public void mouseExited(MouseEvent e) {
    	TilePanel tile = getTileFromEvent(e);
    	if(boardPanel.isDragging()) {
    		if(boardPanel.getCurrPressedTile().currPiece != tile.currPiece)
    			tile.setBackground(color);
    	}
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    	TilePanel lastEnteredTile = boardPanel.getLastEnteredTile();
    	TilePanel tile = getTileFromEvent(e);
    	if(boardPanel.isDragging()) {
	    	ChessPiece currPressedPiece = tile.currPiece;
	    	if(currPressedPiece != null && 
	    	   currPressedPiece.isLegalMove(boardPanel.getBoard(), lastEnteredTile.x, lastEnteredTile.y, false, true) &&
	    	   (boardPanel.gamePanel.getNetworkController().getmode() == false || boardPanel.getBoard().getTurn() == boardPanel.gamePanel.getNetworkController().getPlayer())) {
	    		// Do move here
	    		boardPanel.MovePiece(tile.x, tile.y, lastEnteredTile.x, lastEnteredTile.y, false);
	    	}
    	}
    	
    	boardPanel.setDragging(false);
    	boardPanel.resetColoredTile();
    	boardPanel.displayVisualFeedBack();
    	
    	if(boardPanel.getBoard().getTurn() == false) {
    		 new Thread() {
    		        @Override
    		        public void run() {
    		            try {
    		                Thread.sleep(1000);
    		                boardPanel.AIMove();
    		            } catch (InterruptedException ex) {
    		                Thread.currentThread().interrupt();
    		            }
    		        }
    		    }.start();	
    	}
    		
    }
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	
	public Color getColor() {
		return color;
	}
	
	/*
	 * Some helper function here
	 * */
	
	private TilePanel getTileFromEvent(MouseEvent e) {
		Object obj = e.getSource();
		if(obj instanceof TilePanel) {
			return (TilePanel)obj;
		}
		return null;
	}
	
	private Color computeTileColor() {
		if( (x % 2 == 0 && y % 2 == 0) || (x % 2 != 0 && y % 2 != 0))
			return ChessGamePanel.lightColor;
		else
			return ChessGamePanel.deepColor;
	}
	

}
