package net.libercraft.combatoverhaul.particleanimator;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public enum ParticleSprite {
	FIRE_BALL,
	ORANGE_BALL,
	CYAN_BALL,
	CLOUD_BALL,
	SPARK_BALL,
	SINGLE_FIRE,
	SINGLE_WATER,
	SINGLE_ENERGY,
	SINGLE_RED,
	SINGLE_YELLOW,
	SINGLE_BLUE,
	SINGLE_LIGHTBLUE,
	SINGLE_ORANGE,
	SINGLE_CYAN,
	SINGLE_GRAY,
	SINGLE_WHITE,
	SINGLE_CLOUD,
	SINGLE_FLAME,
	SINGLE_SMOKE,
	SINGLE_SPARK,
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
			case CLOUD_BALL:
				player.spawnParticle(Particle.CLOUD, location, 5, 0.05, 0.05, 0.05, 0.0);
				break;
			case FIRE_BALL:
				player.spawnParticle(Particle.FLAME, location, 5, 0.05, 0.05, 0.05, 0.0);
				break;
			case SPARK_BALL:
				player.spawnParticle(Particle.FIREWORKS_SPARK, location, 5, 0.05, 0.05, 0.05, 0.0);
				break;
			case SINGLE_FIRE:
				player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 0.001, 0.001, 1);
				player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 1.0, 0.001, 1);
				player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 0.32, 0.001, 1);
				break;
			case SINGLE_WATER:
				player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 0.5, 1.0, 1);
				player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 1.0, 1.0, 1);
				player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 0.5, 1.0, 1);
				break;
			case SINGLE_ENERGY:
				player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 1.0, 1.0, 1);
				player.spawnParticle(Particle.REDSTONE, location, 0, 0.34, 0.76, 1.0, 1.0);
				player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 1.0, 1.0, 1.0);
				break;
			case SINGLE_RED:
				player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 0.001, 0.001, 1);
				break;
			case SINGLE_YELLOW:
				player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 1.0, 0.001, 1);
				break;
			case SINGLE_BLUE:
				player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 0.5, 1.0, 1);
				break;
			case SINGLE_LIGHTBLUE:
				player.spawnParticle(Particle.REDSTONE, location, 0, 0.34, 0.76, 1.0, 1.0);
				break;
			case SINGLE_ORANGE:
				player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 0.32, 0.001, 1);
				break;
			case SINGLE_CYAN:
				player.spawnParticle(Particle.REDSTONE, location, 0, 0.001, 1.0, 1.0, 1);
				break;
			case SINGLE_GRAY:
				player.spawnParticle(Particle.REDSTONE, location, 0, 0.45, 0.45, 0.45, 1);
				break;
			case SINGLE_WHITE:
				player.spawnParticle(Particle.REDSTONE, location, 0, 1.0, 1.0, 1.0, 1.0);
				break;
			case SINGLE_CLOUD:
				player.spawnParticle(Particle.CLOUD, location, 1, 0.0, 0.0, 0.0, 0.0);
				break;
			case SINGLE_FLAME:
				player.spawnParticle(Particle.FLAME, location, 1, 0.0, 0.0, 0.0, 0.0);
				break;
			case SINGLE_SMOKE:
				player.spawnParticle(Particle.SMOKE_NORMAL, location, 1, 0.0, 0.0, 0.0, 0.0);
				break;
			case SINGLE_SPARK:
				player.spawnParticle(Particle.FIREWORKS_SPARK, location, 1, 0.0, 0.0, 0.0, 0.0);
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
