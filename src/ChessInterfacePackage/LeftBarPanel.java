package ChessInterfacePackage;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class LeftBarPanel extends JPanel{

	
	// Some buttons
	JButton restartButton;
	JButton newClassicGameButton;
	JButton newCustomGameButton;
	JButton undoButton;
	JButton quitButton;
	
	// JPanel for the players
	JPanel player1;
	JPanel player2;
	
	JLabel winNumLabel1;
	JLabel winNumLabel2;
	
	JLabel stepPlayer1;
	JLabel stepPlayer2;
	
	JButton btnShakeHand1;
	JButton btnShakeHand2;
	
	JButton btnForfeit1;
	JButton btnForfeit2;
	
	// Some Title Borders
	TitledBorder tb1;
	TitledBorder tb2;
	
	
	// Some fonts
    public static Font font1 = new Font("Comic Sans MS", Font.BOLD, 30);
    public static Font font2 = new Font("Comic Sans MS", Font.BOLD, 19);
    
    // The game panel
    ChessGamePanel gamePanel;

    
	public LeftBarPanel(ChessGamePanel gamePanel) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.gamePanel = gamePanel;
		
		setupNavigationButtons();
		setupPlayerStat();
		
	}
	

	public void setupPlayerStat() {
        // Player 1 stats
        tb1 = new TitledBorder(gamePanel.redPlayer.getName());
        tb1.setTitleFont(font1);
        tb1.setTitleColor(ChessGamePanel.player1Color);
        player1 = new JPanel();
        player1.setBorder(tb1);
        player1.setBackground(ChessGamePanel.gameBGColor);   
        
        // Player 2 stats
        tb2 = new TitledBorder(gamePanel.blackPlayer.getName());
        tb2.setTitleFont(font2);
        tb2.setTitleColor(ChessGamePanel.player2Color);
        player2 = new JPanel();
        player2.setBorder(tb2);
        player2.setBackground(ChessGamePanel.gameBGColor);        

        
        // Player 1 label(score)
        winNumLabel1 = new JLabel("score: " + gamePanel.redPlayer.getScore());
        
        // Player 2 label(score)
        winNumLabel2 = new JLabel("score: " + gamePanel.blackPlayer.getScore());
        
        
        setupPlayerButtons();
        setupNavigationButtons();
        
        GridBagLayout gbl = new GridBagLayout();
        player1.setLayout(gbl);
        player2.setLayout(gbl);
        
        gridBagDrawer(player1, winNumLabel1, "jlabel", gbl, 0, 0);
        gridBagDrawer(player2, winNumLabel2, "jlabel", gbl, 0, 0);
        
        gridBagDrawer(player1, btnShakeHand1, "jbutton", gbl, 0, 1);
        gridBagDrawer(player2, btnShakeHand2, "jbutton", gbl, 0, 1);
        
        gridBagDrawer(player1, btnForfeit1, "jbutton", gbl, 0, 3);
        gridBagDrawer(player2, btnForfeit2, "jbutton", gbl, 0, 3);
        
        setBackground(ChessGamePanel.gameBGColor);
        setBorder(new EmptyBorder(0,0,0,0));
        add(restartButton);
        add(newClassicGameButton);
        add(newCustomGameButton);
        add(undoButton);
        add(quitButton);
        add(player1);
        add(player2);
	}
	
	/**
	 * Helper function for grid bag layout management
	 * */
	private void gridBagDrawer(JPanel mainPanel, Object childPanel, String type, GridBagLayout gbl, int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weighty = 1;
        gbc.weightx = 1;
        
        gbc.gridx = x;
        gbc.gridy = y;
        
        if(type.toLowerCase().equals("jlabel"))
        	mainPanel.add((JLabel)childPanel, gbc);
        else
        	mainPanel.add((JButton)childPanel, gbc);
	}
	
	private JButton initPlayerButton(String btnImagePath) {
        JButton button = new JButton();
        button.setIcon(new ImageIcon(this.getClass().getResource(btnImagePath)));
        button.setBorder(new EmptyBorder(0, 0, 0, 0));
		return button;
	}
	
	private JButton initNavigationButton(String btnImagePath) {
		Border b = new LineBorder(ChessGamePanel.gameBGColor, 10);
		
		JButton button = new JButton();
		button.setIcon(new ImageIcon(this.getClass().getResource(btnImagePath)));
		button.setBorder(b);
		button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		return button;
	}
	private void setupPlayerButtons() {
		btnForfeit1 = initPlayerButton("/btnSurrenderYellow.png");
		btnForfeit1.setActionCommand(gamePanel.redPlayer.getName());
		btnForfeit1.addActionListener(btnForfeitHandler());
		
		btnForfeit2 = initPlayerButton("/btnSurrenderBlack.png");
		btnForfeit2.setActionCommand(gamePanel.blackPlayer.getName());
		btnForfeit2.addActionListener(btnForfeitHandler());
		
		btnShakeHand1 = initPlayerButton("/btnShakeHandYellow.png");
		btnShakeHand1.setActionCommand(gamePanel.redPlayer.getName());
		btnShakeHand1.addActionListener(btnShakeHandHandler());
		
		btnShakeHand2 = initPlayerButton("/btnShakeHandBlack.png");
		btnShakeHand2.setActionCommand(gamePanel.blackPlayer.getName());
		btnShakeHand2.addActionListener(btnShakeHandHandler());
	}
	private void setupNavigationButtons() {
		restartButton = initNavigationButton("/btnStartOver.png");
		restartButton.addActionListener(btnRestartHandler());
		
        newClassicGameButton = initNavigationButton("/btnNewGame1.png");
        newClassicGameButton.addActionListener(btnNewClassicGameHandler());
        
        newCustomGameButton = initNavigationButton("/btnNewGame2.png");
        newCustomGameButton.addActionListener(btnNewCustomGameHandler());
        
        undoButton = initNavigationButton("/btnUndo.png");
        undoButton.addActionListener(btnUndoHandler());
        
        quitButton = initNavigationButton("/btnQuit.png");
        quitButton.addActionListener(btnQuitHandler());
	}
	
	

	private  ActionListener btnRestartHandler() {
		return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gamePanel.getBoardPanel().restart(false);
              }
          };
	}
	
	private  ActionListener btnNewClassicGameHandler() {
		return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gamePanel.getBoardPanel().newClassicGame(false);
              }
          };
	}
	
	private  ActionListener btnNewCustomGameHandler() {
		return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gamePanel.getBoardPanel().newCustomGame(false);
              }
          };
	}

	private  ActionListener btnUndoHandler() {
		return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(gamePanel.getNetworkController().getmode() == false || gamePanel.getBoardPanel().getBoard().getTurn() != gamePanel.getNetworkController().getPlayer())
            		gamePanel.getBoardPanel().undo(false);
              }
          };
	}
	
	private  ActionListener btnQuitHandler() {
		return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gamePanel.getBoardPanel().Quit(false);
              }
          };
	}
	
	private  ActionListener btnForfeitHandler() {
		return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JButton button = (JButton)e.getSource();
            	String whichPlayer = button.getActionCommand();
            	if((whichPlayer.equals("Mr Red") && gamePanel.getNetworkController().getPlayer()) ||
            		(whichPlayer.equals("Mr Black") && !gamePanel.getNetworkController().getPlayer()))
            	gamePanel.getBoardPanel().forfeit(whichPlayer, false);
            }
          };
	}
	
	private  ActionListener btnShakeHandHandler() {
		return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JButton button = (JButton)e.getSource();
            	String whichPlayer = button.getActionCommand();
            	if((whichPlayer.equals("Mr Red") && gamePanel.getNetworkController().getPlayer()) ||
                		(whichPlayer.equals("Mr Black") && !gamePanel.getNetworkController().getPlayer()))
            	gamePanel.getBoardPanel().tie(whichPlayer, false);
            }
          };
	}
	
}