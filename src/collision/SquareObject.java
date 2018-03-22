package collision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

import collision.Physics.Direction;

public class SquareObject {
	/***
	 * <b><i>JUST FOR TESTING</b></i>
	 */
	public static ArrayList<SquareObject> array = new ArrayList<>();
	public static ArrayList<SquareObject> dynamicArray = new ArrayList<>();
	public Rectangle2D rectangle;
	public Area rotated;
	public boolean isDynamic = true;
	public String name = "Tile";
	public double rotation = 0;
	public int id = 0;
	public JComponent canvasToDrawOn = null;
	double distanceFromPlayerOnCollide;
	Color squareColor = Color.GRAY;

	protected SquareObject(double x, double y, double w, double h, boolean isDynamic) {
		rectangle = new Rectangle2D.Double(x, y, w, h);
		if (rotated == null)
			rotated = new Area(rectangle);
		this.isDynamic = isDynamic;
		// JUST FOR TESTING
		if (this.isDynamic) {
			dynamicArray.add(this);
		}
		array.add(this);
		id = array.size() - 1;
		//

	}

	public SquareObject(double x, double y, double w, double h, String name, double radians, JComponent j) {

		this(x, y, w, h, false);
		canvasToDrawOn = j;
		this.name = name;
		this.rotation = radians;
		AffineTransform af = new AffineTransform();
		af.rotate(getRad(), rectangle.getBounds2D().getCenterX(), rectangle.getBounds2D().getCenterY());
		rotated = rotated.createTransformedArea(af);

	}

	// public SquareObject(double x, double y, double w, double h, String name)
	// {
	// this(x, y, w, h, false);
	// this.name = name;
	// }

	/***
	 * Barren constructor used for collision checks
	 * 
	 * @param x
	 * @param y
	 * @param width
	 */
	public SquareObject(double x, double y, double w, double h, double radians) {
		this.rotation = radians;
		rectangle = new Rectangle2D.Double(x, y, w, h);
		rotated = new Area(rectangle);
		AffineTransform af = new AffineTransform();
		af.rotate(getRad(), rectangle.getBounds2D().getCenterX(), rectangle.getBounds2D().getCenterY());
		rotated = rotated.createTransformedArea(af);

	}

	void updateRotation() {
		AffineTransform af = new AffineTransform();
		rotated = new Area(rectangle);
		af.rotate(getRad(), rectangle.getBounds2D().getCenterX(), rectangle.getBounds2D().getCenterY());
		rotated = rotated.createTransformedArea(af);
	}

	public static Area checkCollision(SquareObject one, SquareObject two) {
		Area a = one.rotated;
		a.intersect(two.rotated);
		return a;
		// return one.rotated.intersects(two.rotated.getBounds()) &&
		// two.rotated.intersects(one.rotated.getBounds());
	}

	@Override
	public String toString() {
		return "" + ((isDynamic) ? "D|" : "S|") + name + "(" + rectangle.getBounds2D().getCenterX() + ","
				+ rectangle.getBounds2D().getCenterY() + ")";
	}

	double getX() {
		return rectangle.getBounds2D().getX();
	}

	double getY() {
		return rectangle.getBounds2D().getY();
	}

	double getW() {
		return rectangle.getBounds2D().getWidth();
	}

	double getH() {
		return rectangle.getBounds2D().getHeight();
	}

	double getRad() {
		return rotation;
	}

	/**
	 * Overriddable function for collision event
	 * 
	 * @param hit
	 */
	void onCollide(DynamicObject actor, double[][] faceHit) {
		// TODO Auto-generated method stub
		if (actor instanceof PlayerObject) {
			synchronized (((PlayerObject) actor).itLock) {

				// I can't figure out how to make a workidang normal force
				// with the system I am using for collision. Maybe later?
				// Hopefully?
				// But I can add it to the force list for output reasons.

				double slope = Math.abs((faceHit[0][1] - faceHit[1][1]) / (faceHit[0][0] - faceHit[1][0]));
				if (Force.sumForces(actor.AppliedForces).getX() != 0 && !((PlayerObject) actor).isOnGround
						&& Double.isInfinite(slope)) {
					((PlayerObject) actor).rightWall = Force.sumForces(actor.AppliedForces).getX() > 0;
					actor.VEL_X = 0;
					actor.VEL_Y = 0;
					((PlayerObject) actor).isOnWall = true;
					return;
				}
				if ((actor.VEL_Y <= 0 || slope > 0.5) && !((PlayerObject) actor).isOnGround) {
					actor.VEL_X = 0;
					actor.VEL_Y = 0;

					return;
				}

				((PlayerObject) actor).isOnGround = true;
				actor.normalForce.setY(-1 * (Physics.GRAVITY));
				if (Math.abs(actor.VEL_X) > Physics.FRICTION_COEFFICIENT * 10) {
					synchronized (actor.frictionForce) {
						((PlayerObject) actor).frictionForce
								.setX(Math.signum(actor.VEL_X) * -Physics.FRICTION_COEFFICIENT * Physics.GRAVITY);
					}

					actor.VEL_Y = -1 * (Physics.GRAVITY / (1000f / 5));
					distanceFromPlayerOnCollide = (actor.rectangle.getMaxY() - this.rectangle.getMinY());
					if (!((PlayerObject) actor).lastHits.contains(this)) {
						((PlayerObject) actor).lastHits.add(this);
					}
				} else {
					// actor.VEL_X = 0;
					actor.VEL_Y = 0;
					// actor.ACC_X = 0;
					// actor.ACC_Y = 0;
					synchronized (actor.frictionForce) {
						((PlayerObject) actor).frictionForce.setX(0);
					}
				}
			}
		} else {
			actor.VEL_X = 0;
			actor.VEL_Y = 0;
		}
	}

	public void repaint(Graphics2D g) {
		g.setColor(squareColor);
		g.fill(rotated);
	}

	public void debugPaint(Graphics2D g) {

		g.setColor(Color.orange);
		g.drawString(id + "", (int) rectangle.getCenterX(), (int) rectangle.getCenterY());
	}

}
