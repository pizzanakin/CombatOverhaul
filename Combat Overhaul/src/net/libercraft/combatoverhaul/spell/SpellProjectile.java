package net.libercraft.combatoverhaul.spell;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.managers.Caster;

public interface SpellProjectile extends SpellEntity {

	public abstract double retrieveSpeed();
	
	@Override
	public default void move(Location location, Vector vector, int factor) {
		double speed = retrieveSpeed();
		
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		
		x = x + (vector.getX() * speed / factor);
		y = y + (vector.getY() * speed / factor);
		z = z + (vector.getZ() * speed / factor);
		
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
						move(location, vector, 32);
					}
					else {
						onCollision(location, vector);
						this.cancel();
						return;
					}
					
					Caster caster = retrieveCaster();
					double damage = retrieveDamage();
					
					for (LivingEntity target:getTargets(location, 1)) {
						if (target.hasMetadata("cooldown")) continue;
						if (retrieveFlaming()) target.setFireTicks(1);
						target.damage(damage, caster.getPlayer());
						target.setMetadata("cooldown", new FixedMetadataValue(plugin, "cooldown"));
						new BukkitRunnable() {
							@Override public void run() {
								if (target.hasMetadata("cooldown")) target.removeMetadata("cooldown", plugin);
							}
						}.runTaskLater(plugin, 3);
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
