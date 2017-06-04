package ChessNetworkPackage;
import java.io.*;
import java.net.*;
import ChessInterfacePackage.ChessGamePanel;

/*
	ChessNetwork class, it handles the network setup, message sending and receiving
	
	In GUI, if a player is going to move a chess piece(or , surrender and undo), the corresponding sub-routine would also need to ask ChessNetwork
	to send the changes made to the other player. On receiving a message in the ChessNetwork class, it should parse the message and make corresponding
	operations in local.
	
	There should be a state variable in ChessNetwork class that indicates whether it's currently playing in network mode or local mode.
	The user gets to choose to connect to another player, or play in local.
	
	A list of possible  operations
	
	Start over
	New Game I
	New Game II
	Undo
	Quit
	Surrender
	One player asks to start a new game
	Move a piece
	
	We can use a struct(class) to encode an operation
	
 * */
public class ChessNetwork implements Runnable{

	public static final boolean MODE_NETWORK = true;
	public static final boolean MODE_LOCAL = false;
	
	
	// Network mode or local mode (True for network mode)
	private boolean mode;
	private boolean is_server;
	private boolean is_connected;	
	private Socket requestSocket;
	private ServerSocket providerSocket;
	private Socket connection = null;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private ChessNetworkOperation op;
	
	Thread thread;
	// The overall game controller 
	ChessGamePanel gamePanel;
	
	
	public ChessNetwork(ChessGamePanel gamePanel) {
		// Default mode is play in local
		mode = false;
		is_server = false;
		is_connected = false;
		this.gamePanel = gamePanel;
		
		thread = new Thread(this, "Chess Network");
		
	}
	
	public void startChessThread() {
		thread.start();
	}
	
	/*
	 * Server will be the white/red(true) player, client will be the black(false) player
	 * */
	public boolean getPlayer() {
		return is_server;
	}
	
	/*
	 * Set mode
	 * True for network mode
	 * False for local mode
	 * */
	public void setmode(boolean mode) {
		this.mode = mode;
	}
	
	/*
	 * Get mode
	 * True for network mode
	 * False for local mode
	 * */	
	public boolean getmode() {
		return this.mode;
	}
	
	/*
	 * This network establish the connection, and receive information
	 * This thread will first attempt to connect a local host server at port 9999, if the attempt fails, it will make itself a server
	 * So, whichever chess game ran first will be the server
	 * Btw, let's just assume that the server is red player, and client is black player
	 * */
	public void networkThread() {
		is_server = false;
		while(true) {
			try{
				if(is_server && !is_connected) {
					// Server
					providerSocket = new ServerSocket(9999, 10);
		            connection = providerSocket.accept();
		            out = new ObjectOutputStream(connection.getOutputStream());
		            out.flush();
		            in = new ObjectInputStream(connection.getInputStream());
		            is_connected = true;
				}
				else if(!is_connected) {
					// Client
					requestSocket = new Socket("localhost", 9999);
					out = new ObjectOutputStream(requestSocket.getOutputStream());
					out.flush();
					in = new ObjectInputStream(requestSocket.getInputStream());
					is_connected = true;
				}
	            do{
	                try{
	                	System.out.println("Reading from the other player...");
	                	op = (ChessNetworkOperation)in.readObject();
	                	System.out.println("Got an operation, the type is " + op.getOperationType());
	                	recvOperation(op);
	                }
	                catch(ClassNotFoundException classnot){
	                    System.err.println("Data received in unknown format");
	                }
	            }while(op.getOperationType() != ChessNetworkOperation.OP_QUIT);
	        }
	        catch(IOException ioException){
	        	is_server = true;
	        	System.out.println("Connection refused, but I don't care, just don't crash my program");
	        	if(!is_server && is_connected)
	        		ioException.printStackTrace();
	        }
		}
	}
	
	/*
	 * I'll take care of all the operations and dispense them to sub routines that actually update the GUI and database
	 * */
	void recvOperation(ChessNetworkOperation op) {
		if(op.getOperationType() == ChessNetworkOperation.OP_STARTOVER) {
			networkRecvStartOver();
		}
		else if (op.getOperationType() == ChessNetworkOperation.OP_NEWGAME1) {
			networkRecvNewGame1();
		}
		else if (op.getOperationType() == ChessNetworkOperation.OP_NEWGAME2) {
			networkRecvNewGame2();
		}
		else if (op.getOperationType() == ChessNetworkOperation.OP_UNDO) {
			networkRecvUndo();
		}
		else if (op.getOperationType() == ChessNetworkOperation.OP_QUIT) {
			networkRecvQuit();
		}
		else if (op.getOperationType() == ChessNetworkOperation.OP_SURRENDER) {
			networkRecvSurrender(op.getForfeitPlayerName());
		}
		else if (op.getOperationType() == ChessNetworkOperation.OP_TIE) {
			newworkRecvTie(op.getTiePlayerName());
		}
		else if (op.getOperationType() == ChessNetworkOperation.OP_MOVEPIECE) {
			networkRecvMove(op.getSrcX(), op.getSrcY(), op.getDstX(), op.getDstY());
		}
		else if (op.getOperationType() == ChessNetworkOperation.OP_AGREETIE) {
			networkRecvAgreeTie();
		}
	}
	

	/*
	 * Helper function for handling received start over package 
	 * */
	public void networkRecvStartOver() {
		gamePanel.getBoardPanel().restart(true);
	}
	
	/*
	 * Helper function for handling received newgame1 package 
	 * */
	public void networkRecvNewGame1() {
		gamePanel.getBoardPanel().newClassicGame(true);
	}
	
	/*
	 * Helper function for handling received newgame2 package 
	 * */
	public void networkRecvNewGame2() {
		gamePanel.getBoardPanel().newCustomGame(true);
	}
	
	/*
	 * Helper function for handling received undo package 
	 * */
	public void networkRecvUndo() {
		gamePanel.getBoardPanel().undo(true);
	}
	
	/*
	 * Helper function for handling received quit package 
	 * */
	public void networkRecvQuit() {
		gamePanel.getBoardPanel().Quit(true);
	}
	
	/*
	 * Helper function for handling received surrender package 
	 * */
	public void networkRecvSurrender(String whichPlayer) {
		gamePanel.getBoardPanel().forfeit(whichPlayer, true);
	}
	
	/*
	 * Helper function for handling received tie package 
	 * */
	public void newworkRecvTie(String whichPlayer) {
		gamePanel.getBoardPanel().tie(whichPlayer, true);
	}
	
	public void networkRecvAgreeTie() {
		gamePanel.getBoardPanel().tieAgree();
	}
	
	/*
	 * Helper function for handling received move piece package 
	 * */
	public void networkRecvMove(int srcX, int srcY, int dstX, int dstY) {
		gamePanel.getBoardPanel().MovePiece(srcX, srcY, dstX, dstY, true);
	}
	
	/*
	 * Every one else calls me to send operation object
	 * */
	void sendOperation(Object op) {
        try{
            out.writeObject(op);
            out.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
	
	/*
	 * Helper function for packaging a startover operation object and send it
	 * */
	public void networkSendStartOver() {
		ChessNetworkOperation op = new ChessNetworkOperation(ChessNetworkOperation.OP_STARTOVER);
		sendOperation(op);
	}
	
	/*
	 * Helper function for packaging a newgame1 operation object and send it
	 * */
	public void networkSendNewGame1() {
		ChessNetworkOperation op = new ChessNetworkOperation(ChessNetworkOperation.OP_NEWGAME1);
		sendOperation(op);		
	}
	
	/*
	 * Helper function for packaging newgame2 operation object and send it
	 * */
	public void networkSendNewGame2() {
		ChessNetworkOperation op = new ChessNetworkOperation(ChessNetworkOperation.OP_NEWGAME2);
		sendOperation(op);
	}
	
	/*
	 * Helper function for packaging an undo operation object and send it
	 * */
	public void networkSendUndo() {
		ChessNetworkOperation op = new ChessNetworkOperation(ChessNetworkOperation.OP_UNDO);
		sendOperation(op);
	}
	
	/*
	 * Helper function for packaging a quit operation object and send it
	 * */
	public void networkSendQuit() {
		ChessNetworkOperation op = new ChessNetworkOperation(ChessNetworkOperation.OP_QUIT);
		sendOperation(op);
	}
	
	/*
	 * Helper function for packaging a surrender operation object and send it
	 * */
	public void networkSendSurrender(String whichPlayer) {
		ChessNetworkOperation op = new ChessNetworkOperation(ChessNetworkOperation.OP_SURRENDER);
		op.setForfeitPlayerName(whichPlayer);
		sendOperation(op);
	}
	
	/*
	 * Helper function for packaging a tie operation object and send it
	 * */
	public void networkSendTie(String whichPlayer) {
		ChessNetworkOperation op = new ChessNetworkOperation(ChessNetworkOperation.OP_TIE);
		op.setTiePlayerName(whichPlayer);
		sendOperation(op);
	}
	
	public void networkSendAgreeTie() {
		ChessNetworkOperation op = new ChessNetworkOperation(ChessNetworkOperation.OP_AGREETIE);
		sendOperation(op);
	}
	
	/*
	 * Helper function for packaging a move operation object and send it
	 * */
	public void networkSendMove(int srcX, int srcY, int dstX, int dstY) {
		ChessNetworkOperation op = new ChessNetworkOperation(ChessNetworkOperation.OP_MOVEPIECE);
		op.setSrcX(srcX);
		op.setSrcY(srcY);
		op.setDstX(dstX);
		op.setDstY(dstY);
		sendOperation(op);
	}


	@Override
	public void run() {
		System.out.println("Current thread(NETWORK) id is " + Thread.currentThread().getId());
		networkThread();
		
	}
	
}
