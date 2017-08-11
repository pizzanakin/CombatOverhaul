package net.libercraft.combatoverhaul.ability;

import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TippedArrow;
import org.bukkit.metadata.FixedMetadataValue;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.player.Caster;

public interface ArrowAbility {
	
	public default Projectile spawnProjectile(Main plugin, Location loc, Caster caster) {

		// Determine the type of the arrow and spawn an entity
		Projectile proj = loc.getWorld().spawn(loc, caster.getArrowType());
		proj.setMetadata("arrow", new FixedMetadataValue(plugin, "arrow"));
		
		// Apply effect if necessary
		if (caster.getArrowType() == TippedArrow.class) ((TippedArrow) proj).setBasePotionData(caster.getArrowEffect());
		
		// Return the projectile
		return proj;
	}
}
