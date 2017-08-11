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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.player.Caster;

public class IceSpell extends BaseSpell implements SpellProjectile {

	public double speed;
	public double radius;
	public double damage;

	public IceSpell(Main plugin, Player player) {
		onCast(plugin, player);
		speed = 0.7;
		radius = 2.5;
		damage = 25;
		cost = 1;
		initialiseProjectile();
	}

	public static void handEffect(Location location) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onCastEffect(World world, Location location) {
		
		// Cast particles
		world.spawnParticle(Particle.CLOUD, location, 25, 0.0, 0.0, 0.0, 0.2);
		world.spawnParticle(Particle.FIREWORKS_SPARK, location, 10, 0.0, 0.0, 0.0, 0.2);
		
		// Cast sound effect
		world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 2, 3);
	}

	@Override
	public void onFlightEffect(World world, Location location, Vector vector) {
		
		// Flight particles
		world.spawnParticle(Particle.CLOUD, location, 5, 0.0, 0.0, 0.0, 0.05);
		world.spawnParticle(Particle.FIREWORKS_SPARK, location, 5, 0.0, 0.0, 0.0, 0.05);
		
		// Flight effect on targets
		for (LivingEntity target:getTargets(location, 1.5)) {
			target.setFireTicks(0);
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
		}
		
		// Flight block effect
		if (location.getBlock().getType().equals(Material.FIRE)) location.getBlock().setType(Material.AIR);
		if (location.getBlock().getType().equals(Material.STATIONARY_WATER)) {
			Location locUp = location.clone();
			locUp.setY(locUp.getY() + 1);
			if (locUp.getBlock().getType().equals(Material.AIR)) location.getBlock().setType(Material.FROSTED_ICE);
		}
		
		// Flight sound effect
		world.playSound(location, Sound.BLOCK_GLASS_BREAK, 1, 1);
	}

	@Override
	public void onImpactEffect(World world, Location location) {
		
		// Impact particles
		world.spawnParticle(Particle.CLOUD, location, 25, 0.0, 0.0, 0.0, 0.2);
		world.spawnParticle(Particle.FIREWORKS_SPARK, location, 100, 0.0, 0.0, 0.0, 0.2);
		
		// Impact effect on targets
		for (LivingEntity target:getTargets(location, 1.5)) {
			target.setFireTicks(0);
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
		}

		// Impact sound effect
		world.playSound(location, Sound.BLOCK_GLASS_BREAK, 2, -2);
		
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
		if (!(location.getBlock().getType().equals(Material.AIR)|location.getBlock().getType().equals(Material.SNOW))) {
			Location loc = location.clone();
			loc.setY(loc.getY() + 1);
			if (loc.getBlock().getType().equals(Material.AIR)) {
				loc.getBlock().setType(Material.SNOW);
				
				new BukkitRunnable() {

					@Override
					public void run() {
						loc.getBlock().setType(Material.AIR);
					}
					
				}.runTaskLater(plugin, 220);
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
		list.add(Material.LAVA);
		list.add(Material.STATIONARY_LAVA);
		list.add(Material.FIRE);
		list.add(Material.FROSTED_ICE);
		list.add(Material.SNOW);
		list.add(Material.SUGAR_CANE_BLOCK);
		return list;
	}

	@Override
	public List<Material> setFadeMaterial() {
		List<Material> list = new ArrayList<Material>();
		return list;
	}
	
	@Override public Main retrievePlugin() {return plugin;}
	@Override public Caster retrieveCaster() {return caster;}
	@Override public double retrieveSpeed() {return speed;}
	@Override public double retrieveRadius() {return radius;}
	@Override public double retrieveDamage() {return damage;}
}
