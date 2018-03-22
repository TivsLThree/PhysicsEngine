package collision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;

public class PlayerObject extends DynamicObject {
	Object itLock = new Object();
	public boolean isOnGround = false;
	public boolean isOnWall = false;
	public boolean rightWall = false;
	ArrayList<SquareObject> lastHits = new ArrayList<SquareObject>();

	public PlayerObject(double x, double y, double w, double h, String name, double radians, JComponent j) {
		super(x, y, w, h, name, radians, j);
		// TODO Auto-generated constructor stub
	}

	double NORMAL_FORCE = 0;

	@Override
	void linearPhysics() {
		if (!isOnGround)
			synchronized (this.normalForce) {
				normalForce.setY(-0);
			}
		synchronized (itLock) {
			Iterator<SquareObject> sit = lastHits.iterator();
			while (sit.hasNext()) {
				SquareObject SO = sit.next();
				double dis = (this.rectangle.getMaxY() - SO.rectangle.getMinY());
				if (Math.abs(dis) > 0.1) {

					sit.remove();
				}
			}
		}
		this.squareColor = (isOnWall) ? Color.orange : Color.green;
		this.squareColor = (isOnGround) ? Color.red : this.squareColor;
		if (lastHits.isEmpty()) {
			isOnGround = false;
			synchronized (this.frictionForce) {
				frictionForce.setX(0);
			}

		}

		super.linearPhysics();
	}

	@Override
	public void repaint(Graphics2D g) {
		super.repaint(g);
		g.setColor(Color.MAGENTA);
		for(Force f: AppliedForces){
			f.repaint(g);
		}
		Force sum = Force.sumForces(AppliedForces);
		g.drawLine((int) getX(), (int) getY(), (int) (getX() + sum.getX() * 1), (int) (getY() + sum.getY()));
	}
}
