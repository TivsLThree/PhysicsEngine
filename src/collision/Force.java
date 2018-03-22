package collision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import test.Main;

public class Force {
	private double x, y, magnitude, theta;
	private static DynamicObject object;
	private long milliTime = -1;
	private boolean dead = false;
	String name;
	Color c;

	// Ignoring mass right now as I am assuming all objects in my system have a
	// mass of 1kg;
	// B^O
	public Force(double x, double y, DynamicObject object, long milliTime, String name) {
		// if (x == 0 && y == 0) {
		// dead = true;
		// return;
		// }
		this.milliTime = milliTime;
		this.x = x;
		this.y = y;
		this.object = object;
		this.name = name;
		c = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
		synchronized (object.forceLock) {
			if (!object.AppliedForces.contains(this)) {
				object.AppliedForces.add(this);
			}
		}
		if (milliTime != -1) {
			ForceDecayThread.start();
		}

	}

	/**
	 * Empty for reading use only;
	 */
	private Force(double x, double y) {
		this.x = x;
		this.y = y;
	}

	Thread ForceDecayThread = new Thread(() -> {
		try {
			while (!Main.boot) {
				Thread.sleep(10);
			}
			// Thread.sleep() is inaccurate. But probably accurate enough for
			// now... I guess.
			Thread.sleep(milliTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized (object.forceLock) {
			if (object.AppliedForces.contains(this)) {
				object.AppliedForces.remove(this);
				dead = true;
			}
		}
	});

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public DynamicObject getObject() {
		return object;
	}

	public void setObject(DynamicObject object) {
		this.object = object;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public static Force sumForces(ArrayList<Force> AppliedForces) {
		Force net = new Force(0, 0);

		for (Force f : AppliedForces) {
			if (f.isDead()) {
				continue;
			}
			net.setX(net.getX() + f.getX());
			net.setY(net.getY() + f.getY());
		}

		return net;
	}

	@Override
	public String toString() {

		return this.name + "[" + x + "," + y + "]";
	}

	public void repaint(Graphics2D g) {
		Color v = g.getColor();
		g.setColor(c);
		g.drawLine((int) object.getX(), (int) object.getY(), (int) (object.getX() + getX() * 1),
				(int) (object.getY() + getY()));
		g.setColor(v);
	}
}
