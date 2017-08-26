package net.libercraft.combatoverhaul;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.libercraft.combatoverhaul.managers.Caster;

public class ArrowListener implements Listener, Tracer {

	Main plugin;
	
	public ArrowListener(Main _plugin) {
		plugin = _plugin;
	}
	
	// Prevents entities from taking damage from spell effects that are not part of flight or impact damage;
	@EventHandler
	public void removeDamageFromSpellEffects(EntityDamageEvent e) {
		
		// Cancel the event is the entity has the tag "safe"
		if (e.getEntity().hasMetadata("safe")) e.setCancelled(true);
	}
	
	// Change a regular arrow into a volley ability;
	@EventHandler
	public void onAbilityShot(EntityShootBowEvent e) {
		
		// Make sure the shooter is a player;
		if (!(e.getEntity() instanceof Player)) return;
		Caster caster = plugin.getCasterManager().get((Player) e.getEntity());
		
		// Check if the player has activated a volley;
		if (caster.hasActivatedVolley) {
			
			// Activate the volley instance;
			caster.volley.activate(e.getProjectile().getVelocity());
			e.setCancelled(true);
		}
		
		if (caster.hasActivatedHoming) {
			
			// Activate the homing instance;
			caster.homing.activate(e.getProjectile().getVelocity());
			e.setCancelled(true);
		}
		
	}
	
	// Make arrows shot by an ability unable to get picked up;
	@EventHandler
	public void onArrowPickup(PlayerPickupItemEvent e) {
		
		//  Make sure the item being picked up is an arrow
		if (!(e.getItem().getItemStack().getType().equals(Material.ARROW)|e.getItem().getItemStack().getType().equals(Material.SPECTRAL_ARROW)|e.getItem().getItemStack().getType().equals(Material.TIPPED_ARROW))) return;
		
		// Make sure the arrow was shot by an ability
		if (!e.getItem().hasMetadata("arrow")) return;
		e.setCancelled(true);
	}
	
	// Makes arrows shot by an ability dissapear a second after landing.
	@EventHandler
	public void onArrowHit(ProjectileHitEvent e) {
		
		if (!(e.getEntityType().equals(EntityType.ARROW)|e.getEntityType().equals(EntityType.SPECTRAL_ARROW)|e.getEntityType().equals(EntityType.TIPPED_ARROW))) return;
		if (!e.getEntity().hasMetadata("arrow")) return;
		
		new BukkitRunnable() {

			@Override
			public void run() {
				e.getEntity().remove();
			}
			
		}.runTaskLater(plugin, 20);
	}
	
	// Increase the damage of arrows shot by an ability;
	@EventHandler
	public void onArrowDamage(EntityDamageByEntityEvent e) {
		
		if (!e.getDamager().hasMetadata("arrow")) return;
		e.setDamage(25);
	}
}
