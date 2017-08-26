package net.libercraft.combatoverhaul.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.libercraft.combatoverhaul.ArmorStandPlacer;
import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;
import net.libercraft.combatoverhaul.managers.Altar.AltarMode;
import net.libercraft.combatoverhaul.managers.Altar.AltarType;

public class AltarManager extends YamlConfiguration implements Listener, Tracer {

	private Main plugin;
	private List<Altar> altars;
	
	public AltarManager(Main plugin) {
		this.plugin = plugin;
        altars = new ArrayList<Altar>();
	}
	
	// -- LIST METHODS -- //
	
	public int size() {
		return altars.size();
	}
	
	public List<Altar> altars() {
		return altars;
	}
	
	public Altar get(Block block) {
		for (Altar altar:altars) {
			if (altar.getBlock().equals(block)) return altar;
		}
		return null;
	}
	
	public Altar get(int i) {
		return altars.get(i);
	}
	
	public void loadAltars() {
		while (length() != 0) {
			altars.add(getEntry(0));
			removeEntry(0);
		}
	}
	
	public void storeAltars() {
		while (altars.size() != 0) {
			Altar altar = altars.get(0);
			addEntry(altar);
			altars.remove(altar);
		}
	}
	
	// -- DATABASE METHODS -- //
	
	private int length() {
		
		int length = 0;
		while (get(length + ".world") != null) {
			length++;
		}
		return length;
	}
	

	private void removeEntry(int index) {
		
		for (int i = index; i < length() - 1; i++) {
			set(i + ".world", get((i+1) + ".world"));
			set(i + ".x", get((i+1) + ".x"));
			set(i + ".y", get((i+1) + ".y"));
			set(i + ".z", get((i+1) + ".z"));
			set(i + ".type", get((i+1) + ".type"));
			set(i + ".mode", get((i+1) + ".mode"));
			set(i + ".stage", get((i+1) + ".stage"));
		}
		
		int lastIndex = length() - 1;
		// Remove the entry from the database
		set(lastIndex + ".world", null);
		set(lastIndex + ".x", null);
		set(lastIndex + ".y", null);
		set(lastIndex + ".z", null);
		set(lastIndex + ".type", null);
		set(lastIndex + ".mode", null);
		set(lastIndex + ".stage", null);
		
		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Altar getEntry(int index) {
		if (index >= length() || index < 0) return null;
		
		World world = plugin.getServer().getWorld(UUID.fromString(getString(index + ".world")));
		double x = getDouble(index + ".x");
		double y = getDouble(index + ".y");
		double z = getDouble(index + ".z");
		
		AltarType type = AltarType.valueOf(getString(index + ".type"));
		AltarMode mode = AltarMode.valueOf(getString(index + ".mode"));
		int stage = getInt(index + ".stage");
		
		return new Altar(plugin, new Location(world, x, y, z), mode, type, stage);
	}
	
	private boolean addEntry(Altar entry) {
		int index = length();
		
		set(index + ".world", entry.getLocation().getWorld().getUID().toString());
		set(index + ".x", entry.getBlock().getLocation().getX());
		set(index + ".y", entry.getBlock().getLocation().getY());
		set(index + ".z", entry.getBlock().getLocation().getZ());
		
		set(index + ".type", entry.getType().toString());
		set(index + ".mode", entry.getMode().toString());
		set(index + ".stage", entry.getStage());
		
		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	// -- EVENT LISTENERS -- //
	
	@EventHandler
	public void preventTakeItem(PlayerArmorStandManipulateEvent e) {
		
		for (String tag:e.getRightClicked().getScoreboardTags()) {
			if (tag.equals("STATUE")) e.setCancelled(true);
		}
	}
	
	@EventHandler 
	public void advanceAltarStage(EntityDeathEvent e) {
		
		// Check if the killer is a player
		if (!(e.getEntity().getKiller() instanceof Player)) return;
		Player killer = e.getEntity().getKiller();

		// Check if the killer was near an altar;
		for (int i = 0; i < 20; i++) {
			for (Altar altar:altars) {
				if (altar.getLocation().distance(killer.getLocation()) > i) continue;
				if (!altar.getMode().equals(AltarMode.CHARGED)) continue;
				altar.advance();
				
				if (altar.getStage() >= 4) {
					altar.getWorld().dropItem(altar.getLocation(), altar.getSpellbook().create());
					altar.setStage(0);
					altar.setMode(AltarMode.EMPTY);
				}
				return;
			}
		}
	}
	
	@EventHandler
	public void protectEnchantmentTable(BlockBreakEvent e) {

		// Make sure the event wasn't cancelled;
		if (e.isCancelled()) return;
		
		// Check if the block is an enchantment table;
		if (!e.getBlock().getType().equals(Material.ENCHANTMENT_TABLE)) return;
		
		// Check if the enchantment table was put in an altar;
		for (Altar altar:altars) {
			if (e.getBlock().equals(altar.getBlock())) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void preventBlockPlace(BlockPlaceEvent e) {
		
		// Make sure the event wasn't cancelled;
		if (e.isCancelled()) return;
		
		// Check if the enchantment table was placed on an altar;
		for (Altar altar:altars) {
			if (e.getBlock().equals(altar.getBlock())) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void chargeAltar(BlockPlaceEvent e) {
		
		// Make sure the event wasn't cancelled;
		if (e.isCancelled()) return;
		
		// Check if the block is an enchantment table;
		if (!e.getBlock().getType().equals(Material.DIAMOND_BLOCK)) return;
		
		for (Altar altar:altars) {
			if (!altar.getBlock().equals(e.getBlock())) continue;
			if (altar.getMode().equals(AltarMode.CHARGED)) continue;
			e.getBlockPlaced().setType(Material.ENCHANTMENT_TABLE);
			altar.setMode(AltarMode.CHARGED);
		}
	}
	
	@EventHandler
	public void buildAltar(BlockPlaceEvent e) {
		
		// Make sure the event wasn't cancelled;
		if (e.isCancelled()) return;
		
		// Check if the placed block is a diamond block;
		if (!e.getBlock().getType().equals(Material.DIAMOND_BLOCK)) return;
		
		// Check if the diamond block was placed at an altar;
		Location location = e.getBlock().getLocation();
		if (!location.clone().add(0, -1, 0).getBlock().getType().equals(Material.LAPIS_BLOCK)) return;
		
		// Find the type of altar to be created;
		AltarType type = null;
		for (AltarType types:AltarType.values()) {
			if (types.buildmaterial.equals(location.clone().add(1, 0, 1).getBlock().getType())) type = types;
		}
		if (type == null) return;
		
		// Compare the structure of the altar to the blueprint;
		int index = 0;
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				for (int z = -1; z < 2; z++) {
					
					if (Altar.getAltarBlueprint(type)[index] == null) {
						index++;
						continue;
					}

					if (!location.clone().add(x, y, z).getBlock().getType().equals(Altar.getAltarBlueprint(type)[index])) return;
					index++;
				}
			}
		}
		
		// Create the altar instance;
		Altar altar = new Altar(plugin, location, AltarMode.EMPTY, type, 0);
		
		// Remove all the blocks around the area of the altar
		for (int x = -1; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = -1; z < 2; z++) {
					location.clone().add(x, y, z).getBlock().setType(Material.AIR);
				}
			}
		}
		
		// Change the floor of the altar;
		for (int x = -2; x < 3; x++) {
			for (int z = -1; z < 2; z++) {
				location.clone().add(x, -1, z).getBlock().setType(type.primarymaterial);
			}
		}
		for (int x = -1; x < 2; x++) {
			for (int z = -2; z < 3; z++) {
				location.clone().add(x, -1, z).getBlock().setType(type.primarymaterial);
			}
		}
		
		location.clone().add(1, -1, 0).getBlock().setType(Material.OBSIDIAN);
		location.clone().add(-1, -1, 0).getBlock().setType(Material.OBSIDIAN);
		location.clone().add(0, -1, -1).getBlock().setType(Material.OBSIDIAN);
		location.clone().add(0, -1, 1).getBlock().setType(Material.OBSIDIAN);
		location.clone().add(0, -1, 0).getBlock().setType(Material.DIAMOND_BLOCK);
		
		// Position the four pillars of the altar;
		for (int x = -1; x < 2; x++) {
			if (x == 0) continue;
			for (int z = -1; z < 2; z++) {
				if (z == 0) continue;
				
				Location standLocation = location.clone().add((-1.5 * x) + 0.5, -1.375, (-1.5 * z) + 0.5);
				
				// -- CORNER 1 --
				ArmorStandPlacer.createArmorStand(plugin, standLocation, false, type.secondaryMaterial, type.data, 0.5 * x, 0, 0.5 * z, z * 0, 0, x * 0, "ALTARSTAND");
				ArmorStandPlacer.createArmorStand(plugin, standLocation, false, type.secondaryMaterial, type.data, 0.5 * x, 0.55, 0.5 * z, z * 5, 0, x * 5, "ALTARSTAND");
				ArmorStandPlacer.createArmorStand(plugin, standLocation, false, type.secondaryMaterial, type.data, 0.55 * x, 1.1, 0.55 * z, z * 10, 0, x * 10, "ALTARSTAND");
				ArmorStandPlacer.createArmorStand(plugin, standLocation, true, type.secondaryMaterial, type.data, 0.7 * x, 2.35, 0.7 * z, z * 15, 0, x * 15, "ALTARSTAND");
				ArmorStandPlacer.createArmorStand(plugin, standLocation, true, type.secondaryMaterial, type.data, 0.81 * x, 2.725, 0.81 * z, z * 20, 0, x * 20, "ALTARSTAND");
				
				// -- CORNER 4 --
				ArmorStandPlacer.createArmorStand(plugin, standLocation, true, type.primarymaterial, type.data, 0.5 * x, 0.9, 0.0 * z, z * -90, 0, 0, "ALTARSTAND");
				ArmorStandPlacer.createArmorStand(plugin, standLocation, true, type.primarymaterial, type.data, 0.5 * x, 1.2, 0.05 * z, z * -70, 0, 0, "ALTARSTAND");
				
				// -- CORNER 5 --
				ArmorStandPlacer.createArmorStand(plugin, standLocation, true, type.primarymaterial, type.data, 0.0 * x, 0.9, 0.5 * z, 0, 0, x * -90, "ALTARSTAND");
				
				ArmorStandPlacer.createArmorStand(plugin, standLocation, true, type.primarymaterial, type.data, 0.05 * x, 1.2, 0.5 * z, 0, 0, x * -70, "ALTARSTAND");
			}
		}
		ArmorStandPlacer.createArmorStand(plugin, location.clone().add(0.5, 0, 0.5), false, Material.OBSIDIAN, (byte) 0, 0.8, -1.7, 0.0, 0.0, 0.0, 15.0, "ALTARSTAND");
		ArmorStandPlacer.createArmorStand(plugin, location.clone().add(0.5, 0, 0.5), false, Material.OBSIDIAN, (byte) 0, -0.8, -1.7, 0.0, 0.0, 0.0, -15.0, "ALTARSTAND");
		ArmorStandPlacer.createArmorStand(plugin, location.clone().add(0.5, 0, 0.5), false, Material.OBSIDIAN, (byte) 0, 0.0, -1.7, 0.8, 15.0, 0.0, 0.0, "ALTARSTAND");
		ArmorStandPlacer.createArmorStand(plugin, location.clone().add(0.5, 0, 0.5), false, Material.OBSIDIAN, (byte) 0, 0.0, -1.7, -0.8, -15.0, 0.0, 0.0, "ALTARSTAND");

		// Play the creation sound 1 tick later
		new BukkitRunnable() {
			@Override public void run() {
				altar.getWorld().playSound(location, Sound.BLOCK_END_PORTAL_SPAWN, 1, 2);
			}
		}.runTaskLater(plugin, 1);
		
		altars.add(altar);
	}
	
	@EventHandler
	public void preventBreakAltar(BlockBreakEvent e) {
		
		// Make sure the event wasn't cancelled;
		if (e.isCancelled()) return;
		
		for (Altar altar:altars) {
			if (
					altar.getBlock().getLocation().add(1, -1, 0).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(-1, -1, 0).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(0, -1, 1).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(0, -1, -1).getBlock().equals(e.getBlock())||
					
					altar.getBlock().getLocation().add(1, -1, 1).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(-1, -1, 1).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(-1, -1, -1).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(1, -1, -1).getBlock().equals(e.getBlock())||
					
					altar.getBlock().getLocation().add(2, -1, -1).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(2, -1, 0).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(2, -1, 1).getBlock().equals(e.getBlock())||
					
					altar.getBlock().getLocation().add(-2, -1, -1).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(-2, -1, 0).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(-2, -1, 1).getBlock().equals(e.getBlock())||
					
					altar.getBlock().getLocation().add(-1, -1, 2).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(0, -1, 2).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(1, -1, 2).getBlock().equals(e.getBlock())||
					
					altar.getBlock().getLocation().add(-1, -1, -2).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(0, -1, -2).getBlock().equals(e.getBlock())||
					altar.getBlock().getLocation().add(1, -1, -2).getBlock().equals(e.getBlock())
					) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void breakAltar(BlockBreakEvent e) {
		
		// Make sure the event wasn't cancelled;
		if (e.isCancelled()) return;
		
		// Check if the broken block was a diamond block;
		if (!e.getBlock().getType().equals(Material.DIAMOND_BLOCK)) return;
		
		// Check if it was an altar, and remove it;
		Altar altar = get(e.getBlock().getLocation().add(0, 1, 0).getBlock());
		if (altar == null) return;
		
		// Execute altar kill;
		e.setCancelled(true);
		altar.killAltar();
		altars.remove(altar);
		
		// Spawn diamond blocks if the player was in survival mode;
		if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
		e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.DIAMOND_BLOCK, 1));
		if (altar.getMode().equals(AltarMode.CHARGED)) e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.DIAMOND_BLOCK, 1));
	}
}
