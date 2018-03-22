package collision;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class EventHandler {
	public static ArrayList<CollisionEvent> QueuedEvents = new ArrayList<>();
	int colCount = 0;
	int delay;
	Thread EventThread = new Thread(() -> {
		while (true) {

			// If there are no dynamic objects within the system no checks will
			// be done. No objects moving = no collisions.
			for (SquareObject dyn : SquareObject.dynamicArray) {
				for (SquareObject all : SquareObject.array) {
					if (dyn == all)
						continue;
					// The potential event to check
					CollisionEvent temp = new CollisionEvent(dyn, all, colCount, true);
					if (!SquareObject.checkCollision(temp.A, temp.B).isEmpty()) {
						if (QueuedEvents.isEmpty()) {
							QueuedEvents.add(temp);
							continue;
						}
						for (CollisionEvent C : QueuedEvents) {
							if (!C.equals(temp) && !C.equals(temp.conjugate))
								QueuedEvents.add(temp);
							colCount++;
							break;
						}

					}
				}
			}
			// System.out.println(QueuedEvents);
			attemptSleep();
		}
	});

	public void start(int delay) {
		this.delay = delay;
		EventThread.start();
	}

	private void attemptSleep() {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException E) {
			E.printStackTrace();
		}
	}

	public static CollisionEvent contains(SquareObject O) {
		for (CollisionEvent c : QueuedEvents) {
			if (c.A == O || c.B == O) {
				c.checked = true;
				return c;
			}
		}
		return null;
	}

	public class CollisionEvent {
		SquareObject A, B;
		CollisionEvent conjugate;
		boolean checked = false;
		int id;
		/***
		 * Might use this at somepoint to have collisions pile up in the queue
		 * without being checked
		 */
		boolean dupable = false;

		public CollisionEvent(SquareObject A, SquareObject B, int id, boolean makeConjugate) {
			this.A = A;
			this.B = B;
			this.id = id;
			if (makeConjugate)
				conjugate = new CollisionEvent(B, A, id, false);
		}

		public boolean equals(CollisionEvent event) {
			return this.A == event.A && this.B == event.B;
		}

		@Override
		public String toString() {
			return "{" + A + " -> " + B + "[" + this.id + "]}";

		}

	}

}
