package net.libercraft.combatoverhaul.spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;
import net.libercraft.combatoverhaul.managers.Caster;

public abstract interface SpellEntity extends Tracer {

	// Functions for spell behaviour;
	public abstract void onFlightEffect(World world, Location location, Vector vector);
	public abstract void onImpactEffect(World world, Location location, Vector vector);
	public abstract void castBehaviour(Main plugin, Location location, Vector vector);

	// Functions for setting materials to fade and pass through;
	public abstract List<Material> setPassMaterial();
	public abstract List<Material> setFadeMaterial();
	
	// Dynamic functions;
	public abstract Main retrievePlugin();
	public abstract Caster retrieveCaster();
	public abstract double retrieveRadius();
	public abstract double retrieveDamage();
	public abstract boolean retrieveFlaming();
	
	public default void initialiseProjectile() {
		Main plugin = retrievePlugin();
		Caster caster = retrieveCaster();
		
		// Determine the location where to spawn the projectile;
		Location eyeLoc = caster.getPlayer().getEyeLocation().clone();
		Vector eyeVec = caster.getPlayer().getEyeLocation().getDirection().clone().normalize();
		Location spawnLoc = eyeLoc.clone().add(eyeVec);
		
		// Create the variables holding the location of the projectile;
		double x = spawnLoc.getX();
		double y = spawnLoc.getY();
		double z = spawnLoc.getZ();
		
		// Create Minecraft variables for the projectile;
		Vector vector = caster.getPlayer().getEyeLocation().getDirection();
		World world = caster.getPlayer().getWorld();
		Location location = new Location(world, x, y, z);
		
		// Change the vector to make it appear as if it came from the hand;
		Location end = location.clone();
		boolean foundEnd = false;
		while (!foundEnd) {
			end.add(vector);
			if (!setPassMaterial().contains(end.getBlock().getType())) {
				foundEnd = true;
			}
			if (end.getY() >= 256 || end.getY() <= 0) {
				foundEnd = true;
			}
			if (end.distance(location) >= 320) {
				foundEnd = true;
			}
		}
		vector = end.subtract(caster.getThirdPersonHandLocation()).toVector().normalize();
		location = caster.getThirdPersonHandLocation();
		
		castBehaviour(plugin, location, vector);
	}
	
	public default boolean checkCollision(Location location, Vector vector) {
		
		move(location, vector, 32);
		
		// Check for out of bounds
		if (location.getY() > 256 || location.getY() < 0) return true;
		
		// Check for blocks that the spell can pass through
		for (Material mat:setPassMaterial()) {
			if (!location.getBlock().getType().equals(mat)) continue;
			return false;
		}
		
		return true;
	}
	
	public default void move(Location location, Vector vector, int factor) {
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		
		x = x + (vector.getX() / factor);
		y = y + (vector.getY() / factor);
		z = z + (vector.getZ() / factor);
		
		location.setX(x);
		location.setY(y);
		location.setZ(z);
	}
	
	public default void onCollision(Location location, Vector vector) {
		Main plugin = retrievePlugin();
		Caster caster = retrieveCaster();
		double radius = retrieveRadius();
		double damage = retrieveDamage();
		
		Location fadeLocation = location.clone();
		move(fadeLocation, vector, 32);
		
		// Check for fade materials to cancel the impact effect;
		for (Material mat:setFadeMaterial()) {
			if (fadeLocation.getBlock().getType().equals(mat)) {
				return;
			}
		}
		
		// Execute damage on targets and add a safety tag;
		for (LivingEntity target:getTargets(location, radius)) {
			target.damage(damage, caster.getPlayer());
			if (!target.hasMetadata("safe")) target.setMetadata("safe", new FixedMetadataValue(plugin, "safe"));
		}
		
		// Call the function to execute the impact effect from the final spell class;'
		onImpactEffect(location.getWorld(), location, vector);
		
		// Remove the safety tag from all targets;
		for (LivingEntity target:getTargets(location, radius)) {
			if (target.hasMetadata("safe")) target.removeMetadata("safe", plugin);
		}
	}
	
	
	public default List<LivingEntity> getTargets(Location location, double radius) {
		Caster caster = retrieveCaster();
		
		List<LivingEntity> returnItem = new ArrayList<LivingEntity>();
		
		for (Entity e:location.getWorld().getEntities()) {
			Location eLoc = e.getLocation();
			eLoc.setY(eLoc.getY() + 1);
			if (location.distance(eLoc) <= radius && e instanceof LivingEntity && e != caster.getPlayer()) {
				returnItem.add((LivingEntity) e);
			}
		}
		
		return returnItem;
	}
}
