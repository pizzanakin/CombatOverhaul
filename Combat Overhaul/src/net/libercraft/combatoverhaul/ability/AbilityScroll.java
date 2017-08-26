package net.libercraft.combatoverhaul.ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.libercraft.combatoverhaul.Main;

public enum AbilityScroll {
	RAIN("Razor Rain", ChatColor.DARK_GREEN, WeaponType.BOW),
	VOLLEY("Spreadshot", ChatColor.DARK_GREEN, WeaponType.BOW),
	ZOOM("Hawk Eye", ChatColor.DARK_GREEN, WeaponType.BOW),
	WALL("Wall", ChatColor.DARK_GREEN, WeaponType.BOW),
	TELEPORT("Teleport", ChatColor.DARK_RED, WeaponType.SWORD);
	
	private enum WeaponType {
		SWORD,
		BOW;
	}
	
	private String itemName;
	private WeaponType type;
	private ChatColor color;
	private List<String> lore;

	AbilityScroll(String itemName, ChatColor color, WeaponType type) {
		this.itemName = itemName;
		this.type = type;
		this.color = color;
		lore = new ArrayList<String>();
		lore.add(ChatColor.GREEN + "Ability:");
		lore.add(color + itemName);
	}
	
	public ItemStack create() {
		ItemStack item = new ItemStack(Material.EMPTY_MAP, 1);
		
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, true);
		itemMeta.setDisplayName(itemName);
		itemMeta.setLore(lore);
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		item.setItemMeta(itemMeta);
		return item;
	}
	
	public BaseAbility cast(Main plugin, Player player) {
		switch (this) {
		case RAIN:
			return new RainAbility(plugin, player);
		case VOLLEY:
			return new VolleyAbility(plugin, player);
		case ZOOM:
			return new ZoomAbility(plugin, player);
		case WALL:
			return new WallAbility(plugin, player);
		case TELEPORT:
			return new TeleportAbility(plugin, player);
		default:
			return null;
		}
	}
	
	// Checks if a particular item is considered an ability;
	public static boolean isAbility(ItemStack item) {
		if (item == null) return false;
		
		if (item.getAmount() != 1) return false;
		if (!item.getItemMeta().hasLore()) return false;
		
		List<String> lore = item.getItemMeta().getLore();
		if (!lore.get(0).equals(ChatColor.GREEN + "Ability:")) return false;
		
		return true;
	}
	
	public static AbilityScroll fromItem(ItemStack item) {
		if (!item.getItemMeta().hasLore()) return null;
		
		for (AbilityScroll ability:AbilityScroll.values()) {
			if (item.getItemMeta().getLore().get(1).equals(ability.color + ability.itemName)) return ability;
		}
		return null;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public WeaponType getWeaponType() {
		return type;
	}
}
