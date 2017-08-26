package net.libercraft.combatoverhaul.ability;

import org.bukkit.entity.Player;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.managers.Caster;

public class BaseAbility {

	public Main plugin;
	public Caster caster;
	
	public void onCast(Main plugin, Player player) {
		this.plugin = plugin;
		this.caster = plugin.getCasterManager().get(player);
	}
	
}
