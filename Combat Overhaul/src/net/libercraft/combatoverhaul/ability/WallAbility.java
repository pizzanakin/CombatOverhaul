package net.libercraft.combatoverhaul.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;

public class WallAbility extends BaseAbility implements Tracer {

	public WallAbility(Main plugin, Player player) {
		onCast(plugin, player);
		
		// Determine the location of the middle of the wall, 4 blocks away from the player
		Vector vector = caster.getPlayer().getEyeLocation().getDirection().clone();
		Location loc = caster.getPlayer().getLocation().add(vector.multiply(4));
		
		// Determine the location where to start the wall
		Vector otherVector = vector.clone().crossProduct(new Vector(0, 1, 0));
		otherVector.normalize().multiply(3);
		Location wallLoc = loc.clone().add(otherVector);
		
		// Make a vector crossed to the player's view line that will become the wall
		Vector crossVector = vector.clone().crossProduct(new Vector(0, 1, 0));
		Vector heightVector = crossVector.clone().crossProduct(vector).normalize();
		
		// Set the location to the end of the wall
		crossVector.normalize();
		crossVector.multiply(-1);
		for (int j = 1; j < 4; j++) {
			for (int i = 1; i < 4; i++) {
				
				// Calculate the location for the blocks of the wall
				Location block = wallLoc.clone();
				block.add(heightVector.clone().multiply(1 * j));
				block.add(crossVector.clone().multiply(1 + i));

				ArmorStand stand = block.getWorld().spawn(block, ArmorStand.class);
				stand.setGravity(false);
			}
		}
		
		// Remove the wall again after the timer is over
		new BukkitRunnable() {
			@Override public void run() {
				for (int j = 1; j < 4; j++) {
					for (int i = 1; i < 4; i++) {
						Location block = wallLoc.clone();
						block.add(heightVector.clone().multiply(1 * j));
						block.add(crossVector.clone().multiply(1 + i));
						if (block.getBlock().getType().equals(Material.WOOD)) block.getBlock().setType(Material.AIR);
					}
				}
			}
			
		}.runTaskLater(plugin, 15);
	}
}
