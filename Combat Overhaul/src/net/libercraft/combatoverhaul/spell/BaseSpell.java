package net.libercraft.combatoverhaul.spell;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.player.Caster;

public abstract class BaseSpell {

	public Main plugin;
	public Caster caster;
	public boolean cast;
	public int cost;
	
	public abstract void onCastEffect(World world, Location location);
	
	public void onCast(Main plugin, Player player) {
		this.plugin = plugin;
		this.caster = plugin.getCaster(player);
		cast = true;
		
		player.setCooldown(Material.BOOK, 25);
		caster.decreaseMana(cost);
		onCastEffect(player.getWorld(), getFirstPersonHandLocation(caster));
	}
	
	private static Location getFirstPersonHandLocation(Caster caster) {
		String spellHand = null;
		if (caster.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.AIR)) spellHand = "OFF";
		if (caster.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)) spellHand = "MAIN";
		if (spellHand == null) return null;
		
		String mainHand = null;
		if (caster.getPlayer().getMainHand().equals(MainHand.LEFT)) mainHand = "LEFT";
		if (caster.getPlayer().getMainHand().equals(MainHand.RIGHT)) mainHand = "RIGHT";
		if (mainHand == null) return null;
		
		Location loc1 = caster.getPlayer().getEyeLocation();
		Vector vec1 = caster.getPlayer().getEyeLocation().getDirection();
		Vector vec2 = vec1.clone().crossProduct(new Vector(0, 1, 0));
		vec2.normalize();
		vec2.multiply(0.4);
		
		// Place the particle location at the correct hand
		if (mainHand.equals("LEFT") && spellHand.equals("MAIN")) vec2.multiply(-1);
		if (mainHand.equals("RIGHT") && spellHand.equals("OFF")) vec2.multiply(-1);
		
		vec2.add(vec1.normalize().multiply(0.8));
		
		loc1.setY(loc1.getY() - 0.05);
		Location loc2 = loc1.add(vec2);
		
		return loc2;
	}
	
	public static void activeEffect(Caster caster, Spellbook type) {
		Location loc2 = getFirstPersonHandLocation(caster);
		
		// switch statement on castable type and call static function in that class
		switch(type) {
		case FIRE:
			FireSpell.handEffect(caster, loc2);
			return;
		case WATER:
			WaterSpell.handEffect(caster, loc2);
			return;
		case ENERGY:
			EnergySpell.handEffect(loc2);
			return;
		case TELEPORT:
			EnergySpell.handEffect(loc2);
			return;
		case WALL:
			EnergySpell.handEffect(loc2);
			return;
		default:
			break;
		}
	}
}
