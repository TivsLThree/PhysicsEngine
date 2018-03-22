package test;

import collision.BounceObject;
import collision.DynamicObject;
import collision.PlayerObject;
import collision.SquareObject;
import input.InputHandler;

public class Main {
	public static DynamicObject player;

	public static PlayerObject getPlayer() {
		return (PlayerObject) player;
	}

	public static boolean boot = false;

	public static void main(String[] args) {
		Display dis = new Display();
		new SquareObject(50, 100, 30, 520, "Rotate", 0, dis.p);
		new SquareObject(200, 00, 30, 500, "Rotate", 0, dis.p);
		new SquareObject(200, 440, 300, 60, "Rotate", 0, dis.p);
		new SquareObject(50, 590, 6200, 30, "Rotate", 0, dis.p);
		// new BounceObject(50, 290, 620, 30, "Rotate", 0, dis.p);
		// SquareObject t0 = new SquareObject(305, 300, 32);
		// SquareObject o = new SquareObject(275, 300, 32);
		// DynamicObject two = new DynamicObject(500, 520, 32, 32, "Player", 0);
		// DynamicObject six = new PlayerObject(300, 350, 32, 32, "Player", .25,
		// dis.p);
		player = new PlayerObject(400, 400, 32, 32, "Player1", 0, dis.p);
		// two.isTrigger = true;
		InputHandler input = new InputHandler(dis.p);
		for (int i = 0; i < 0; i++) {

			SquareObject t = new DynamicObject(Math.random() * 600d, Math.random() * 600d, 32, 32, "Player",
					Math.PI / 4, dis.p);
		}
		System.out.println(System.getProperty("java.version"));
		// EventHandler Handler = new EventHandler();
		// Handler.start(100);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boot = true;
	}

}
