package net.libercraft.combatoverhaul;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public interface ArmorStandPlacer {

	public static ArmorStand createArmorStand(Main plugin, Location location, boolean small, Material mat, byte data, double x, double y, double z, double pitch, double yaw, double roll, String tag) {
		Location loc = location.clone();
		loc.add(x, y, z);
		ArmorStand armorstand = loc.getWorld().spawn(loc, ArmorStand.class);
		armorstand.setGravity(false);
		armorstand.setSmall(small);
		armorstand.setInvulnerable(true);
		armorstand.setVisible(false);
		ItemStack helmet = new ItemStack(mat, 1); 
		armorstand.setHelmet(helmet);
		
		armorstand.addScoreboardTag(tag);
		armorstand.addScoreboardTag("STATUE");
		
		armorstand.setHeadPose(new EulerAngle(pitch * (Math.PI/180), yaw * (Math.PI/180), roll * (Math.PI/180)));
		return armorstand;
	}
}
