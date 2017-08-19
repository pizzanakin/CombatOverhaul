package net.libercraft.combatoverhaul.spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation.Animation;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation.Shape;
import net.libercraft.combatoverhaul.particleanimator.ParticleSprite;
import net.libercraft.combatoverhaul.player.Caster;

public class FireSpell extends BaseSpell implements SpellProjectile {

	public double speed;
	public double radius;
	public double damage;
	
	private ParticleAnimation flameAnimation = new ParticleAnimation(ParticleSprite.SINGLE_FLAME, Animation.TWIST, Shape.CROSS_FOUR, 0.2, 0, 0.3);
	private ParticleAnimation smokeAnimation = new ParticleAnimation(ParticleSprite.SINGLE_SMOKE, Animation.SINUS, Shape.CROSS_FOUR, 0.5, 0.0, 1);
	
	public FireSpell(Main plugin, Player player, int cost) {
		speed = 0.6;
		radius = 2;
		damage = 10;
		onCast(plugin, player, cost);
		initialiseProjectile();
	}

	public static void handEffect(Player player, Location location) {
		player.spawnParticle(Particle.FLAME, location, 4, 0.025, 0.025, 0.025, 0.00);
	}
	
	@Override
	public void onCastEffect(World world, Location location) {
		
		// Cast particles
		world.spawnParticle(Particle.SMOKE_LARGE, location, 10, 0.1, 0.1, 0.1, 0.5);
		
		// Cast sound effect
		world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 2, 1);
	}

	@Override
	public void onFlightEffect(World world, Location location, Vector vector) {
		boolean useAnimation = true;

		// Flight particles
		if (useAnimation) {
			flameAnimation.showNextFrame(location, vector);
			smokeAnimation.showNextFrame(location, vector);
			ParticleSprite.ORANGE_BALL.summon(location);
		}
		else {
			world.spawnParticle(Particle.FLAME, location, 10, 0.15, 0.15, 0.15, 0.01);
			world.spawnParticle(Particle.SMOKE_LARGE, location, 1, 0.2, 0.2, 0.2, 0.1);
			world.spawnParticle(Particle.SMOKE_NORMAL, location, 10, 0.0, 0.0, 0.0, 0.01);
			world.spawnParticle(Particle.FLAME, location, 1, 0.1, 0.1, 0.1, 0.1);
		}
		
		// Flight effect on targets
		for (LivingEntity target:getTargets(location, 1.5)) {
			target.setFireTicks(100);
		}
		
		// Flight block effect
		if (location.getBlock().getType().equals(Material.LONG_GRASS)) location.getBlock().setType(Material.FIRE);
		if (location.getBlock().getType().equals(Material.DEAD_BUSH)) location.getBlock().setType(Material.FIRE);
		if (location.getBlock().getType().equals(Material.DOUBLE_PLANT)) location.getBlock().setType(Material.FIRE);
		if (location.getBlock().getType().equals(Material.FIRE)) {
			final Location fireLoc = location.clone();
			new BukkitRunnable() {
				@Override
				public void run() {
					fireLoc.getBlock().setType(Material.AIR);
				}
			}.runTaskLater(plugin, (long) Math.ceil(Math.random() * 20) + 20);
		}
		// Flight sound effect
		world.playSound(location, Sound.BLOCK_FIRE_AMBIENT, 1, 2);
	}

	@Override
	public void onImpactEffect(World world, Location location) {
		
		// Impact particles
		world.spawnParticle(Particle.SMOKE_LARGE, location, 50, 0.5, 0.5, 0.5, 0.05);
		world.spawnParticle(Particle.LAVA, location, 50, 0.5, 0.5, 0.5, 0.05);
		
		// Impact effect on targets
		for (LivingEntity target:getTargets(location, radius)) {
			target.setFireTicks(100);
		}
		
		// Impact sound effect
		world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, -1);
		
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		
		// Impact block effect
		Location[] locs = {new Location(world, x + 1, y, z), new Location(world, x - 1, y, z), new Location(world, x, y + 1, z), new Location(world, x, y - 1, z), new Location(world, x, y, z + 1), new Location(world, x, y, z - 1), location.clone()};
		for (Location loc:locs) {
			if (loc.getBlock().getType().equals(Material.AIR)) {
				loc.getBlock().setType(Material.FIRE);
				// Check if block below it is burnable;
				// If not then remove fire after small time;
				new BukkitRunnable() {
					@Override public void run() {
						loc.getBlock().setType(Material.AIR);
					}
				}.runTaskLater(plugin, (long) Math.ceil(Math.random() * 20) + 40);
			}
		}
		
	}

	@Override
	public List<Material> setPassMaterial() {
		List<Material> list = new ArrayList<Material>();
		list.add(Material.AIR);
		list.add(Material.DOUBLE_PLANT);
		list.add(Material.LONG_GRASS);
		list.add(Material.DEAD_BUSH);
		list.add(Material.SAPLING);
		list.add(Material.WEB);
		list.add(Material.TORCH);
		list.add(Material.VINE);
		list.add(Material.LAVA);
		list.add(Material.STATIONARY_LAVA);
		list.add(Material.FIRE);
		list.add(Material.SUGAR_CANE_BLOCK);
		list.add(Material.SUGAR_CANE);
		return list;
	}

	@Override
	public List<Material> setFadeMaterial() {
		List<Material> list = new ArrayList<Material>();
		list.add(Material.WATER);
		list.add(Material.STATIONARY_WATER);
		return list;
	}
	
	@Override public Main retrievePlugin() {return plugin;}
	@Override public Caster retrieveCaster() {return caster;}
	@Override public double retrieveSpeed() {return speed;}
	@Override public double retrieveRadius() {return radius;}
	@Override public double retrieveDamage() {return damage;}
}