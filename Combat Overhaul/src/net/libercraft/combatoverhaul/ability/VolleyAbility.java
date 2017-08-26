package net.libercraft.combatoverhaul.ability;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;

public class VolleyAbility extends BaseAbility implements ArrowAbility {

	public VolleyAbility(Main plugin, Player player) {
		onCast(plugin, player);
		caster.hasActivatedVolley = true;
	}
	
	public void activate(Vector vector) {
		
		// Create 5 new arrows
		for (int i = 0; i < 5; i++) {
			int index = i - 2;
			
			// Create a new projectile to be launched
			Location eyeLoc = caster.getPlayer().getEyeLocation().clone();
			Vector eyeVec = caster.getPlayer().getEyeLocation().getDirection().clone().normalize();
			Location spawnLoc = eyeLoc.clone().add(eyeVec);
			
			
			Projectile proj = spawnProjectile(plugin, spawnLoc, caster);
			
			// Create a crossed vector of random length that will be added do the new projectile's vector
			Vector addVector = vector.clone().crossProduct(new Vector(0, 1, 0));
			addVector.normalize();
			addVector.multiply((Math.random() * 0.3) * index * 1);
			
			// Give the arrow the correct direction
			proj.setVelocity(vector.clone().add(addVector));
		}
		
		caster.getPlayer().setCooldown(Material.BOW, 25);
		caster.hasActivatedVolley = false;
		if (caster.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
		caster.removeArrow();
		caster.decreaseBowDurability();
	}
}
