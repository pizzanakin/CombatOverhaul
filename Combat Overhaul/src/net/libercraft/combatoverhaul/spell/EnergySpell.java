package net.libercraft.combatoverhaul.spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.player.Caster;

public class EnergySpell extends BaseSpell implements SpellStrike {

	public World world;
	public Location location;
	public double radius;
	public double damage;

	public EnergySpell(Main plugin, Player player) {
		onCast(plugin, player);
		world = player.getWorld();
		radius = 1.5;
		damage = 30;
		cost = 1;
		initialiseProjectile();
	}

	public static void handEffect(Location location) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onCastEffect(World world, Location location) {
		
		// Cast particles
		world.spawnParticle(Particle.FIREWORKS_SPARK, location, 25, 0.0, 0.0, 0.0, 0.05);
		world.spawnParticle(Particle.ENCHANTMENT_TABLE, location, 100, 1, 1, 1, 0.1);
	}

	@Override
	public void onFlightEffect(World world, Location location, Vector vector) {
		
		// Flight particles
		world.spawnParticle(Particle.FIREWORKS_SPARK, location, 5, 0.0, 0.0, 0.0, 0.05);
	}

	@Override
	public void onImpactEffect(World world, Location location) {
		
		// Impact block effect
		world.strikeLightning(location);
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
	@Override public double retrieveRadius() {return radius;}
	@Override public double retrieveDamage() {return damage;}
}
