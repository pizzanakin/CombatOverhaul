package net.libercraft.combatoverhaul.spell;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;

public interface SpellProjectile extends SpellEntity {

	public abstract double retrieveSpeed();
	
	@Override
	public default void move(Location location, Vector vector) {
		double speed = retrieveSpeed();
		
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		
		x = x + (vector.getX() * speed / 32);
		y = y + (vector.getY() * speed / 32);
		z = z + (vector.getZ() * speed / 32);
		
		location.setX(x);
		location.setY(y);
		location.setZ(z);
	}
	
	@Override
	public default void castBehaviour(Main plugin, Location location, Vector vector) {
		new BukkitRunnable() {
			@Override public void run() {
				for (int i = 0; i < 32; i++) {
					if (!checkCollision(location.clone(), vector)) {
						move(location, vector);
					}
					else {
						onCollision(location, vector);
						this.cancel();
						return;
					}
					
					if (i==0|i==16) {
						// Remove the safety tag from all affected entities
						for (LivingEntity target:getTargets(location, 2)) {
							target.setMetadata("safe", new FixedMetadataValue(plugin, "safe"));
						}

						// Execute effect for flying
						onFlightEffect(location.getWorld(), location, vector);
						
						// Remove the safety tag from all affected entities
						for (LivingEntity target:getTargets(location, 2)) {
							if (target.hasMetadata("safe")) target.removeMetadata("safe", plugin);
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
}
