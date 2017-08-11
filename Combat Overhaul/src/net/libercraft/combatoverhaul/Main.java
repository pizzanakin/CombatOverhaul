package net.libercraft.combatoverhaul;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.libercraft.combatoverhaul.player.Caster;
import net.libercraft.combatoverhaul.player.GetCommand;
import net.libercraft.combatoverhaul.player.InputReader;
import net.libercraft.combatoverhaul.player.ListenClass;

public class Main extends JavaPlugin implements Listener, Tracer {

	final Main plugin = this;
	private List<Caster> casters;
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new ListenClass(this), this);
		this.getServer().getPluginManager().registerEvents(new InputReader(this), this);
		this.getCommand("get").setExecutor(new GetCommand(this));
		
		// Register the glow enchantment;
		try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            Glow glow = new Glow(80);
            Enchantment.registerEnchantment(glow);
        }
        catch (IllegalArgumentException e){
        	
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        casters = new ArrayList<Caster>();
        
        new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player:getServer().getOnlinePlayers()) {
					if (getCaster(player) != null) continue;
					addCaster(player);
				}
			}
        }.runTaskTimer(this, 0, 1);
	}
	
	@Override
	public void onDisable() {
	}
	
	public List<Caster> getCasters() {
		return casters;
	}
	
	// Return the caster instance of the player;
	public Caster getCaster(Player player) {
		for (Caster caster:casters) {
			if (caster.getPlayer() != player) continue;
			return caster;
		}
		return null;
	}
	
	// Add a caster instance of the player;
	private void addCaster(Player player) {
		casters.add(new Caster(player, this));
	}
	
	// Remove the caster instance of the player;
	@EventHandler
	private void removeCaster(PlayerQuitEvent e) {
		for (int i = 0; i < casters.size(); i++) {
			if (casters.get(i).getPlayer() != e.getPlayer()) continue;
			casters.remove(i);
		}
	}
}
