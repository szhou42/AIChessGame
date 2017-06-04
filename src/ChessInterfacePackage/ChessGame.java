package ChessInterfacePackage;
import java.awt.Color;
import ChessBoardPackage.*;

import java.awt.EventQueue;
import java.util.*;

import javax.swing.JFrame;

public class ChessGame extends JFrame{

	public static void main(String[] args) {
    	// Run GUI in a separate thread
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChessGamePanel().display();
            }
        });
	}

}
