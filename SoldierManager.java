package soldier;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SoldierManager {
	
	// --- DEFENDERS CONSTANTS ---
	private final static float SINGLE_SHOT_SPEED = 2f;
	private final static float SINGLE_SHOT_RADIOUS = 5f;
	private final static Color SINGLE_SHOT_COLOR = Color.GREEN;
	
	private final static float DOUBLE_SHOT_SPEED = 2f;
	private final static float DOUBLE_SHOT_RADIOUS = 5f;
	private final static Color DOUBLE_SHOT_COLOR = Color.GREEN;
	
	private final static float TRIPLE_SHOT_SPEED = 2f;
	private final static float TRIPLE_SHOT_RADIOUS = 5f;
	private final static Color TRIPLE_SHOT_COLOR = Color.GREEN;
	
	private final static float QUINTUPLE_SHOT_SPEED = 2f;
	private final static float QUINTUPLE_SHOT_RADIOUS = 5f;
	private final static Color QUINTUPLE_SHOT_COLOR = Color.GREEN;
	
	private final static int BOMB_SHOTS = 1000;
	private final static float BOMB_SPEED = 2.0f;
	private final static float BOMB_RADIOUS = 4f;
	private final static Color BOMB_COLOR = Color.ORANGE;

	// --- ATTACKERS CONSTANTS ---
	
	private final static float SLOW_ENEMY_SPEED = 0.2f;
	private final static float SLOW_ENEMY_RADIOUS = 10;
	private final static Color SLOW_ENEMY_COLOR = Color.RED;
	
	private final static float NORMAL_ENEMY_SPEED = 0.35f;
	private final static float NORMAL_ENEMY_RADIOUS = 10;
	private final static Color NORMAL_ENEMY_COLOR = Color.BLUE;
	
	private final static float FAST_BIG_ENEMY_SPEED = 0.5f;
	private final static float FAST_BIG_ENEMY_RADIOUS = 12;
	private final static Color FAST_BIG_ENEMY_COLOR = Color.DARK_GRAY;
	
	private final static float FAST_SMALL_ENEMY_SPEED = 0.5f;
	private final static float FAST_SMALL_ENEMY_RADIOUS = 8;
	private final static Color FAST_SMALL_ENEMY_COLOR = Color.LIGHT_GRAY;
		
	private List <Soldier> list;
	
	// --- CLASS RELATED METHODS ---
	
	public SoldierManager(){
		
		list = new ArrayList<Soldier>();
	}
	
	private void addSoldier ( Soldier s ){
		list.add(s);
	}
	
	public int getSize(){
		return list.size();
	}
	
	public void remove(Soldier s){
		
		if ( !list.remove(s) ) 
			System.out.println("No removed"); // Prueba
	}
	public void removeAll(){
		list.removeAll(list);
	}
	
	public Iterator<Soldier> listIterator() {
		return list.listIterator();
	}
	
	
	// --- SOLDIERS RELATED METHODS ---
	
	// --- DEFENDERS ---
	public void createSingleShot(Point p, float angle){
		addSoldier(new Soldier(p, SINGLE_SHOT_SPEED, angle, SINGLE_SHOT_RADIOUS, SINGLE_SHOT_COLOR));
	}
	
	public void createDoubleShot(Point p, float angle){
		addSoldier(new Soldier((float)p.getX() - 10, (float)p.getY(), DOUBLE_SHOT_SPEED, angle - 1, DOUBLE_SHOT_RADIOUS, DOUBLE_SHOT_COLOR));
		addSoldier(new Soldier((float)p.getX() + 10, (float)p.getY(), DOUBLE_SHOT_SPEED, angle + 1, DOUBLE_SHOT_RADIOUS, DOUBLE_SHOT_COLOR));
	}
	
	public void createTripleShot(Point p, float angle){
		addSoldier(new Soldier((float)p.getX() - 5, (float)p.getY(), TRIPLE_SHOT_SPEED, angle - 2, TRIPLE_SHOT_RADIOUS, TRIPLE_SHOT_COLOR));
		addSoldier(new Soldier((float)p.getX() + 0, (float)p.getY(), TRIPLE_SHOT_SPEED, angle + 0, TRIPLE_SHOT_RADIOUS, TRIPLE_SHOT_COLOR));
		addSoldier(new Soldier((float)p.getX() + 5, (float)p.getY(), TRIPLE_SHOT_SPEED, angle + 2, TRIPLE_SHOT_RADIOUS, TRIPLE_SHOT_COLOR));
	}
	
	public void createQuintupleShot(Point p, float angle){
		addSoldier(new Soldier((float)p.getX() - 10, (float)p.getY(), QUINTUPLE_SHOT_SPEED, angle - 10, QUINTUPLE_SHOT_RADIOUS, QUINTUPLE_SHOT_COLOR));
		addSoldier(new Soldier((float)p.getX() - 5, (float)p.getY(), QUINTUPLE_SHOT_SPEED, angle - 5, QUINTUPLE_SHOT_RADIOUS, QUINTUPLE_SHOT_COLOR));
		addSoldier(new Soldier((float)p.getX() + 0, (float)p.getY(), QUINTUPLE_SHOT_SPEED, angle + 0, QUINTUPLE_SHOT_RADIOUS, QUINTUPLE_SHOT_COLOR));
		addSoldier(new Soldier((float)p.getX() + 5, (float)p.getY(), QUINTUPLE_SHOT_SPEED, angle + 5, QUINTUPLE_SHOT_RADIOUS, QUINTUPLE_SHOT_COLOR));
		addSoldier(new Soldier((float)p.getX() + 10, (float)p.getY(), QUINTUPLE_SHOT_SPEED, angle + 10, QUINTUPLE_SHOT_RADIOUS, QUINTUPLE_SHOT_COLOR));
		
	}
	
	public void createBomb(Point p) {
		for ( int i = 0 ; i < BOMB_SHOTS ; i++)
			addSoldier(new Soldier((float)p.getX(), (float)p.getY(), BOMB_SPEED, 360 * i / BOMB_SHOTS, BOMB_RADIOUS, BOMB_COLOR));
		
	}
	
	// --- ATTACKERS ---
	
	public void createSlowEnemy(Point p, float angle){
		addSoldier(new Soldier(p, SLOW_ENEMY_SPEED, angle, SLOW_ENEMY_RADIOUS, SLOW_ENEMY_COLOR));
	}
	
	public void createNormalEnemy(Point p, float angle){
		addSoldier(new Soldier(p, NORMAL_ENEMY_SPEED, angle, NORMAL_ENEMY_RADIOUS, NORMAL_ENEMY_COLOR));
	}
	
	public void createFastBigEnemy (Point p, float angle){
		addSoldier(new Soldier(p, FAST_BIG_ENEMY_SPEED, angle, FAST_BIG_ENEMY_RADIOUS, FAST_BIG_ENEMY_COLOR));
	}
	
	public void createFastSmallEnemy(Point p, float angle){
		addSoldier(new Soldier(p, FAST_SMALL_ENEMY_SPEED, angle, FAST_SMALL_ENEMY_RADIOUS, FAST_SMALL_ENEMY_COLOR));
	}
	
	//public void create
	
	// --- UTILITIES ---
	public static boolean crashed (Soldier s0, Soldier s1){
		return s0.crashed(s1);
	}
	
}
