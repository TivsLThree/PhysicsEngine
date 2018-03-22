package collision;

import java.awt.Color;

import javax.swing.JComponent;

public class BounceObject extends SquareObject {

	public BounceObject(double x, double y, double w, double h, String name, double radians, JComponent j) {
		super(x, y, w, h, name, radians, j);
		this.squareColor = Color.orange;
		// TODO Auto-generated constructor stub
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
}