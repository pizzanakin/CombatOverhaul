package net.libercraft.combatoverhaul.spell;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

public interface SpellBouncer extends SpellEntity {

	public abstract void onBounceEffect(World world, Location location);
	public abstract int retrieveBounces();
	public abstract void decreaseBounces();

	@Override // Returns true if it projectile has to get killed;
	public default boolean checkCollision(Location location, Vector vector) {

		move(location, vector);
		
		// Check for out of bounds
		if (location.getY() > 256 || location.getY() < 0) return true;
		
		// Check for blocks that the spell can pass through
		for (Material mat:setPassMaterial()) {
			if (location.getBlock().getType().equals(mat)) break;
			return !bounce(vector, location);
		}
		
		return false;
	}
	
	// Returns true if a bounce was executed;
	public default boolean bounce(Vector vector, Location location) {
		int bounces = retrieveBounces();
		if (bounces == 0) return false;
		
		// Find the surfaces that were hit
		double x = location.getX() - location.getBlockX();
		double y = location.getY() - location.getBlockY();
		double z = location.getZ() - location.getBlockZ();
		
		if (((y > x && y + x < 1) && (z > x && z + x < 1)) | ((y < x && y + x > 1) && (x > z && z + x > 1))) {
			trace("flip x");
			vector.setX(vector.getX() * -1);
		}
		
		if (((z > y && z + y < 1) && (x > y && x + y < 1)) | ((z < y && z + y > 1) && (y > x && x + y > 1))) {
			trace("flip y");
			vector.setY(vector.getY() * -1);
		}
		
		if (((x > z && x + z < 1) && (y > z && y + z < 1)) | ((x < z && x + z > 1) && (z > y && y + z > 1))) {
			trace("flip z");
			vector.setZ(vector.getZ() * -1);
		}
		
		// Call the function to execute the bounce effect in the final spell class
		onBounceEffect(location.getWorld(), location);
		decreaseBounces();
		
		return true;
	}
	
}
