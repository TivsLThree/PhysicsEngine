package input;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import test.Main;

public class InputHandler {
	public InputHandler(JComponent j) {
		InputThread.start();
		j.getInputMap().put(KeyStroke.getKeyStroke("released A"), "RELEASED_A");
		j.getInputMap().put(KeyStroke.getKeyStroke("released D"), "RELEASED_D");
		j.getInputMap().put(KeyStroke.getKeyStroke('a'), "LEFT");
		j.getInputMap().put(KeyStroke.getKeyStroke('d'), "RIGHT");
		j.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "JUMP");
		j.getActionMap().put("JUMP", new MovementAction("JUMP", Main.getPlayer()));
		j.getActionMap().put("LEFT", new MovementAction("LEFT", Main.getPlayer()));
		j.getActionMap().put("RELEASED_A", new MovementAction("RLEFT", Main.getPlayer()));
		j.getActionMap().put("RELEASED_D", new MovementAction("RRIGHT", Main.getPlayer()));
		j.getActionMap().put("RIGHT", new MovementAction("RIGHT", Main.getPlayer()));
	}

	Thread InputThread = new Thread(() -> {
		while (true) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	});
}
