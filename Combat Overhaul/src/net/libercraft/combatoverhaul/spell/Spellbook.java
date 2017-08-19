package net.libercraft.combatoverhaul.spell;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;
import net.libercraft.combatoverhaul.player.Caster;

public enum Spellbook implements Tracer {
	FIRE("Ignis", 1),
	WATER("Water Splash", 1),
	LAVA("Lava Ball", 2),
	ICE("Ice Spike", 2),
	TELEPORT("Teleport", 1),
	WALL("Wall", 1),
	ENERGY("Energy Strike", 2);
	

	public String itemName;
	public int cost;
	
	Spellbook(String itemName, int cost) {
		this.itemName = itemName;
		this.cost = cost;
	}
	

	public BaseSpell cast(Main plugin, Player player) {
		switch (this) {
		case FIRE:
			return new FireSpell(plugin, player, cost);
		case WATER:
			return new WaterSpell(plugin, player, cost);
		case LAVA:
			return new LavaSpell(plugin, player, cost);
		case ICE:
			return new IceSpell(plugin, player, cost);
		case ENERGY:
			return new EnergySpell(plugin, player, cost);
		case TELEPORT:
			return new TeleportSpell(plugin, player, cost);
		default:
			return null;
		}
	}
	
	public BaseSpell dualCast(Main plugin, Player player) {
		switch (this) {
		case FIRE:
			return new LavaSpell(plugin, player, cost);
		case WATER:
			return new IceSpell(plugin, player, cost);
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
