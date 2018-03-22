package input;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import collision.Force;
import collision.PlayerObject;
import test.Main;

public class MovementAction extends AbstractAction {
	String dir;
	PlayerObject player;

	public MovementAction(String dir, PlayerObject p) {
		this.dir = dir;
		this.player = p;
	}

	double horizontalAccel = 1600;

	@Override
	public void actionPerformed(ActionEvent e) {

		// Main.getPlayer().VEL_Y = -1 * 100;
		// JUMP
		if (dir == "JUMP") {
			if (player.isOnGround) {
				if (!player.contains("Jump")) {
					new Force(0, -5300, player, 100, "Jump");
				}
			} else if (player.isOnWall) {
				//System.out.println(player.rightWall);
				if (!player.contains("Wall Jump")) {
					player.VEL_X = 0;
					new Force(((player.rightWall) ? -1 : 1) * 3000, -3300, player, 100, "Jump");
					player.isOnWall = false;
				}
			}
		}
		if (player.isOnGround) {
			if (dir == "LEFT") {
				player.leftMovementForce.setX(-horizontalAccel);
			}
			if (dir == "RIGHT") {
				player.rightMovementForce.setX(horizontalAccel);
			}

		} else {
			if (dir == "LEFT") {
				player.leftMovementForce.setX(-horizontalAccel * .1);
			}
			if (dir == "RIGHT") {
				player.rightMovementForce.setX(horizontalAccel * .1);
			}
		}
		if (dir == "RLEFT") {
			player.leftMovementForce.setX(0);
		}
		if (dir == "RRIGHT") {
			player.rightMovementForce.setX(0);
		}
	}

}
