package net.libercraft.combatoverhaul.spell;

import org.bukkit.entity.Player;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.player.Caster;

public enum Spellbook {
	FIRE("Ignis"),
	WATER("Water Splash"),
	LAVA("Lava Ball"),
	ICE("Ice Spike"),
	TELEPORT("Teleport"),
	WALL("Wall"),
	ENERGY("Energy Strike");
	

	public String itemName;
	public int cost;
	
	Spellbook(String itemName) {
		this.itemName = itemName;
	}
	

	public BaseSpell cast(Main plugin, Player player) {
		switch (this) {
		case FIRE:
			return new FireSpell(plugin, player);
		case WATER:
			return new WaterSpell(plugin, player);
		case LAVA:
			return new LavaSpell(plugin, player);
		case ICE:
			return new IceSpell(plugin, player);
		case ENERGY:
			return new EnergySpell(plugin, player);
		case TELEPORT:
			return new TeleportSpell(plugin, player);
		default:
			return null;
		}
	}
	
	public BaseSpell dualCast(Main plugin, Player player) {
		switch (this) {
		case FIRE:
			return new LavaSpell(plugin, player);
		case WATER:
			return new IceSpell(plugin, player);
		default:
			return null;
		}
	}
	
	public void handEffect(Caster caster) {
		BaseSpell.activeEffect(caster, this);
	}
	
	public static Spellbook fromBookName(String string) {
		for (Spellbook spell:Spellbook.values()) {
			if (spell.itemName.equals(string)) return spell;
		}
		return null;
	}
}
