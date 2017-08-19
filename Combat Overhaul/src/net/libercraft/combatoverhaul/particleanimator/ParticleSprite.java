package net.libercraft.combatoverhaul.particleanimator;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public enum ParticleSprite {
	FIREBALL,
	ORANGE_BALL,
	CYAN_BALL,
	SINGLE_BLUE,
	SINGLE_CYAN,
	SINGLE_FLAME,
	SINGLE_SMOKE,
	SINGLE_WAKE;
	
	public void summon(Location location) {
		for (Player player:location.getWorld().getPlayers()) {
			if (location.distance(player.getLocation()) > 80) continue;
			switch (this) {
			case ORANGE_BALL:
				for (int i = 0; i < 5; i++) {
					double x = location.getX();
					double y = location.getY();
					double z = location.getZ();
					player.spawnParticle(Particle.REDSTONE, randomise(x, 0.3), randomise(y, 0.3), randomise(z, 0.5), 0, 1, 0.32, 0.001, 1);
				}
				break;
			case CYAN_BALL:
				for (int i = 0; i < 5; i++) {
					double x = location.getX();
					double y = location.getY();
					double z = location.getZ();
					player.spawnParticle(Particle.REDSTONE, randomise(x, 0.3), randomise(y, 0.3), randomise(z, 0.5), 0, 0.001, 1.0, 1.0, 1);
				}
				break;
			case FIREBALL:
				player.spawnParticle(Particle.FLAME, location, 5, 0.05, 0.05, 0.05, 0.0);
				break;
			case SINGLE_BLUE:
				player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 0.5, 1.0, 1);
				break;
			case SINGLE_CYAN:
				player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 1.0, 1.0, 1);
				break;
			case SINGLE_FLAME:
				player.spawnParticle(Particle.FLAME, location, 1, 0.0, 0.0, 0.0, 0.0);
			case SINGLE_SMOKE:
				player.spawnParticle(Particle.SMOKE_NORMAL, location, 1, 0.0, 0.0, 0.0, 0.0);
				break;
			case SINGLE_WAKE:
				player.spawnParticle(Particle.WATER_WAKE, location.getX(), location.getY() + 0.15, location.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
				break;
			default:
				break;
			
			}
		}
	}
	
	public static void summonDust(Location location, int red, int green, int blue) {
		for (Player player:location.getWorld().getPlayers()) {
			if (location.distance(player.getLocation()) > 80) continue;
			player.spawnParticle(Particle.REDSTONE, location, 0, red/255, green/255, blue/255, 1);
		}
	}
	
	private double randomise(double input, double factor) {
		return (Math.random() * factor) + input - (factor / 2);
	}
}
