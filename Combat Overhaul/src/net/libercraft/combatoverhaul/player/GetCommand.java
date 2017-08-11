package net.libercraft.combatoverhaul.player;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.libercraft.combatoverhaul.Glow;
import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;
import net.libercraft.combatoverhaul.ability.Ability;
import net.libercraft.combatoverhaul.spell.Spellbook;

public class GetCommand implements CommandExecutor, Tracer {
	
	Main plugin;
	
	public GetCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return true;
		Player player = (Player) sender;
		
		ItemStack item;
		ItemMeta itemMeta;
		int cost = 40;
		if (!player.getGameMode().equals(GameMode.CREATIVE)) {
			int count = 0;
			for (int i = 0; i < 40; i++) {
				ItemStack diamond = player.getInventory().getItem(i);
				if (diamond == null) continue;
				if (!diamond.getType().equals(Material.DIAMOND)) continue;
				count += diamond.getAmount();
			}
			if (count < 40) {
				player.sendMessage("You do not have enough diamonds to use this command.");
				return true;
			}
		}
		switch (args[0]) {
		case "spell":
			item = new ItemStack(Material.BOOK, 1);
			itemMeta = item.getItemMeta();
			itemMeta.addEnchant(new Glow(80), 0, true);
			itemMeta.setDisplayName("UNKNOWN");
			for (Spellbook spell:Spellbook.values()) {
				if (!spell.name().equalsIgnoreCase(args[1])) continue;
				itemMeta.setDisplayName(spell.itemName);
			}
			if (player.getGameMode().equals(GameMode.CREATIVE)) break;
			for (int i = 0; i < 40; i++) {
				ItemStack diamond = player.getInventory().getItem(i);
				if (diamond == null) continue;
				if (!diamond.getType().equals(Material.DIAMOND)) continue;
				if (diamond.getAmount() >= cost) {
					trace(cost);
					diamond.setAmount(diamond.getAmount() - cost);
					break;
				}
				else if (diamond.getAmount() < cost) {
					trace(cost);
					int oldCost = cost;
					cost = cost - diamond.getAmount();
					diamond.setAmount(diamond.getAmount() - oldCost);
				}
			}
			break;
		case "ability":
			item = new ItemStack(Material.EMPTY_MAP, 1);
			itemMeta = item.getItemMeta();
			itemMeta.addEnchant(new Glow(80), 0, true);
			itemMeta.setDisplayName("UNKNOWN");
			for (Ability ability:Ability.values()) {
				if (!ability.name().equalsIgnoreCase(args[1])) continue;
				itemMeta.setDisplayName(ability.itemName);
			}
			if (player.getGameMode().equals(GameMode.CREATIVE)) break;
			break;
		default:
			return false;
		}

		item.setItemMeta(itemMeta);
		player.getInventory().addItem(item);
		return true;
	}
}
