package net.libercraft.combatoverhaul;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import net.libercraft.combatoverhaul.ability.AbilityScroll;
import net.libercraft.combatoverhaul.managers.AltarManager;
import net.libercraft.combatoverhaul.managers.CasterManager;
import net.libercraft.combatoverhaul.spell.Spellbook;

public class Main extends JavaPlugin implements CommandExecutor {

	private AltarManager altarManager;
	private CasterManager casterManager;
	
	@Override
	public void onEnable() {

		// Create a reference to the datafile
		File datafile = new File(getDataFolder(), "data.yml");
		
		// Make sure the data file exists
		if (!datafile.exists()) {
			datafile.getParentFile().mkdirs();
			saveResource("data.yml", false);
		}
		
		altarManager = new AltarManager(this);
		try  {
			altarManager.load(datafile);
		} catch (InvalidConfigurationException | IOException e) {
			e.printStackTrace();
		}

		altarManager.loadAltars();
		
		
		casterManager = new CasterManager(this);
		
		this.getServer().getPluginManager().registerEvents(new ArrowListener(this), this);
		this.getServer().getPluginManager().registerEvents(casterManager, this);
		this.getServer().getPluginManager().registerEvents(altarManager, this);
		this.getCommand("get").setExecutor(this);
	}
	
	@Override
	public void onDisable() {
		altarManager.storeAltars();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return true;
		Player player = (Player) sender;
		
		ItemStack item = null;
		switch (args[0]) {
		case "spell":
			
			// Get the spell
			for (Spellbook spell:Spellbook.values()) {
				if (!spell.name().equalsIgnoreCase(args[1])) continue;
				item = spell.create();
			}
			if (item == null) {
				player.sendMessage("That spell does not exist.");
				return true;
			}
			break;
		case "ability":
			for (AbilityScroll ability:AbilityScroll.values()) {
				if (!ability.name().equalsIgnoreCase(args[1])) continue;
				item = ability.create();
			}
			if (item == null) {
				player.sendMessage("That ability does not exist.");
				return true;
			}
			break;
		default:
			return false;
		}

		player.getInventory().addItem(item);
		return true;
	}
	
	public AltarManager getAltarManager() {
		return altarManager;
	}
	
	public CasterManager getCasterManager() {
		return casterManager;
	}
}
