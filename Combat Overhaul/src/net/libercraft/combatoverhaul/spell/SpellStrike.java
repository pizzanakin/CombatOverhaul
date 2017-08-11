package net.libercraft.combatoverhaul.spell;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;

public interface SpellStrike extends SpellEntity {
	
	@Override
	public default void castBehaviour(Main plugin, Location location, Vector vector) {
		while (!checkCollision(location.clone(), vector)) {
			move(location, vector);
			
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
		onCollision(location, vector);
	}
}