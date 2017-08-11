package net.libercraft.combatoverhaul.ability;

import org.bukkit.entity.Player;

import net.libercraft.combatoverhaul.Main;

public enum Ability {
	RAIN("Razor Rain"),
	VOLLEY("Spreadshot"),
	ZOOM("Hawk Eye");
	
	public String itemName;

	Ability(String itemName) {
		this.itemName = itemName;
	}
	
	public BaseAbility cast(Main plugin, Player player) {
		switch (this) {
		case RAIN:
			return new ArrowRain(plugin, player);
		case VOLLEY:
			return new Volley(plugin, player);
		case ZOOM:
			return new Zoom(plugin, player);
		default:
			return null;
		}
	}
	
	public static Ability fromMapName(String string) {
		for (Ability ability:Ability.values()) {
			if (ability.itemName.equals(string)) return ability;
		}
		return null;
	}
}
