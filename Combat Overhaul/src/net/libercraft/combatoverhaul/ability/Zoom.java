package net.libercraft.combatoverhaul.ability;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.libercraft.combatoverhaul.Main;

public class Zoom extends BaseAbility {

	public Zoom(Main plugin, Player player) {
		onCast(plugin, player);
		
		PotionEffect zoom = new PotionEffect(PotionEffectType.SLOW, 999999, 100);
		if (caster.getPlayer().hasMetadata("zoomed")) {
			caster.getPlayer().removeMetadata("zoomed", plugin);
			caster.getPlayer().removePotionEffect(zoom.getType());
		} else {
			caster.getPlayer().setMetadata("zoomed", new FixedMetadataValue(plugin, "zoomed"));
			caster.getPlayer().addPotionEffect(zoom);
		}
		
		caster.getPlayer().setCooldown(Material.BOW, 25);
	}
}
