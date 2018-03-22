package test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.swing.JFrame;
import javax.swing.JPanel;

import collision.DynamicObject;
import collision.Physics;
import collision.SquareObject;

public class Display {
	JFrame frame = new JFrame("TITLE");
	Panel p = new Panel();

	Display() {
		frame.setSize(800, 800);
		frame.add(p);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		displayThread.start();
	}

	private class Panel extends JPanel {

		@Override
		protected void paintComponent(Graphics gg) {
			Graphics2D g = (Graphics2D) gg;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			super.paintComponent(g);
			drawHectoMeterGrid(g);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setStroke(new BasicStroke((float) 1.5));
			for (SquareObject SO : SquareObject.array) {
				SO.repaint(g);
				SO.debugPaint(g);
			}

		}
	}

	void drawHectoMeterGrid(Graphics2D g) {
		g.setColor(new Color(0, 0, 0, 50));
		g.drawString("Hectometer Grid", 5, 10);
		for (int i = 0; i <= 2000; i += 99) {
			g.drawLine(i, 0, i, 2000);
			g.drawLine(0, i, 2000, i);
		}
	}

	Thread displayThread = new Thread(new Runnable() {
		public void run() {
			while (true) {
				p.repaint();

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	});
}
