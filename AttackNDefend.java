package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import soldier.Soldier;
import soldier.SoldierManager;


public class AttackNDefend extends JPanel implements Runnable{
	
	// --- VARIABLES AND CONTANTS ---
	
	private static final long serialVersionUID = 1L;
	
	// Fonts
	private final Font FONT_SMALL = new Font ("Arial", Font.PLAIN, 20);
	private final Font FONT_BIG = new Font ("Arial", Font.PLAIN, 28);
	private final Font FONT_GAME_FINISHED = new Font ("Arial", Font.BOLD, 168);
	private final Font FONT_GAME_PAUSED = new Font ("Arial", Font.BOLD, 168);
	
	// Colors
	private final Color COLOR_GAME_FINISHED = Color.ORANGE;
	private final Color COLOR_GAME_PAUSED = Color.ORANGE;
	
	// Positions
	private Point defenderShipPosition;
	
	// Painting sizes
	private final int TEXT_TOP = 30;
	private final int INFO_SIZE = 3 * TEXT_TOP;		// Space to show level, points etc.
	private Dimension panelSize;
	
	//private final Dimension DEFENDER_BOX_SIZE = new Dimension (20,100);
	private final Integer DEFENDER_OVAL_RADIOUS = 10;
	private final Integer DEFENDER_OVAL_DIAMETER = DEFENDER_OVAL_RADIOUS * 2;
	
	private final Color DEFENDER_OVAL_COLOR = new Color ( 4496965);
	
	// Game constants
	private final int MAX_LIFES = 3;
	private final int MAX_BOMBS = 3;	
	private final int GAME_SPEED = 200;
	
	private final int POINTS_PER_LIFE = 750;
	private final int POINTS_PER_BOMB = 500;
	
	private final int TIME_BETWEEN_LEVELS = 2000; 	// in milliseconds
	private final int TIME_WHEN_DEFENDER_HIT = 500; 
	
	// Game variables
	
	SoldierManager a;
	SoldierManager d;
	
	private Point mousePosition;
	
	private long lastDefenderShootTime = 0;
	private long lastAttackerShootTime = 0;	
	
	private int timePerDefenderShoot;
	private int timePerAttackerShoot;
	
	private boolean running;
	private boolean game, pause;
	private boolean mousePressed;
	private boolean keyPressedW;
	private boolean keyPressedS;
	private boolean keyPressedA;
	private boolean keyPressedD;
	
	private int points, lifes, level, soldiersToNextLevel, hitSoldiers, bombs;
	
	public AttackNDefend() {
							
		setListeners();
		
		newGame();
		
	}
	
	private class ListenersController{									// New class MouseController
		
		private MouseMotion mouseMotion;
		private Mouse mouse;
		private Key key;
		
		public ListenersController(){
			mouseMotion = new MouseMotion();
			mouse = new Mouse();
			key = new Key();
		}
		
		private class MouseMotion extends MouseMotionAdapter{		// New class MouseMotion
			public void mouseDragged(MouseEvent me) {
				if ( SwingUtilities.isLeftMouseButton(me)){
					mousePosition = me.getPoint();
					mousePressed = true;
				}
			}
			public void mouseMoved(MouseEvent me) {
				mousePosition = me.getPoint();
			}
		}
		
		private class Mouse extends MouseAdapter{					// New class Mouse
			public void mousePressed (MouseEvent me){	
				if ( SwingUtilities.isLeftMouseButton(me))
					mousePressed = true;	

			}
	    	public void mouseReleased (MouseEvent me){
	    		if ( !SwingUtilities.isRightMouseButton(me))
					mousePressed = false;	
				else
					manageDefenderBomb();
			}
		}
		
		private class Key extends KeyAdapter {						// New class Key
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()){
					case KeyEvent.VK_W : keyPressedW = true; break;
					case KeyEvent.VK_S : keyPressedS = true; break;
					case KeyEvent.VK_A : keyPressedA = true; break;
					case KeyEvent.VK_D : keyPressedD = true; break;
				}
			}

			public void keyReleased(KeyEvent e) {
				switch(e.getKeyCode()){
					case KeyEvent.VK_W : keyPressedW = false; break;
					case KeyEvent.VK_S : keyPressedS = false; break;
					case KeyEvent.VK_A : keyPressedA = false; break;
					case KeyEvent.VK_D : keyPressedD = false; break;
					case KeyEvent.VK_R : newGame(); break;
					case KeyEvent.VK_P : pause = !pause; break;
					case KeyEvent.VK_Q : game = false; 
				}
			}
		}
		
		public MouseMotionAdapter getMouseMotionAdapter(){			// Getters and Setters
			return mouseMotion;
		}
		
		public MouseAdapter getMouseAdapter(){
			return mouse;
		}
		
		public KeyAdapter getKeyAdapter(){
			return key;
		}
	}
	
	private void setListeners() {
		ListenersController mc = new ListenersController();
		
		// Mouse Related Listeners
		addMouseListener(mc.getMouseAdapter());
		addMouseMotionListener(mc.getMouseMotionAdapter());
		
		// KeyBoard Related Listener
		addKeyListener(mc.getKeyAdapter());
		
		
	}

	private void newGame(){
		terminate();
		setBeginningValues();
		setNextLevelValues(++level);
		(new Thread(this)).start();
	}

	private void setBeginningValues() {
		a = new SoldierManager();
		d = new SoldierManager();
		
		removeAll();
		
		level = 0;
		running = true;	
		game = true;
		pause = false;
		lifes = MAX_LIFES; 
		points = 0;
		bombs = MAX_BOMBS;
	}

	private void setNextLevelValues(int nextLevel) {
		level = nextLevel;
		soldiersToNextLevel = 10 * nextLevel; 
		hitSoldiers = 0;
		
		timePerDefenderShoot = 200 - (int)Math.sqrt(10*level);
		timePerAttackerShoot = 500 - nextLevel *10;
		
	}
	
	// --- THREAD CODE ---
	
	public void terminate(){
		running = false;
	}
	
	public void run() {
		long oldTime = System.nanoTime(), newTime, deltaTime;
		
		try {Thread.sleep(TIME_BETWEEN_LEVELS);} catch (InterruptedException e1) {}
		
		requestFocusInWindow();
		
		while(running){
			
			while (game && !pause){
				newTime = System.nanoTime();
				deltaTime = (newTime - oldTime) / GAME_SPEED;
	            oldTime = newTime;	
				
				physics(deltaTime); 		// Moves the components and calculates new positions
				
				manageDefenderShoot();		// Takes care of defender
				manageAttackerShoot();		// Takes care of attacker
				
				crashes(); 					// Checks if there is any crash 
				
				gameStatus(); 				// Checks if a new level has to me set or if the game has finished
				
				repaint();  				// Asks to repaint after painting objects
				
				try {Thread.sleep(800/GAME_SPEED);} catch (InterruptedException e) {}
			}
			
			repaint();  				// Asks to repaint after painting objects		
			
		}
	}
	
	// --- GAME METHODS ---
	
	private void physics(long deltaTime) {
		
		moveShip();
		
		physicsSoldiers(deltaTime, d.listIterator(), false);
		physicsSoldiers(deltaTime, a.listIterator(), true);

	}

	private void moveShip() {
		if (keyPressedW && defenderShipPosition.getY() > INFO_SIZE + DEFENDER_OVAL_RADIOUS) defenderShipPosition.y -= 1;
		if (keyPressedS && defenderShipPosition.getY() < panelSize.getHeight() - DEFENDER_OVAL_RADIOUS) defenderShipPosition.y += 1;
		if (keyPressedA && defenderShipPosition.getX() > DEFENDER_OVAL_RADIOUS) defenderShipPosition.x -= 1;
		if (keyPressedD && defenderShipPosition.getX() < panelSize.getWidth() - DEFENDER_OVAL_RADIOUS) defenderShipPosition.x += 1;
	}


	private void physicsSoldiers (long deltaTime, Iterator<Soldier> iterator, boolean updateAngle){
		try {
			Iterator<Soldier> it = iterator;

			while ( it.hasNext() ){
				Soldier s = it.next();
				
				if ( s.getX() < -(s.getRadious() + 10) | s.getX() > panelSize.getWidth() | s.getY() + s.getRadious() < INFO_SIZE | s.getY() > panelSize.getHeight())
					it.remove();
				
				if (updateAngle){
					s.setAngle(calculateAngle(new Point((int)s.getX(), (int)s.getY()), defenderShipPosition));
					if ( s.crashed(defenderShipPosition, DEFENDER_OVAL_RADIOUS)) {
						it.remove();
						lifes--;
						try {Thread.sleep(TIME_WHEN_DEFENDER_HIT);} catch (InterruptedException e) {}
					}
				}
				
				s.setX((float)(s.getX() + s.getSpeed() * Math.cos(Math.toRadians(s.getAngle()))));
				s.setY((float)(s.getY() + s.getSpeed() * Math.sin(Math.toRadians(s.getAngle()))));
				
			}
		} catch (ConcurrentModificationException e) {}
	}
		
	private void manageDefenderShoot(){
		if ( (System.currentTimeMillis() - lastDefenderShootTime) > timePerDefenderShoot && mousePressed){
			lastDefenderShootTime = System.currentTimeMillis();
			createDefenderShoot();
		}
	}
	
	private void manageDefenderBomb(){
		if ( bombs > 0 ){
			bombs--;
			d.createBomb(defenderShipPosition);
		}
	}
	
	private void manageAttackerShoot(){
		if (a.getSize() + hitSoldiers < soldiersToNextLevel && (System.currentTimeMillis() - lastAttackerShootTime) > timePerAttackerShoot ){
			lastAttackerShootTime = System.currentTimeMillis();
			createAttackerShoot();
		}
	}

	private void createDefenderShoot() {
		if ( level < 3 )
			d.createSingleShot(defenderShipPosition, calculateAngle(defenderShipPosition , mousePosition));
		else if ( level < 5 )
			d.createDoubleShot(defenderShipPosition, calculateAngle(defenderShipPosition , mousePosition));
		else if ( level < 10 )
			d.createTripleShot(defenderShipPosition, calculateAngle(defenderShipPosition , mousePosition));
		else /*if ( level < 12 )*/
			d.createQuintupleShot(defenderShipPosition, calculateAngle(defenderShipPosition , mousePosition));
		
		}
	private void createAttackerShoot() {
		Point p;
		if ( level < 2 ) 
			a.createSlowEnemy (p = createPointOutsidePanel(), calculateAngle(p , defenderShipPosition));
		else if ( level < 4 )
			a.createNormalEnemy (p = createPointOutsidePanel(), calculateAngle(p , defenderShipPosition));
		else if ( level < 5 ){
			a.createSlowEnemy (p = createPointOutsidePanel(), calculateAngle(p , defenderShipPosition));
			a.createNormalEnemy (p = createPointOutsidePanel(),  calculateAngle(p, defenderShipPosition));
		}else if ( level < 7){
			a.createSlowEnemy (p = createPointOutsidePanel(), calculateAngle(p , defenderShipPosition));
			a.createNormalEnemy (p = createPointOutsidePanel(),  calculateAngle(p, defenderShipPosition));
			a.createNormalEnemy (p = createPointOutsidePanel(),  calculateAngle(p, defenderShipPosition));
		}else if ( level < 9){
			a.createSlowEnemy (p = createPointOutsidePanel(), calculateAngle(p , defenderShipPosition));
			a.createNormalEnemy (p = createPointOutsidePanel(),  calculateAngle(p, defenderShipPosition));
			a.createFastBigEnemy (p = createPointOutsidePanel(), calculateAngle(p , defenderShipPosition));
		}else if ( level < 11 ){
			a.createSlowEnemy (p = createPointInsidePanel(), calculateAngle(p , defenderShipPosition));
			a.createNormalEnemy (p = createPointOutsidePanel(),  calculateAngle(p, defenderShipPosition));
			a.createFastBigEnemy (p = createPointOutsidePanel(), calculateAngle(p , defenderShipPosition));
		}else if ( level < 13 ){
			a.createNormalEnemy (p = createPointOutsidePanel(),  calculateAngle(p, defenderShipPosition));
			a.createNormalEnemy (p = createPointOutsidePanel(),  calculateAngle(p, defenderShipPosition));
			a.createFastBigEnemy (p = createPointOutsidePanel(), calculateAngle(p , defenderShipPosition));
		}else if ( level < 15 ){
			a.createSlowEnemy (p = createPointOutsidePanel(), calculateAngle(p , defenderShipPosition));
			a.createNormalEnemy (p = createPointOutsidePanel(),  calculateAngle(p, defenderShipPosition));
			a.createNormalEnemy (p = createPointOutsidePanel(),  calculateAngle(p, defenderShipPosition));
			a.createFastBigEnemy (p = createPointOutsidePanel(), calculateAngle(p , defenderShipPosition));
		}else /*if ( level < 17 )*/{
			a.createSlowEnemy (p = createPointOutsidePanel(), calculateAngle(p , defenderShipPosition));
			a.createNormalEnemy (p = createPointOutsidePanel(),  calculateAngle(p, defenderShipPosition));
			a.createFastBigEnemy (p = createPointOutsidePanel(), calculateAngle(p , defenderShipPosition));
			a.createFastSmallEnemy (p = createPointOutsidePanel(), calculateAngle(p , defenderShipPosition));
		}
	}

	private Point createPointOutsidePanel() {
		Random r = new Random();
		Point p = null;
		
		switch ( r.nextInt(4) ){
		
			case 0 : p = new Point ( r.nextInt((int)panelSize.getWidth()) , INFO_SIZE - 10); break;
			case 1 : p = new Point ( -10 , r.nextInt((int)(panelSize.getHeight() - INFO_SIZE - 10)) + INFO_SIZE ); break;
			case 2 : p = new Point ( r.nextInt((int)panelSize.getWidth()), (int)panelSize.getHeight() ); break;
			case 3 : p = new Point ( (int) panelSize.getWidth(), (int) r.nextInt((int)(panelSize.getHeight() - INFO_SIZE - 10)) + INFO_SIZE ); break;
		}
		
		return p;
	}

	private Point createPointInsidePanel() {
		Random r = new Random();
		return new Point(r.nextInt((int)panelSize.getWidth()), r.nextInt((int)panelSize.getHeight() - INFO_SIZE) + INFO_SIZE);
	}

	private void crashes() {
		try {
			Iterator<Soldier> at = a.listIterator();

			while ( at.hasNext() ){
				Soldier s0 = at.next();
				
				Iterator<Soldier> def = d.listIterator();
				
				while( def.hasNext() ){
					Soldier s1 = def.next();
	
					if ( SoldierManager.crashed(s0, s1) ){
						a.remove(s0);
						d.remove(s1);
						points += level;
						hitSoldiers++;
						if ( points % POINTS_PER_LIFE == 0) lifes++;
						if ( points % POINTS_PER_BOMB == 0) bombs++;
					}
				}
			}
		} catch (Exception e) {}
	}
	
	private void gameStatus(){
		
		if ( lifes == 0 ) {
			game = false;;
		
		}else if ( soldiersToNextLevel == hitSoldiers ) {
			setNextLevelValues(++level);
			removeAllSoldiers();
			repaint();
			try {Thread.sleep(TIME_BETWEEN_LEVELS);} catch (InterruptedException e) {}
			
		}
		
	}
	
	private void removeAllSoldiers() {
		a.removeAll();
		d.removeAll();
		
	}

	// --- GRAPHICS ---

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (panelSize == null) getPanelValues();
		
		
		// Paint objects
				
		paintAttacker(g2d);
		paintDefender(g2d);
		
		paintValues(g2d);	
		
		if ( !game ){
			printGameOver(g2d);
		}else if( pause )
			printPause(g2d);
	}
	
	private void printPause(Graphics g2d) {
		g2d.setColor(COLOR_GAME_PAUSED);
		g2d.setFont(FONT_GAME_PAUSED);
		g2d.drawString("PAUSE", (int)panelSize.getWidth()/4, (int)(panelSize.getHeight() - INFO_SIZE)/2 + INFO_SIZE);
	}

	private void printGameOver(Graphics2D g2d) {
		g2d.setColor(COLOR_GAME_FINISHED);
		g2d.setFont(FONT_GAME_FINISHED);
		g2d.drawString("GAME OVER", (int)panelSize.getWidth()/26, (int)(panelSize.getHeight() - INFO_SIZE)/2 + INFO_SIZE);
	}

	private void paintValues(Graphics2D g) {
		
		g.setPaint(Color.BLACK);  
		
		g.fillRect(0, 0, (int)panelSize.getWidth(), INFO_SIZE);
		
		g.setPaint(Color.WHITE);
		
		g.setFont(FONT_SMALL);
		g.drawString("Soldiers to next level :  " + String.valueOf(soldiersToNextLevel), 50, INFO_SIZE*2/5);
		g.drawString("Hit soldiers : " + String.valueOf(hitSoldiers), 50, INFO_SIZE*4/5);
		
		g.setFont(FONT_BIG);
		g.drawString("Points : " + String.valueOf(points), 500, TEXT_TOP*2);
		
		g.setFont(FONT_SMALL);
		g.drawString("Level : " + String.valueOf(level), 950, INFO_SIZE*2/7);
		g.drawString("Lifes : " + String.valueOf(lifes), 950, INFO_SIZE*4/7);
		g.drawString("Bombs : " + String.valueOf(bombs), 950, INFO_SIZE*6/7);
	}
	
	private void paintDefender(Graphics2D g2d) {
		
		
		paintSoldierIterator(g2d, d);
		
		g2d.setColor(DEFENDER_OVAL_COLOR);
		
		g2d.fillOval((int)(defenderShipPosition.getX()) - DEFENDER_OVAL_RADIOUS, (int)defenderShipPosition.getY() - DEFENDER_OVAL_RADIOUS, DEFENDER_OVAL_DIAMETER, DEFENDER_OVAL_DIAMETER);
		
	}

	private void paintAttacker(Graphics2D g2d) {
		
		paintSoldierIterator(g2d, a);
		
	}
	
	private void paintSoldierIterator(Graphics2D g2d, SoldierManager soldierList){
				
		try {
			Iterator<Soldier> it = soldierList.listIterator();
			
			while ( it.hasNext() ){
				Soldier s = it.next();
				g2d.setColor(s.getColor());
				g2d.fillOval((int)(s.getX()-s.getRadious()), (int)(s.getY()-s.getRadious()), (int)s.getRadious()*2, (int)s.getRadious()*2);	
			}
		} catch (ConcurrentModificationException e) {
			
		}catch (NoSuchElementException e){
			
		}catch (NullPointerException e){}
		
		
	}

	
	// --- UTILITIES --- 
	
	private void getPanelValues() {
		
		panelSize = new Dimension (this.getWidth(), this.getHeight());
				
		// Center
		defenderShipPosition = new Point ( 	(int)(panelSize.getWidth()/2),
				(int)((panelSize.getHeight() - INFO_SIZE)/2) + INFO_SIZE);
		
	}
	
	private float calculateAngle(Point p0, Point p1){
		
		if (p0 != null && p1 != null){
			float dX = (float) (p1.getX() - p0.getX()), dY = (float) (p1.getY() - p0.getY()), angle = 0;
			
			angle = (float) Math.toDegrees(Math.atan( dY / dX ));
			
			if ( dX < 0 && dY > 0)
				angle += 180;
			
			if ( dX < 0 && dY < 0)
				angle += 180;
			
			return angle;
		}else{
			return (float) 0;
		}
	}
}
