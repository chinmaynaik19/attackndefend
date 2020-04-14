package soldier;

import java.awt.Color;
import java.awt.Point;


public class Soldier {
	
	private float x, y, speed, radious, angle;
	Color color;
		
	public Soldier ( float x, float y, float s, float angle, float rad, Color c){
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.radious = rad;
		this.speed = s;
		this.color = c;
	}
	
	public Soldier ( Point p, float s, float angle, float rad, Color c){
		this ((float)p.getX(), (float)p.getY(), s, angle, rad, c);
	}
	
	public boolean crashed ( Point p , float radious){
		boolean ret = false;
		
		if ( this.distance(p) < (this.getRadious() + radious) ) ret = true;
		
		return ret;
	}
	
	public boolean crashed (Soldier s){
		return crashed(new Point ((int)s.getX(), (int)s.getY()), s.getRadious());
	}
	
	private float distance( Point s ){
		return  (float) Math.sqrt( Math.pow(this.getX() - s.getX(), 2) + Math.pow(this.getY() - s.getY(), 2) );
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getRadious() {
		return radious;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public Color getColor() {
		return color;
	}
	
}
