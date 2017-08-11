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
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.player.Caster;

public class LavaSpell extends BaseSpell implements SpellProjectile {

	public double speed;
	public double radius;
	public double damage;

	public LavaSpell(Main plugin, Player player) {
		onCast(plugin, player);
		speed = 1.5;
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
		world.spawnParticle(Particle.LAVA, location, 15, 0.0, 0.0, 0.0, 0.15);
		world.spawnParticle(Particle.SMOKE_LARGE, location, 15, 0.1, 0.1, 0.1, 0.1);
		
		// Cast sound effect
		world.playSound(location, Sound.ITEM_FIRECHARGE_USE, 1, -1);
		
	}

	@Override
	public void onFlightEffect(World world, Location location, Vector vector) {
		
		// Flight particles
		world.spawnParticle(Particle.LAVA, location, 5, 0.1, 0.1, 0.1, 0.05);
		world.spawnParticle(Particle.FLAME, location, 5, 0.1, 0.1, 0.1, 0.05);
		world.spawnParticle(Particle.SMOKE_LARGE, location, 5, 0.1, 0.1, 0.1, 0.05);
		
		
		// Flight effect on targets
		for (LivingEntity target:getTargets(location, 1.5)) {
			target.setFireTicks(50);
		}
		
		// Flight sound effect
		world.playSound(location, Sound.BLOCK_SNOW_BREAK, 4, -1);
		
		// Flight block effect
		if (location.getBlock().getType().equals(Material.ICE)) location.getBlock().setType(Material.WATER);
		if (location.getBlock().getType().equals(Material.FROSTED_ICE)) location.getBlock().setType(Material.WATER);
	}
	
	@Override
	public void onImpactEffect(World world, Location location) {
		
		// Impact particles
		world.spawnParticle(Particle.LAVA, location, 50, 0.1, 0.1, 0.1, 0.1);
		world.spawnParticle(Particle.EXPLOSION_LARGE, location, 10, 1, 1, 1, 0.1);
		
		// Flight effect on targets
		for (LivingEntity target:getTargets(location, 1.5)) {
			target.setFireTicks(50);
		}
		
		// Flight sound effect
		world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 5, 1);
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
		list.add(Material.WATER);
		list.add(Material.STATIONARY_WATER);
		list.add(Material.ICE);
		list.add(Material.FROSTED_ICE);
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
