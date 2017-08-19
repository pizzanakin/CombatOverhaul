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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.particleanimator.ParticleSprite;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation.Animation;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation.Shape;
import net.libercraft.combatoverhaul.player.Caster;

public class WaterSpell extends BaseSpell implements SpellProjectile {

	public double speed;
	public double radius;
	public double damage;
	private ParticleAnimation cyanAnimation = new ParticleAnimation(ParticleSprite.SINGLE_BLUE, Animation.TWIST, Shape.CROSS_FOUR, 0.2, 0.0, 0.3);
	private ParticleAnimation wakeAnimation = new ParticleAnimation(ParticleSprite.SINGLE_WAKE, Animation.TWIST, Shape.CROSS_FOUR, 0.4, 0, 1);
	
	public WaterSpell(Main plugin, Player player, int cost) {
		speed = 0.6;
		radius = 2.5;
		damage = 10;
		onCast(plugin, player, cost);
		initialiseProjectile();
	}

	public static void handEffect(Player player, Location location) {
		player.spawnParticle(Particle.WATER_WAKE, location, 4, 0.1, 0.1, 0.1, 0.00);
		//player.spawnParticle(Particle.REDSTONE, location, 0, 0.01, 1, 1, 1);
		//player.spawnParticle(Particle.REDSTONE, location, 0, 0.01, 1, 1, 1);
	}
	
	@Override
	public void onCastEffect(World world, Location location) {
		
		// Cast particles
		world.spawnParticle(Particle.WATER_WAKE, location, 50, 0, 0, 0, 0.1);
		
		// Cast sound effect
		world.playSound(location, Sound.ENTITY_BOAT_PADDLE_WATER, 1, 1);
	}

	@Override
	public void onFlightEffect(World world, Location location, Vector vector) {
		
		// Flight particles
		cyanAnimation.showNextFrame(location, vector);
		wakeAnimation.showNextFrame(location, vector);
		ParticleSprite.CYAN_BALL.summon(location);
		
		// Flight effect on targets
		for (LivingEntity target:getTargets(location, 1.5)) {
			target.setFireTicks(0);
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1));
		}
		
		// Flight sound effect
		world.playSound(location, Sound.BLOCK_WATER_AMBIENT, 1, 2);
		
		// Flight block effect
		if (location.getBlock().getType().equals(Material.FIRE)) location.getBlock().setType(Material.AIR);
	}
	
	@Override
	public void onImpactEffect(World world, Location location) {
		
		// Impact particles
		world.spawnParticle(Particle.WATER_WAKE,location, 60, 0.2, 0.2, 0.2, 0.1);
		world.spawnParticle(Particle.CLOUD, location, 50, 0.2, 0.2, 0.2, 0.1);
		
		
		// Impact effect on targets
		for (LivingEntity target:getTargets(location, radius)) {
			target.setFireTicks(0);
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1));
		}
		
		// Sound effect
		world.playSound(location, Sound.BLOCK_WATERLILY_PLACE, 1, -1);
		
		// Impact block effect
		for (int i=-1; i<2; i++) {
			for (int j=-1; j<2; j++) {
				for (int k=-1; k<2; k++) {
					Location loc = new Location(world, location.getX() + i, location.getY() + j + 1, location.getZ() + k);
					loc = loc.getBlock().getLocation();
					loc.setX(loc.getX() + 0.5);
					loc.setY(loc.getY() + 0.5);
					loc.setZ(loc.getZ() + 0.5);
					if (location.distance(loc) <= 1.5 && loc.getBlock().getType().equals(Material.FIRE)) loc.getBlock().setType(Material.AIR);
				}
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
		list.add(Material.WATER);
		list.add(Material.STATIONARY_WATER);
		list.add(Material.FIRE);
		list.add(Material.SUGAR_CANE_BLOCK);
		return list;
	}

	@Override
	public List<Material> setFadeMaterial() {
		List<Material> list = new ArrayList<Material>();
		list.add(Material.LAVA);
		list.add(Material.STATIONARY_LAVA);
		return list;
	}
	
	@Override public Main retrievePlugin() {return plugin;}
	@Override public Caster retrieveCaster() {return caster;}
	@Override public double retrieveSpeed() {return speed;}
	@Override public double retrieveRadius() {return radius;}
	@Override public double retrieveDamage() {return damage;}
}
