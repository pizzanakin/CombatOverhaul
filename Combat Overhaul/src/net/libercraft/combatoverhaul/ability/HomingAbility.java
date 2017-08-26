package net.libercraft.combatoverhaul.ability;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;

public class HomingAbility extends BaseAbility implements ArrowAbility, Tracer {

	private LivingEntity target;
	
	public HomingAbility(Main plugin, Player player) {
		onCast(plugin, player);
		
		for (LivingEntity entity:player.getWorld().getLivingEntities()) {
			if (entity.equals(player)) continue;
			if (entity.getLocation().distance(player.getLocation()) > 12) continue;
			Vector targetLine = entity.getLocation().subtract(player.getLocation()).toVector().normalize();
			if (targetLine.getCrossProduct(player.getEyeLocation().getDirection().normalize()).length() > 1.5) continue;
			target = entity;
		}
		if (target != null) {
			caster.hasActivatedHoming = true;
		}
	}
	
	public void activate(Vector vector) {
		
		// Create a new projectile to be launched
		Location eyeLoc = caster.getPlayer().getEyeLocation().clone();
		Vector eyeVec = caster.getPlayer().getEyeLocation().getDirection().clone().normalize();
		Location spawnLoc = eyeLoc.clone().add(eyeVec);
		
		Projectile proj = spawnProjectile(plugin, spawnLoc, caster);
		proj.setVelocity(vector);
		
		new BukkitRunnable() {
			@Override public void run() {
				Vector projVelocity = proj.getVelocity();
				Vector targetVelocity = target.getLocation().add(new Vector(0, 2, 0)).subtract(proj.getLocation()).toVector().normalize();
				
				if (projVelocity.getX() > targetVelocity.getX()) {
					projVelocity.setX(projVelocity.getX() - 0.1);
				}
				else if (projVelocity.getX() < targetVelocity.getX()) {
					projVelocity.setX(projVelocity.getX() + 0.1);
				}
				
				if (projVelocity.getY() > targetVelocity.getY()) {
					projVelocity.setY(projVelocity.getY() - 0.1);
				}
				else if (projVelocity.getY() < targetVelocity.getY()) {
					projVelocity.setY(projVelocity.getY() + 0.1);
				}
				
				if (projVelocity.getZ() > targetVelocity.getZ()) {
					projVelocity.setZ(projVelocity.getZ() - 0.1);
				}
				else if (projVelocity.getZ() < targetVelocity.getZ()) {
					projVelocity.setZ(projVelocity.getZ() + 0.1);
				}
				
				projVelocity.normalize().multiply(0.1);
				proj.setVelocity(projVelocity);
				if (proj.isDead()) this.cancel();
			}
		}.runTaskTimer(plugin, 0, 1);
		
		
		caster.getPlayer().setCooldown(Material.BOW, 25);
		caster.hasActivatedHoming = false;
		if (caster.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
		caster.removeArrow();
		caster.decreaseBowDurability();
	}
}
