package ChessInterfacePackage;
import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ChessBoardPackage.ChessBoard;
import ChessNetworkPackage.ChessNetwork;
import ChessPlayerPackage.ChessPlayer;

/*
 * The panel that represent the whole game view
 * It includes another two panel, The ChessBoardPanel and the LeftNavigationPanel
 * */
public class ChessGamePanel extends JPanel{
	
	// JFrame
	JFrame f;
	
	// The chess board grid GUI
	ChessBoardPanel boardPanel;
	
	// The left navigation bar GUI
	LeftBarPanel leftBarPanel;
	
	// Chess Network controller
	ChessNetwork networkController;
	
	// Two players
	public ChessPlayer redPlayer;
	public ChessPlayer blackPlayer;

	public static final Color player1Color = new Color(142, 75, 45);

	public static final Color player2Color = new Color(48, 58, 65);

	public static final Color lightYellow = new Color(239, 242, 150);

	public static final Color lightGreen = new Color(171, 242, 126);

	public static final Color lightRed = new Color(222, 135, 115);

	public static final Color gameBGColor = new Color(160, 115, 80);

	public static final Color lightColor = new Color(240, 218, 179);

	// Colors used:
	public static final Color deepColor = new Color(180, 135, 102);
	
	public ChessGamePanel() {
		super(new BorderLayout());
		
		// Setup player
		redPlayer = new ChessPlayer(ChessPlayer.RED);
		blackPlayer = new ChessPlayer(ChessPlayer.BLACK);
		
		// Setup two panels
		leftBarPanel = new LeftBarPanel(this);
		boardPanel = new ChessBoardPanel(this);
		
		add(boardPanel, BorderLayout.CENTER);
		add(leftBarPanel, BorderLayout.WEST);
		
		// Initialize network controller
		networkController = new ChessNetwork(this);
	}
	
	public ChessBoardPanel getBoardPanel() {
		return boardPanel;
	}
	
	public LeftBarPanel getLeftBarPanel() {
		return leftBarPanel;
	}
	
	public ChessNetwork getNetworkController() {
		return networkController;
	}
	
    /*
     * Create a new frame, append a JPanel to it and display
     * */
    public void display() {
        f = new JFrame("Chess Game");
        f.setMinimumSize(new Dimension(ChessBoard.BOARD_WIDTH * ChessBoardPanel.GRID_SIZE, ChessBoard.BOARD_WIDTH * ChessBoardPanel.GRID_SIZE));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(this);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        
        // Ask user whether he wants to play in network mode or local mdoe
    	int opcion = 1;//JOptionPane.showConfirmDialog(null, "Hi! Would you like to play in network mode(Yes) or local mode(No)", "Choose Mode", JOptionPane.YES_NO_OPTION);
    	if(opcion == 0) {
    		networkController.setmode(true);
    		//networkC
        	// Run send/receive chess operations in a separate thread
    		System.out.println("Current thread(GUI) id is " + Thread.currentThread().getId());
    		networkController.startChessThread();
    	}
    }
}
