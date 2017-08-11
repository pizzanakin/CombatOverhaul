package net.libercraft.combatoverhaul.ability;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;

public class ArrowRain extends BaseAbility implements ArrowAbility {

	public ArrowRain(Main plugin, Player player) {
		onCast(plugin, player);
		
		// Retrieve the location and direction of the player
		Vector vector = caster.getPlayer().getEyeLocation().getDirection();
		Location rainLoc = caster.getPlayer().getEyeLocation();
		
		// Find the location the player is looking at
		while (
				(rainLoc.getBlock().getType().equals(Material.AIR)|
				rainLoc.getBlock().getType().equals(Material.STATIONARY_WATER))&&
				rainLoc.distance(caster.getPlayer().getLocation()) < 50
				) {
			rainLoc.setX(rainLoc.getX() + vector.getX());
			rainLoc.setY(rainLoc.getY() + vector.getY());
			rainLoc.setZ(rainLoc.getZ() + vector.getZ());
		}
		
		// Spawn the arrows 10 blocks above the found location
		rainLoc.setY(rainLoc.getY() + 10);
		for (int i = 0; i<5; i++) {
			Projectile proj = spawnProjectile(plugin, rainLoc, caster);
			
			// Randomise the direction the arrow falls to
			proj.setVelocity(new Vector((Math.random() * 0.3) - 0.15, -1, (Math.random() * 0.3) - 0.15));
		}
		
		caster.getPlayer().setCooldown(Material.BOW, 25);
		if (caster.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
		caster.removeArrow();
		caster.decreaseBowDurability();
	}
}
