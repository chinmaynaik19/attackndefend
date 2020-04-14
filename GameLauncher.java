package game;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class GameLauncher implements ActionListener{
	
	private JFrame window;
	
	public static void main(String[] args) {
		GameLauncher game = new GameLauncher();
		game.launchGame();
	}

	private void launchGame() {
		
		createInitialWindow();
		createMenuWindow();		
		
	}

	private void createInitialWindow() {
		window = new JFrame("Attack N Defend Launcher");
		window.setSize(450,300);
		window.setLocationRelativeTo(null);
		window.setContentPane(new JLabel(new ImageIcon(this.getClass().getResource("/resources/AND_logo.jpg"))));
		try {window.setIconImage(ImageIO.read(( this.getClass().getResource("/resources/AND_icon.png"))));} catch (IOException e1) {}
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.setUndecorated(true);
		window.setVisible(true);
		
		try {Thread.sleep(2000);} catch (InterruptedException e) {} // Wait 2s 
		
	}
	
	private void createMenuWindow() {
		window.setTitle("Attack N Defend");
		window.setVisible(false);
		window.setContentPane(createPanelMenu());
		window.setSize(1100,600);
		window.setLocationRelativeTo(null);
		//window.setUndecorated(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);		
	}
		
	private Container createPanelMenu() {
		JPanel pMenu = new JPanel ( new GridLayout(3,1,20,80));
		
		pMenu.setBackground(Color.BLACK);
		
		pMenu.setBorder(BorderFactory.createEmptyBorder(80,100,80,100));
		
		pMenu.add(createButton("PLAY"));
		pMenu.add(createButton("HOW TO PLAY"));
		pMenu.add(createButton("EXIT"));
		
		return pMenu;
	}


	private Component createButton(String string) {
		JButton button = new JButton(string);
		
		button.setBackground(Color.white);
		
		button.addActionListener(this);
		button.setActionCommand(string);
		
		button.setFont(new Font("Arial", Font.BOLD, 48));
		
		return button;
	}
	
	private void startGame() {
		
		window.setContentPane( new AttackNDefend() );
		
		window.validate();

	}
	
	private void startPaneLInstructions() {
		
		window.setContentPane ( createInstructionsPanel() ) ;
		
		window.validate();
	}
	
	private Container createInstructionsPanel() {
		JPanel pI = new JPanel (new GridLayout(13,1));
		
		pI.setBackground(Color.BLACK);
		
		pI.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));
		
		pI.add(createLabel("W  -   UP"));
		pI.add(createLabel("A   -   LEFT"));
		pI.add(createLabel("S   -   DOWN"));
		pI.add(createLabel("D   -   RIGHT"));
		pI.add(createLabel(""));
		pI.add(createLabel("LEFT CLICK     -   SHOOT"));
		pI.add(createLabel("RIGHT CLICK   -   BOMB"));
		pI.add(createLabel(""));
		pI.add(createLabel("Q   -   EXIT"));
		pI.add(createLabel("R   -   RESTART"));
		pI.add(createLabel("P   -   PAUSE"));
		pI.add(createLabel(""));
		pI.add(createButton("BACK"));
		
		return pI;
	}

	private Component createLabel(String string) {
		JLabel l = new JLabel (string);
		
		l.setVerticalAlignment(JLabel.CENTER);
		l.setForeground(Color.WHITE);
		l.setFont(new Font ("Arial", Font.BOLD, 48));
		
		return l;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		switch (e.getActionCommand()) {
		
		case "PLAY" : startGame(); break;
		case "HOW TO PLAY" : startPaneLInstructions(); break;
		case "BACK" : window.setContentPane(createPanelMenu()); window.validate(); break;
		case "EXIT" : window.dispose(); break;
		
		}
	}
	
}
