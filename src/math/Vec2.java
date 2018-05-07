package math;

public class Vec2 {
	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	public Vec2(Vec2 vec2) {
		this.x = vec2.x;
		this.y = vec2.y;
	}
	public float x;
	public float y;
	@Override 
	public String toString() {
		return "(" + x +","+y+")";
	}
}
