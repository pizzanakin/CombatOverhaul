package net.libercraft.combatoverhaul.spell;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.managers.Caster;

public interface SpellStrike extends SpellEntity {
	
	@Override
	public default void castBehaviour(Main plugin, Location location, Vector vector) {
		while (!checkCollision(location.clone(), vector)) {
			move(location, vector, 16);
			
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