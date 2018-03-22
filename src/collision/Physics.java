package collision;

import java.awt.geom.Area;

public final class Physics {
	// multiple this by an objects mass to figure force. Otherwise just use to
	// accelerate an object.
	public static final double GRAVITY = 98.1 * 5f; // m/s^2
	public static final double TERMINAL_VEL_X =554f;
	public static final double TERMINAL_VEL_Y = 254;
	public static final double FRICTION_COEFFICIENT = 1.299;;

	// GRAVITY / tick rate will give the desired acceleration. G/ 1000ms over
	// one second will give G

	public static double[][] getFaceVector(SquareObject a, Direction dir) {
		double[] x1 = new double[2], x2 = new double[2];
		switch (dir) {
		case EAST:
			x1 = rotatePoint(new double[] { a.rectangle.getMaxX(), a.rectangle.getMinY() },
					new double[] { a.rectangle.getCenterX() + 1, a.rectangle.getCenterY() - 1 }, a.rotation);
			x2 = rotatePoint(new double[] { a.rectangle.getMaxX(), a.rectangle.getMaxY() },
					new double[] { a.rectangle.getCenterX() + 1, a.rectangle.getCenterY() - 1 }, a.rotation);
			break;
		case NORTH:
			x1 = rotatePoint(new double[] { a.rectangle.getMinX(), a.rectangle.getMinY() },
					new double[] { a.rectangle.getCenterX() + 1, a.rectangle.getCenterY() - 1 }, a.rotation);
			x2 = rotatePoint(new double[] { a.rectangle.getMaxX(), a.rectangle.getMinY() },
					new double[] { a.rectangle.getCenterX() + 1, a.rectangle.getCenterY() - 1 }, a.rotation);
			break;
		case SOUTH:
			x1 = rotatePoint(new double[] { a.rectangle.getMaxX(), a.rectangle.getMaxY() },
					new double[] { a.rectangle.getCenterX() + 1, a.rectangle.getCenterY() - 1 }, a.rotation);
			x2 = rotatePoint(new double[] { a.rectangle.getMinX(), a.rectangle.getMaxY() },
					new double[] { a.rectangle.getCenterX() + 1, a.rectangle.getCenterY() - 1 }, a.rotation);
			break;
		case WEST:
			x1 = rotatePoint(new double[] { a.rectangle.getMinX(), a.rectangle.getMaxY() },
					new double[] { a.rectangle.getCenterX() + 1, a.rectangle.getCenterY() - 1 }, a.rotation);
			x2 = rotatePoint(new double[] { a.rectangle.getMinX(), a.rectangle.getMinY() },
					new double[] { a.rectangle.getCenterX() + 1, a.rectangle.getCenterY() - 1 }, a.rotation);
			break;

		}

		return new double[][] { x1, x2 };
	}

	public static final boolean intersectLines(double[] s1, double[] e1, double[] s2, double[] e2) {
		double det, gamma, lambda;
		// c -a * s - q - r - p * d - b
		det = (e1[0] - s1[0]) * (e2[1] - s2[1]) - (e2[0] - s2[0]) * (e1[1] - s1[1]);
		if (det == 0) {
			return false;
		} else {
			// s-q * r-a + p - r * s-b /det
			lambda = ((e2[1] - s2[1]) * (e2[0] - s1[0]) + (s2[0] - e2[0]) * (e2[1] - s1[1])) / det;
			// b-d * r-a + c-a * s-b /det
			gamma = ((s1[1] - e1[1]) * (e2[0] - s1[0]) + (e1[0] - s1[0]) * (e2[1] - s1[1])) / det;
			return (0 < lambda && lambda < 1) && (0 < gamma && gamma < 1);
		}

	}

	/**
	 * 
	 * @param a
	 *            A vector. in the form {x,y}
	 * @param b
	 *            Another Vector. in the form {x,y}
	 * @return The angle result from A <b>·</b> B
	 */
	public static final double getAngleBetweenVectors(double[] a, double b[]) {
		double theta = 0;
		// Dot product of angles.
		// Cos (Theta) = (A · B) / |A||B|
		double t = (a[0] * b[0] + a[1] * b[1])
				/ (Math.sqrt(a[0] * a[0] + a[1] * a[1]) + Math.sqrt(b[0] * b[0] + b[1] * b[1]));

		theta = Math.acos(t);

		return theta;
	}

	public enum Direction {
		NORTH, EAST, SOUTH, WEST;
	}

	private static double[] rotatePoint(double[] point, double[] center, double rad) {
		// System.out.println(point[1]);
		double[] newPoint = new double[2];
		newPoint[0] = center[0] + ((point[0] - center[0]) * Math.cos(rad)) - ((point[1] - center[1]) * Math.sin(rad));
		newPoint[1] = center[1] + ((point[0] - center[0]) * Math.sin(rad)) + ((point[1] - center[1]) * Math.cos(rad));
		// System.out.println(newPoint[1]);
		return newPoint;
	}

	public static double[] Normalize(double[] vector) {
		double distance = Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]);
		return vectorMult(vector, 1 / (1.05 * distance));
	}

	/**
	 * Does not return a normalized vector.
	 * 
	 * @param points
	 */
	public static double[] pointsToVector(double[][] points) {
		double x = points[1][0] - points[0][0];
		double y = points[1][1] - points[0][1];
		return new double[] { x, y };
	}

	public static double[] getNormal(double[] vector) {
		return new double[] { vector[1], -vector[0] };

	}

	public static double[] reflectVector(double[] d, double[] n) {
		return vectorAdd(d, vectorMult(n, -2 * dotProduct(d, n)));
	}

	/**
	 * Only dots two dimensional vectors
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double dotProduct(double[] a, double[] b) {
		return a[0] * b[0] + a[1] * b[1];
	}

	/**
	 * Only adds two dimensional vectors
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double[] vectorAdd(double[] a, double[] b) {
		return new double[] { a[0] + b[0], b[1] + a[1] };
	}

	public static double[] vectorMult(double[] a, double scalar) {
		return new double[] { scalar * a[0], scalar * a[1] };
	}
}
