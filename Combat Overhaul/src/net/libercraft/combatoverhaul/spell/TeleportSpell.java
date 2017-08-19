package net.libercraft.combatoverhaul.spell;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.libercraft.combatoverhaul.Main;

public class TeleportSpell extends BaseSpell {

	Location oldLoc;
	Location newLoc;
	
	public TeleportSpell(Main plugin, Player player, int cost) {
		onCast(plugin, player, cost);

		player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, caster.getPlayer().getLocation(), 20, 0.2, 0.2, 0.2, 0.05);
		Location loc = caster.getPlayer().getLocation().clone();
		double xDiff = (Math.random() * 10) - 5;
		double zDiff = (Math.random() * 10) - 5;
		
		loc.setX(loc.getX() + xDiff);
		loc.setZ(loc.getZ() + zDiff);
		while (!loc.getBlock().getType().equals(Material.AIR)) {
			loc.setY(loc.getY() + 1);
		}
		caster.getPlayer().teleport(loc);
		player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, caster.getPlayer().getLocation(), 20, 0.2, 0.2, 0.2, 0.05);
		
		cast = false;
	}

	public static void handEffect(Player player, Location location) {
		player.spawnParticle(Particle.REDSTONE, location, 0, 0.05, 0.47, 0.34, 1.0);
	}

	@Override
	public void onCastEffect(World world, Location location) {
		// TODO Auto-generated method stub
		
	}
}