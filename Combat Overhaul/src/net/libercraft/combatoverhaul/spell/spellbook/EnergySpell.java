package net.libercraft.combatoverhaul.spell.spellbook;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.managers.Caster;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation;
import net.libercraft.combatoverhaul.particleanimator.ParticleSprite;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation.Animation;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation.Shape;
import net.libercraft.combatoverhaul.spell.BaseSpell;
import net.libercraft.combatoverhaul.spell.SpellStrike;

public class EnergySpell extends BaseSpell implements SpellStrike {

	public World world;
	public Location location;
	public double radius;
	public double damage;
	
	private ParticleAnimation castAnimation;
	private ParticleAnimation flightCyanAnimation;
	private ParticleAnimation flightBlueAnimation;
	private ParticleAnimation flightWhiteAnimation;
	private ParticleAnimation impactBlueAnimation;

	public EnergySpell(Main plugin, Player player, int cost) {
		world = player.getWorld();
		radius = 1.5;
		damage = 30;
		castAnimation = new ParticleAnimation(plugin, ParticleSprite.SINGLE_ENERGY, Animation.LINEAR, Shape.RANDOM_SPHERE, 1.5, 0, 6, false);
		flightCyanAnimation = new ParticleAnimation(plugin, ParticleSprite.SINGLE_CYAN, Animation.LIGHTNING, Shape.SINGLE_LINE_RANDOM, 4, 0, 4, false);
		flightBlueAnimation = new ParticleAnimation(plugin, ParticleSprite.SINGLE_LIGHTBLUE, Animation.LIGHTNING, Shape.SINGLE_LINE_RANDOM, 3, 0, 4, false);
		flightWhiteAnimation = new ParticleAnimation(plugin, ParticleSprite.SINGLE_WHITE, Animation.LIGHTNING, Shape.SINGLE_LINE_RANDOM, 3, 0, 4, false);
		impactBlueAnimation = new ParticleAnimation(plugin, ParticleSprite.SINGLE_LIGHTBLUE, Animation.LINEAR, Shape.RANDOM_HALF_SPHERE, 3, 0, 4, false);
		onCast(plugin, player, cost);
		initialiseProjectile();
	}

	public static void handEffect(Player player, Location location) {
		ParticleSprite.SINGLE_LIGHTBLUE.summon(location);
		ParticleSprite.SINGLE_LIGHTBLUE.summon(location);
		ParticleSprite.SINGLE_CYAN.summon(location);
	}
	
	@Override
	public void onCastEffect(World world, Location location) {
		
		// Cast particles
		castAnimation.play(location);
	}

	@Override
	public void onFlightEffect(World world, Location location, Vector vector) {
		
		// Flight particles
		flightCyanAnimation.showNextFrame(location, vector);
		flightBlueAnimation.showNextFrame(location, vector);
		flightWhiteAnimation.showNextFrame(location, vector);
		
		for (LivingEntity target:getTargets(location, 1.1)) {
			if (target instanceof Creeper) {
				Creeper creeper = (Creeper) target;
				creeper.setPowered(true);
			}
		}
	}

	@Override
	public void onImpactEffect(World world, Location location, Vector vector) {
		
		// Impact block effect
		impactBlueAnimation.play(location, vector);
		world.spawnParticle(Particle.EXPLOSION_LARGE, location, 3, 0.5, 0.5, 0.5, 0.01);
		
		// Impact sound effect
		world.playSound(location, Sound.ENTITY_LIGHTNING_THUNDER, 10, 2);
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
		list.add(Material.STATIONARY_WATER);
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
	@Override public boolean retrieveFlaming() {return true;}
}
