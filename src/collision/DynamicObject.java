package collision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;

import collision.EventHandler.CollisionEvent;
import collision.Physics.Direction;
import test.Main;

public class DynamicObject extends SquareObject {
	public Object lock = new Object();
	public Object forceLock = new Object();
	public boolean isTrigger = false;
	private boolean cp = !false;
	public double[][] p = new double[2][2];
	ArrayList<ColoredPoint> points = new ArrayList<>();
	ArrayList<Force> AppliedForces = new ArrayList<>();
	Force frictionForce;
	Force normalForce;
	public Force rightMovementForce;
	public Force leftMovementForce;
	public Force centripetal;
	public Force gravity;

	public DynamicObject(double x, double y, double w, double h, String name, double radians, JComponent j) {
		super(x, y, w, h, name, radians, j);
		DynamicObjectThread.setName(this.name + " Thread");
		DynamicObjectThread.start();
	
	}

	public class ColoredPoint {
		double[] position = new double[2];
		Color color = new Color(0);

		public ColoredPoint(double[] pos, Color c) {
			this.color = c;
			this.position = pos;
		}
	}

	private double tickRate = 5;
	public double VEL_X, VEL_Y;
	private double ACC_X, ACC_Y = 0;
	double initialXVel = -120;
	Thread DynamicObjectThread = new Thread(() -> {
		VEL_X = initialXVel;
		synchronized (forceLock) {
			centripetal = new Force(0, 0, this, -1, "Centripetal");
			rightMovementForce = new Force(0, 0, this, -1, "Right Movement");
			leftMovementForce = new Force(0, 0, this, -1, "Left Movement");
			frictionForce = new Force(0, 0, this, -1, "Friction");
			normalForce = new Force(0, 0, this, -1, "Normal");
			normalForce.setDead(true);
			gravity = new Force(0, Physics.GRAVITY, this, -1, "Gravity");
			//new Force(-50, -20, this, 500, "Timed");

		}
		while (true) {
			while (!Main.boot) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// System.out.println(this.rectangle.getMinY());
			updateRotation();
			if (cp) {
				// VEL_Y += (double) (Physics.GRAVITY / (1000f / tickRate));
				linearPhysics();
			} else {
				centripetalPhysics();
			}
			attemptSleep();
		}

	});

	private double angleBetweenPoints(double[] p1, double[] p2) {
		return Math.atan2(p1[1] - p2[1], p1[0] - p2[0]);
	}

	// TODO: Make this scalable;
	public double[] point = new double[] { 400d, 300d };

	// TODO: Update centripetal physics to use the Force system.
	void centripetalPhysics() {
		// a = v^2/r
		//
		gravity.setY(0);
		double a = initialXVel * initialXVel / 100;
		double theta = angleBetweenPoints(point, new double[] { this.getX(), this.getY() });
		centripetal.setX(a * Math.cos(theta));
		centripetal.setY(a * Math.sin(theta));
		linearPhysics();
	}

	double x1, x2, y1, y2;
	public Area temp = new Area();
	boolean col = false;

	void linearPhysics() {
		col = false;
		ACC_X = 0;
		ACC_Y = 0;
		// System.out.println(AppliedForces);
		Force netAppliedForce = Force.sumForces(AppliedForces);
		ACC_X = netAppliedForce.getX();
		ACC_Y = netAppliedForce.getY();
		// Apply vertical acceleration
		VEL_Y += (double) (ACC_Y / (1000f / tickRate));
		// Apply horizontal acceleration
		VEL_X += (double) (ACC_X / (1000f / tickRate));
		// Clamp vertical velocity
		 VEL_Y = Math.signum(VEL_Y) * Math.min(Math.abs(VEL_Y),
		 Math.abs(Physics.TERMINAL_VEL_Y));
		 VEL_X = Math.signum(VEL_X) * Math.min(Math.abs(VEL_X),
		 Math.abs(Physics.TERMINAL_VEL_X));
		 if (this instanceof PlayerObject) {
		 VEL_X = (!((PlayerObject) this).isOnGround)
		 ? Math.signum(VEL_X) * Math.min(Math.abs(VEL_X),
		 Math.abs(Physics.TERMINAL_VEL_X * 0.4)) : VEL_X;
		 }
		synchronized (lock) {
			if (points.size() >= 2000) {
				points.remove(0);
				points.trimToSize();
			}
			points.add(new ColoredPoint(
					new double[] { (double) this.rectangle.getMinX(), (double) this.rectangle.getMinY() },
					this.squareColor.darker()));

		}

		// Attempt to move
		double newX = (double) (VEL_X / (1000f / tickRate));
		double newY = (double) (VEL_Y / (1000f / tickRate));
		for (SquareObject SO : SquareObject.array) {
			if (SO.id == this.id) {
				continue;
			}
			Area a;

			a = SquareObject.checkCollision(
					new SquareObject(this.getX() + newX, this.getY() + newY, this.getW(), this.getH(), this.getRad()),
					SO);

			if (!a.isEmpty()) {
				temp = a;
				double x = a.getBounds2D().getCenterX();
				double y = a.getBounds2D().getCenterY();
				double x1 = x + newX * 5;
				double y1 = y + newY * 5;// *4*slope
				double x2 = x - newX * 5;
				double y2 = y - newY * 5;// *slope
				for (Direction d : Physics.Direction.values()) {
					double[][] hitVec = Physics.getFaceVector(SO, d);
					boolean b = Physics.intersectLines(new double[] { x1, y1 }, new double[] { x2, y2 }, hitVec[0],
							hitVec[1]);
					if (b) {
						p = hitVec;
						SO.onCollide(this, hitVec);
						// if (SO.name == "Player")
						// this.onCollide((DynamicObject) SO, hitVec);
					}
				}

				col = true;

			}
		}
		// Re-calculate the velocity and newX with friction taken into account;

		// newY = (double) (VEL_Y / (1000f / tickRate));
		if (!col || isTrigger) {

			this.rectangle = new Rectangle2D.Double(getX() + newX, getY() + newY, getW(), getH());
		}

	}

	public boolean contains(String name) {
		for (Force f : AppliedForces) {
			if (f.name == name) {
				return true;
			}
		}
		return false;
	}

	@Override
	void onCollide(DynamicObject actor, double[][] faceHit) {
		double gamma = Math
				.toDegrees(Physics.getAngleBetweenVectors(Physics.Normalize(new double[] { actor.VEL_X, actor.VEL_Y }),
						Physics.Normalize(Physics.pointsToVector(faceHit))));
		double[] d = Physics.reflectVector(new double[] { actor.VEL_X, actor.VEL_Y },
				Physics.Normalize(Physics.getNormal(Physics.pointsToVector(faceHit))));
		actor.VEL_X = d[0];
		actor.VEL_Y = d[1];

	}

	private void attemptSleep() {
		try {
			Thread.sleep((long) tickRate);
		} catch (InterruptedException E) {
			E.printStackTrace();
		}
	}

	@Override
	public void repaint(Graphics2D g) {

		super.repaint(g);
		DynamicObject s = this;
		Color c = g.getColor();
		g.setColor(Color.blue);
		// g.drawOval((int) s.point[0], (int) s.point[1], 1, 1);
		g.fillOval((int) s.p[0][0], (int) s.p[0][1], 4, 4);
		g.fillOval((int) s.p[1][0], (int) s.p[1][1], 4, 4);
		double[] ds = new double[] { s.p[1][0] - s.p[0][0], s.p[1][1] - s.p[0][1] };
		g.drawLine((int) s.p[0][0], (int) s.p[0][1], (int) (ds[0] + s.p[0][0]), (int) (ds[1] + s.p[0][1]));
		g.drawLine((int) getX(), (int) getY(), (int) (getX() + VEL_X), (int) (getY() + VEL_Y));
		g.setColor(Color.GREEN);
		g.fill(s.temp);
		g.setColor(Color.MAGENTA);
		g.drawLine((int) getX(), (int) getY(), (int) (getX() + ACC_X), (int) (getY() + ACC_Y));
		g.setColor(c);
		synchronized (s.lock) {
			if (s.points.size() >= 2) {
				double[] lastPos = null;
				for (ColoredPoint d : s.points) {
					g.setColor(d.color);
					if (lastPos == null) {
						lastPos = d.position;
						continue;
					}
					g.drawLine((int) Math.round(d.position[0]), (int) Math.round(d.position[1]),
							(int) Math.round(lastPos[0]), (int) Math.round(lastPos[1]));
					lastPos = d.position;
				}
			}
		}
	}
}
