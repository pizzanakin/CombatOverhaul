package net.libercraft.combatoverhaul.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;
import net.libercraft.combatoverhaul.ability.AbilityScroll;

public class CasterManager implements Listener, Tracer {
	
	private Main plugin;
	private List<Caster> casters;
	
	public CasterManager(Main _plugin) {
		plugin = _plugin;
		casters = new ArrayList<Caster>();
	}
	
	// -- LIST METHODS -- //
	// Get the array of casters;
	public List<Caster> casters() {
		return casters;
	}
	
	// Return the caster instance of the player;
	public Caster get(Player player) {
		for (Caster caster:casters) {
			if (caster.getPlayer() != player) continue;
			return caster;
		}
		return null;
	}
	
	// -- EVENT LISTENERS -- //
	
	// Add a caster instance of the player;
	@EventHandler
	private void addCaster(PlayerJoinEvent e) {
		if (get(e.getPlayer()) != null) return;
		
		casters.add(new Caster(e.getPlayer(), plugin));
	}
	
	// Remove the caster instance of the player;
	@EventHandler
	private void removeCaster(PlayerQuitEvent e) {
		for (int i = 0; i < casters.size(); i++) {
			if (casters.get(i).getPlayer() != e.getPlayer()) continue;
			casters.remove(i);
		}
	}
	
	// Check for collision, otherwise update the player rotation data;
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		
		if (e.isCancelled()) return;
		
		for (Altar altar:plugin.getAltarManager().altars()) {
			altar.checkPillarCollision(e);
		}
		if (e.isCancelled()) return;
		
		if (get(e.getPlayer()) == null) casters.add(new Caster(e.getPlayer(), plugin));
		Caster caster = get(e.getPlayer());
		
		caster.updatePlayerRotation(e);
	}
	
	// Register a click and execute the correct actions;
	@EventHandler
	public void onClickEvent(PlayerInteractEvent e) {
		
		// Prevent player from turning an ability into a map;
		if (AbilityScroll.isAbility(e.getItem())) e.setCancelled(true);
		
		final Caster caster = get(e.getPlayer());
		if (caster == null) return;
		
		// -- Register Action --
		if (e.getAction().equals(Action.LEFT_CLICK_AIR)|e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			e.getPlayer().setMetadata("JLC", new FixedMetadataValue(plugin, "JLC"));
		}
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR)|e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			e.getPlayer().setMetadata("JRC", new FixedMetadataValue(plugin, "JRC"));
		}
		
		// -- Process clicks --
		// dual click;
		if (e.getPlayer().hasMetadata("JRC") && e.getPlayer().hasMetadata("JLC")) {
			e.getPlayer().removeMetadata("JRC", plugin);
			e.getPlayer().removeMetadata("JLC", plugin);
			
			if (caster.handleDualClickEvent()) {
				e.setCancelled(true);
			}
			return;
		}
		
		// double left click;
		if (e.getPlayer().hasMetadata("JLC") && e.getPlayer().hasMetadata("OLC")) {
			e.getPlayer().removeMetadata("OLC", plugin);
			
			if (caster.handleDoubleLeftClickEvent()) {
				e.setCancelled(true);
			}
			return;
		}

		// double right click;
		if (e.getPlayer().hasMetadata("JRC") && e.getPlayer().hasMetadata("ORC")) {
			e.getPlayer().removeMetadata("ORC", plugin);
			
			if (caster.handleDoubleRightClickEvent()) {
				e.setCancelled(true);
			}
			return;
		}
		
		new BukkitRunnable() {
			@Override public void run() {
				
				// left click;
				if (e.getPlayer().hasMetadata("JLC") && !e.getPlayer().hasMetadata("OLC")) {
					
					if (caster.handleLeftClick()) {
						e.setCancelled(true);
					}
				}

				// right click;
				if (e.getPlayer().hasMetadata("JRC") && !e.getPlayer().hasMetadata("ORC")) {
					
					if (caster.handleRightClick()) {
						e.setCancelled(true);
					}
				}
				
				
				// Change tags;
				if (e.getPlayer().hasMetadata("JLC")) {
					e.getPlayer().removeMetadata("JLC", plugin);
					e.getPlayer().setMetadata("OLC", new FixedMetadataValue(plugin, "OLC"));
				}
				if (e.getPlayer().hasMetadata("JRC")) {
					e.getPlayer().removeMetadata("JRC", plugin);
					e.getPlayer().setMetadata("ORC", new FixedMetadataValue(plugin, "ORC"));
				}
			}
			
		}.runTaskLater(plugin, 2);
		
		// Remove tags timer;
		new BukkitRunnable() {

			@Override
			public void run() {
				if (e.getPlayer().hasMetadata("OLC")) e.getPlayer().removeMetadata("OLC", plugin);
				if (e.getPlayer().hasMetadata("ORC")) e.getPlayer().removeMetadata("ORC", plugin);
				
			}
			
		}.runTaskLater(plugin, 4);
		
		return;
	}
}
