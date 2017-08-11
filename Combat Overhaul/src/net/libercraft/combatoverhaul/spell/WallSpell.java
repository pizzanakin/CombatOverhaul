package net.libercraft.combatoverhaul.spell;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;

public class WallSpell extends BaseSpell implements Tracer {

	public WallSpell(Main plugin, Player player) {
		onCast(plugin, player);
		
		cost = 1;
		
		// Determine the location of the middle of the wall, 4 blocks away from the player
		Vector vector = caster.getPlayer().getEyeLocation().getDirection().clone().setY(0);
		Location loc = caster.getPlayer().getLocation().add(vector.multiply(4));
		
		// Get the material for the wall
		Location matLoc = loc.clone();
		matLoc.setY(matLoc.getY() - 1);
		Material blockType = matLoc.getBlock().getType();
		
		// Check if the wall material is a type that isn't allowed
		if (
				blockType.equals(Material.WATER)|
				blockType.equals(Material.STATIONARY_WATER)|
				blockType.equals(Material.LAVA)|
				blockType.equals(Material.STATIONARY_LAVA)|
				blockType.equals(Material.AIR)|
				blockType.equals(Material.LONG_GRASS)
				) return;
		
		// Determine the location where to start the wall
		Vector otherVector = vector.clone().crossProduct(new Vector(0, 1, 0));
		otherVector.normalize().multiply(3);
		loc.add(otherVector);
		
		// Make a vector crossed to the player's view line that will become the wall
		Vector crossVector = vector.clone().crossProduct(new Vector(0, 1, 0));
		
		// Set the location to the end of the wall
		crossVector.normalize();
		crossVector.multiply(-1);
		for (int y = 0; y < 3; y++) {
			for (int i = 0; i < 5; i++) {
				
				// Calculate the location for the blocks of the wall
				Location block = loc.clone();
				block.setY(block.getY() + y);
				block.add(crossVector.clone().multiply(1 + i));
				Material locationType = block.getBlock().getType();
				
				// Check for blocks that can be replaced by the wall
				if (
						locationType.equals(Material.AIR)|
						locationType.equals(Material.LONG_GRASS)|
						locationType.equals(Material.LAVA)|
						locationType.equals(Material.STATIONARY_LAVA)|
						locationType.equals(Material.WATER)|
						locationType.equals(Material.STATIONARY_WATER)
						) block.getBlock().setType(blockType);
			}
		}
		
		// Remove the wall again after the timer is over
		new BukkitRunnable() {

			@Override
			public void run() {
				for (int y = 0; y < 3; y++) {
					for (int i = 0; i < 5; i++) {
						Location block = loc.clone();
						block.setY(block.getY() + y);
						block.add(crossVector.clone().multiply(1 + i));
						if (block.getBlock().getType().equals(blockType)) block.getBlock().setType(Material.AIR);
					}
				}
			}
			
		}.runTaskLater(plugin, 15);
	}
	
	public static void handEffect(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCastEffect(World world, Location location) {
		// TODO Auto-generated method stub
		
	}
}
