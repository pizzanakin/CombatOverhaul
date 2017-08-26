package net.libercraft.combatoverhaul.spell;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;
import net.libercraft.combatoverhaul.managers.Caster;

public abstract class BaseSpell implements Tracer {

	public Main plugin;
	public Caster caster;
	public boolean cast;
	
	public abstract void onCastEffect(World world, Location location);
	
	public void onCast(Main plugin, Player player, int cost) {
		this.plugin = plugin;
		this.caster = plugin.getCasterManager().get(player);
		cast = true;
		player.setCooldown(Material.BOOK, 25);
		
		onCastEffect(player.getWorld(), caster.getThirdPersonHandLocation());
		if (player.getGameMode().equals(GameMode.CREATIVE)) return;
		caster.decreaseMana(cost);
	}
}
