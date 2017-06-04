package ChessNetworkPackage;
import java.io.Serializable;

public class ChessNetworkOperation implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final int OP_STARTOVER = 1000;
	public static final int OP_NEWGAME1 = 1001;
	public static final int OP_NEWGAME2 = 1002;
	public static final int OP_UNDO = 1003;
	public static final int OP_QUIT = 1004;
	public static final int OP_SURRENDER = 1005;
	public static final int OP_TIE = 1006;
	public static final int OP_MOVEPIECE = 1007;
	public static final int  OP_AGREETIE = 1008;
	
	private int operationType;
	
	// Information necessary to move a piece
	private int srcX;
	private int srcY;
	private int dstX;
	private int dstY;
	
	// Information necessary to surrender
	private String forfeitPlayerName;
	
	// Information necessary to ask a tie
	private String tiePlayerName;
	
	public String getTiePlayerName() {
		return tiePlayerName;
	}

	public void setTiePlayerName(String tiePlayerName) {
		this.tiePlayerName = tiePlayerName;
	}

	public String getForfeitPlayerName() {
		return forfeitPlayerName;
	}
	
	public void setForfeitPlayerName(String forfeitPlayerName) {
		this.forfeitPlayerName = forfeitPlayerName;
	}
	
	public ChessNetworkOperation(int type) {
		this.operationType = type;
	}
	public int getOperationType() {
		return operationType;
	}
	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

	public int getSrcX() {
		return srcX;
	}
	public void setSrcX(int srcX) {
		this.srcX = srcX;
	}

	public int getSrcY() {
		return srcY;
	}
	public void setSrcY(int srcY) {
		this.srcY = srcY;
	}

	public int getDstX() {
		return dstX;
	}
	public void setDstX(int dstX) {
		this.dstX = dstX;
	}

	public int getDstY() {
		return dstY;
	}
	public void setDstY(int dstY) {
		this.dstY = dstY;
	}
	
}
