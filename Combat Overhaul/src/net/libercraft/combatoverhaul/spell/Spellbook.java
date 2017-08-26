package net.libercraft.combatoverhaul.spell;

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
import net.libercraft.combatoverhaul.Tracer;
import net.libercraft.combatoverhaul.managers.Caster;
import net.libercraft.combatoverhaul.particleanimator.ParticleSprite;
import net.libercraft.combatoverhaul.spell.spellbook.EnergySpell;
import net.libercraft.combatoverhaul.spell.spellbook.FireSpell;
import net.libercraft.combatoverhaul.spell.spellbook.IceSpell;
import net.libercraft.combatoverhaul.spell.spellbook.LavaSpell;
import net.libercraft.combatoverhaul.spell.spellbook.WaterSpell;

public enum Spellbook implements Tracer {
	FIRE("Ignirium", ChatColor.RED, ParticleSprite.SINGLE_FIRE, 2), // ignirium
	WATER("Aquaria", ChatColor.AQUA, ParticleSprite.SINGLE_WATER, 2), // aquarius
	ENERGY("Electrus", ChatColor.WHITE, ParticleSprite.SINGLE_ENERGY, 2); // electria
	
	private String itemName;
	private List<String> lore;
	private ParticleSprite handEffect;
	private ChatColor color;
	private int cost;
	
	Spellbook(String itemName, ChatColor color, ParticleSprite handEffect, int cost) {
		this.itemName = itemName;
		this.handEffect = handEffect;
		this.color = color;
		this.cost = cost;
		lore = new ArrayList<String>();
		lore.add(ChatColor.DARK_AQUA + "Spell:");
		lore.add(color + itemName);
		lore.add(ChatColor.DARK_AQUA + "" + cost + " Mana");
	}
	
	public ItemStack create() {
		ItemStack item = new ItemStack(Material.BOOK, 1);
		
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, true);
		itemMeta.setDisplayName(itemName);
		itemMeta.setLore(lore);
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		item.setItemMeta(itemMeta);
		return item;
	}

	public BaseSpell cast(Main plugin, Player player) {
		switch (this) {
		case FIRE:
			return new FireSpell(plugin, player, cost);
		case WATER:
			return new WaterSpell(plugin, player, cost);
		case ENERGY:
			return new EnergySpell(plugin, player, cost);
		default:
			return null;
		}
	}
	
	public BaseSpell dualCast(Main plugin, Player player) {
		switch (this) {
		case FIRE:
			return new LavaSpell(plugin, player, cost + 1);
		case WATER:
			return new IceSpell(plugin, player, cost + 1);
		default:
			return null;
		}
	}
	
	// Check if a particular item is considered a spell;
	public static boolean isSpell(ItemStack item) {
		if (item == null) return false;
		
		if (item.getAmount() != 1) return false;
		if (!item.getItemMeta().hasLore()) return false;
		
		List<String> lore = item.getItemMeta().getLore();
		if (!lore.get(0).equals(ChatColor.DARK_AQUA + "Spell:")) return false;
		
		return true;
	}
	
	public void handEffect(Caster caster) {
		handEffect.summon(caster.getThirdPersonHandLocation());
	}
	
	public static Spellbook fromItem(ItemStack item) {
		if (!item.getItemMeta().hasLore()) return null;
		
		for (Spellbook spell:Spellbook.values()) {
			if (item.getItemMeta().getLore().get(1).equals(spell.color + spell.itemName)) return spell;
		}
		return null;
	}
	
	public int getCost() {
		return cost;
	}
	
	public List<String> getLore() {
		return lore;
	}
	
	public String getItemName() {
		return itemName;
	}
}
