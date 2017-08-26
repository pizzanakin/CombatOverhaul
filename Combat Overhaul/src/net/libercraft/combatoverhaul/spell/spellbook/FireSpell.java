package net.libercraft.combatoverhaul.spell.spellbook;

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
import net.libercraft.combatoverhaul.managers.Caster;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation.Animation;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation.Shape;
import net.libercraft.combatoverhaul.particleanimator.ParticleSprite;
import net.libercraft.combatoverhaul.spell.BaseSpell;
import net.libercraft.combatoverhaul.spell.SpellProjectile;

public class FireSpell extends BaseSpell implements SpellProjectile {

	public double speed;
	public double radius;
	public double damage;
	
	private ParticleAnimation castFlameAnimation;
	private ParticleAnimation flightFlameAnimation;
	private ParticleAnimation flightSmokeAnimation;
	private ParticleAnimation flameImpactAnimation;
	
	public FireSpell(Main plugin, Player player, int cost) {
		speed = 0.6; //0.6
		radius = 2;
		damage = 10;
		castFlameAnimation = new ParticleAnimation(plugin, ParticleSprite.SINGLE_ORANGE, Animation.LINEAR, Shape.RANDOM_SPHERE, 1, 0, 6, false);
		flightFlameAnimation = new ParticleAnimation(plugin, ParticleSprite.SINGLE_FLAME, Animation.TWIST, Shape.CROSS_FOUR, 0.2, 0, 0.3, false);
		flightSmokeAnimation = new ParticleAnimation(plugin, ParticleSprite.SINGLE_SMOKE, Animation.SINUS, Shape.CROSS_FOUR, 0.5, 0.0, 1, false);
		flameImpactAnimation = new ParticleAnimation(plugin, ParticleSprite.SINGLE_GRAY, Animation.LINEAR, Shape.RANDOM_HALF_SPHERE, 2.5, 0, 4, false);
		onCast(plugin, player, cost);
		initialiseProjectile();
	}

	public static void handEffect(Player player, Location location) {
		ParticleSprite.SINGLE_ORANGE.summon(location);
		ParticleSprite.SINGLE_RED.summon(location);
		ParticleSprite.SINGLE_YELLOW.summon(location);
	}
	
	@Override
	public void onCastEffect(World world, Location location) {
		
		// Cast particles
		world.spawnParticle(Particle.SMOKE_LARGE, location, 10, 0.1, 0.1, 0.1, 0.1);
		castFlameAnimation.play(location);
		
		// Cast sound effect
		world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 2, 1);
	}

	@Override
	public void onFlightEffect(World world, Location location, Vector vector) {
		boolean useAnimation = true;

		// Flight particles
		if (useAnimation) {
			flightFlameAnimation.showNextFrame(location, vector);
			flightSmokeAnimation.showNextFrame(location, vector);
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
					if (fireLoc.getBlock().getType().equals(Material.FIRE)) fireLoc.getBlock().setType(Material.AIR);
				}
			}.runTaskLater(plugin, (long) Math.ceil(Math.random() * 20) + 20);
		}
		
		// Flight sound effect
		world.playSound(location, Sound.BLOCK_FIRE_AMBIENT, 1, 2);
	}

	@Override
	public void onImpactEffect(World world, Location location, Vector vector) {
		
		// Impact particles
		world.spawnParticle(Particle.SMOKE_LARGE, location, 50, 0.5, 0.5, 0.5, 0.05);
		world.spawnParticle(Particle.LAVA, location, 50, 0.5, 0.5, 0.5, 0.05);
		flameImpactAnimation.play(location, vector);
		
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
				
				Location belowLoc = loc.clone();
				belowLoc.setY(belowLoc.getY() - 1);
				if (
						!belowLoc.getBlock().getType().equals(Material.NETHERRACK)&&
						!belowLoc.getBlock().getType().equals(Material.LOG)&&
						!belowLoc.getBlock().getType().equals(Material.LOG_2)&&
						!belowLoc.getBlock().getType().equals(Material.LEAVES)&&
						!belowLoc.getBlock().getType().equals(Material.LEAVES_2)
						) new BukkitRunnable() {
					@Override public void run() {
						if (loc.getBlock().getType().equals(Material.FIRE)) loc.getBlock().setType(Material.AIR);
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
	@Override public boolean retrieveFlaming() {return true;}
}