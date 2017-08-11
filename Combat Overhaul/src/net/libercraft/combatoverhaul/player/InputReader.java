package net.libercraft.combatoverhaul.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;

public class InputReader implements Listener, Tracer {
	
	Main plugin;
	
	public InputReader(Main _plugin) {
		plugin = _plugin;
	}
	
	@EventHandler
	public void onClickEvent(PlayerInteractEvent e) {
		
		final Caster caster = plugin.getCaster(e.getPlayer());
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
			trace("test");
			
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
