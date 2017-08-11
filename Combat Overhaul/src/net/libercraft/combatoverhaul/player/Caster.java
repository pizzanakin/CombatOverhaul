package net.libercraft.combatoverhaul.player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Glow;
import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;
import net.libercraft.combatoverhaul.ability.Ability;
import net.libercraft.combatoverhaul.ability.Volley;
import net.libercraft.combatoverhaul.spell.Spellbook;

public class Caster implements Tracer {

	
	/// -===- CASTER CODE -===- ///
	
		// -- Caster Variables -- //
	public Main plugin;
	public Player player;
	public ItemStack mainItem;
	public ItemStack offItem;
	
	
		// -- Body Code -- //
	private float bodyYaw;
	public boolean walking;
	//private Location oldLocation;
	
	public void resetRotation() {
		World world = player.getWorld();
		double x = player.getLocation().getX();
		double y = player.getLocation().getY();
		double z = player.getLocation().getZ();
		bodyYaw = 0;
		float pitch = player.getLocation().getPitch();
		player.teleport(new Location(world, x, y, z, 0, pitch));
	}
	
	public void walkSideways(String direction) {
		if (direction == "RIGHT") {
			
		}
		if (direction == "LEFT") {
			
		}
	}
	
	public void updatePlayerRotation(PlayerMoveEvent e) {
		
		if (e.getTo().getX() != e.getFrom().getX() || e.getTo().getZ() != e.getFrom().getZ()) {
			// Compare walking direction and player eye direction to see if the player is walking straight
			double x = e.getTo().getX() - e.getFrom().getX();
			double z = e.getTo().getZ() - e.getFrom().getZ();
			
			Vector walkDirection = new Vector(x, 0, z);
			Vector playerDirection = player.getLocation().getDirection();
			playerDirection.setY(0);
			
			double length = walkDirection.crossProduct(playerDirection).length();
			trace(length);
			if (length > 0.1) {
				//trace("sideways");
			}
			else {
				//trace("straight");
				bodyYaw = e.getTo().getYaw();
			}
		}
		
		if (e.getTo().getYaw() != e.getFrom().getYaw()) {
			// Compare the player eye direction to the body rotation and update the body rotation if necessary
			float faceYaw = player.getLocation().getYaw();
			float difference = faceYaw - bodyYaw;
			if (difference > 50) bodyYaw = faceYaw - 50;
			if (difference < -50) bodyYaw = faceYaw + 50;
			trace("faceYaw: " + faceYaw);
			trace("bodyYaw: " + bodyYaw);
		}
	}
	
	
	
	
		// -- Caster Methods -- //
	public Caster(Player player, Main plugin) {
		this.player = player;
		this.plugin = plugin;
		passiveAbilities = new ArrayList<Ability>();
		passiveSpells = new ArrayList<Spellbook>();
		
		new BukkitRunnable() {
			@Override public void run() {
				if (!exists()) this.cancel();
				passiveAbilities.clear();
				passiveSpells.clear();
				for (int i = 0; i < 40; i++) {
					ItemStack item = player.getInventory().getItem(i);
					if (item == null) continue;
					if (!item.getItemMeta().hasEnchant(new Glow(80))) continue;
					if (item.getType().equals(Material.EMPTY_MAP)) passiveAbilities.add(Ability.fromMapName(item.getItemMeta().getDisplayName()));
					if (item.getType().equals(Material.BOOK)) passiveSpells.add(Spellbook.fromBookName(item.getItemMeta().getDisplayName()));
				}
				mainItem = player.getInventory().getItemInMainHand();
				offItem = player.getInventory().getItemInOffHand();
			}
		}.runTaskTimer(plugin, 0, 1);
		
		new BukkitRunnable() {
			@Override public void run() {
				if ((mainItem.getType().equals(Material.AIR)|offItem.getType().equals(Material.AIR))&&activeSpell != null) activeSpell.handEffect(plugin.getCaster(player));
			}
		}.runTaskTimer(plugin, 0, 11);
	}
	
	private boolean exists() {
		return plugin.getCasters().contains(this);
	}
	
	// Return the player instance of this caster;
	public Player getPlayer() {
		return player;
	}
	
	// Check if the player can execute anything by left clicking;
	public boolean handleLeftClick() {
		
		// If player has bow in main hand, switch to next ability;
		if (mainItem.getType().equals(Material.BOW) && passiveAbilities.size() > 0) {
			int index = passiveAbilities.indexOf(activeAbility);
			if (passiveAbilities.size() > index + 1) activeAbility = passiveAbilities.get(index + 1);
			else activeAbility = passiveAbilities.get(0);
			player.sendMessage("New Ability: " + activeAbility.itemName);
			return true;
		}
		
		// If player has staff in main hand, switch to next spell;
		/*if (mainItem.getType().equals(Material.AIR) && passiveSpells.size() > 0) {
			int index = passiveSpells.indexOf(activeSpell);
			if (passiveSpells.size() > index + 1) activeSpell = passiveSpells.get(index + 1);
			else activeSpell = passiveSpells.get(0);
			player.sendMessage("New Spell: " + activeSpell.itemName);
			return true;
		}*/
		
		// If player has a staff in offhand, cast spell;
		if (mainItem.getType().equals(Material.AIR) && canCastSpell()) {
			activeSpell.cast(plugin, player);
			return true;
		}

		return false;
	}
	
	// Check if the player can execute anything by right clicking;
	public boolean handleRightClick() {
		
		// If player has an ability in main hand, select the ability;
		if (isAbility(mainItem)) {
			activeAbility = Ability.fromMapName(mainItem.getItemMeta().getDisplayName());
			player.sendMessage("New Ability: " + activeAbility.itemName);
			return true;
		}
		
		if (isSpell(mainItem)) {
			activeSpell = Spellbook.fromBookName(mainItem.getItemMeta().getDisplayName());
			player.sendMessage("New Spell: " + activeSpell.itemName);
			return true;
		}
		
		// If player has a staff in main hand, switch to next spell;
		/*if (offItem == null && player.getMainHand().equals(MainHand.LEFT) && passiveSpells.size() > 0) {
			int index = passiveSpells.indexOf(activeSpell);
			if (passiveSpells.size() > index + 1) activeSpell = passiveSpells.get(index + 1);
			else activeSpell = passiveSpells.get(0);
			return true;
		}*/
		
		// If player has a staff in offhand, cast spell;
		if (offItem.getType().equals(Material.AIR) && canCastSpell()) {
			activeSpell.cast(plugin, player);
			return true;
		}
		
		return false;
	}
	
	// Check if the player can execute anything by double left clicking;
	public boolean handleDoubleLeftClickEvent() {return false;}
	
	// Check if the player can execute anything by double right clicking;
	public boolean handleDoubleRightClickEvent() {
		if (isBow(mainItem) && canCastAbility()) {
			if (activeAbility.equals(Ability.VOLLEY)) {
				volley = (Volley) activeAbility.cast(plugin, player);
			} else activeAbility.cast(plugin, player);
			return true;
		}
		
		return false;
	}
	
	// Check if the player can execute anything by dual clicking;
	public boolean handleDualClickEvent() {
		if (activeSpell != null) {
			activeSpell = null;
			return true;
		}
		return false;
	}
	
	/// -===- ABILITY CODE -===- ///
	
		// -- Ability Variables -- //
	private Ability activeAbility;
	private List<Ability> passiveAbilities;
	public boolean hasActivedVolley;
	public Volley volley;
	
	// -- Ability Methods -- //
	
	// Checks if the caster can cast an ability
	private boolean canCastAbility() {
		if (!hasArrow()) return false;
		if (activeAbility == null) return false;
		if (player.hasCooldown(Material.BOW)) return false;
		return true;
	}
	
	// Checks if the caster has an arrow in their inventory;
	private boolean hasArrow() {
		if (player.getGameMode().equals(GameMode.CREATIVE)) return true;
		if ((getFirstArrowSlot() <= -1 || getFirstArrowSlot() > 40)) return false;
		return true;
	}
	
	// Returns the first slot in the inventory that contains an arrow;
	private int getFirstArrowSlot() {
		Material type = player.getInventory().getItemInOffHand().getType();
		if (type == Material.ARROW|type == Material.TIPPED_ARROW|type == Material.SPECTRAL_ARROW) return 40;
		for (int i = 0; i < 40; i++) {
			if (player.getInventory().getItem(i) == null) continue;
			type = player.getInventory().getItem(i).getType();
			if (type == Material.ARROW|type == Material.TIPPED_ARROW|type == Material.SPECTRAL_ARROW) return i;
		}
		return -1;
	}
	
	// Returns the projectile class to be fired from the caster;
	public Class<? extends Projectile> getArrowType() {
		int index = getFirstArrowSlot();
		if ((index == -1|index > 40)&&player.getGameMode().equals(GameMode.CREATIVE)) return Arrow.class;
		
		ItemStack item = player.getInventory().getItem(index);
		if (item.getType().equals(Material.ARROW)) return Arrow.class;
		if (item.getType().equals(Material.SPECTRAL_ARROW)) return SpectralArrow.class;
		if (item.getType().equals(Material.TIPPED_ARROW)) return TippedArrow.class;
		return null;
	}
	
	// Returns the effect to be given to arrows fired from the caster;
	public PotionData getArrowEffect() {
		int index = getFirstArrowSlot();
		if (index == -1|index > 40) return null;
		
		ItemStack item = player.getInventory().getItem(index);
		if (!item.getType().equals(Material.TIPPED_ARROW)) return null;
		return ((PotionMeta) item.getItemMeta()).getBasePotionData();
	}
	
	// Removes an arrow from the caster;
	public boolean removeArrow() {
		if (player.getGameMode().equals(GameMode.CREATIVE)) return true;
		if (!hasArrow()) return false;
		
		int index = getFirstArrowSlot();
		ItemStack arrows = player.getInventory().getItem(index);
		arrows.setAmount(arrows.getAmount() - 1);
		return true;
	}
	
	// Decrease the durability of the casters' bow;
	public void decreaseBowDurability() {
		if (!mainItem.getType().equals(Material.BOW)) return;
		mainItem.setDurability((short) (mainItem.getDurability() + 1));
	}
	
	// Checks if a particular item is considered an ability;
	private boolean isAbility(ItemStack item) {
		if (item.getAmount() != 1) return false;
		if (!item.getType().equals(Material.EMPTY_MAP)) return false;
		if (!item.getItemMeta().hasEnchant(new Glow(80))) return false;
		return true;
	}
	
	// Checks if a particular item is considered a bow;
	private boolean isBow(ItemStack item) {
		if (!item.getType().equals(Material.BOW)) return false;
		return true;
	}
	
	// Checks if a particular item is considered a sword;
	private boolean isSword(ItemStack item) {
		if (
				!item.getType().equals(Material.DIAMOND_SWORD)&&
				!item.getType().equals(Material.GOLD_SWORD)&&
				!item.getType().equals(Material.IRON_SWORD)&&
				!item.getType().equals(Material.STONE_SWORD)&&
				!item.getType().equals(Material.WOOD_SWORD)
				) return false;
		return true;
	}

	
	/// -===- SPELL CODE -===- ///
	
		// -- Spell Variables -- //
	private Spellbook activeSpell;
	private List<Spellbook> passiveSpells;

	// -- Spell Methods -- //
	
	// Checks if the player is able to cast a spell;
	private boolean canCastSpell() {
		if (isBow(mainItem)||isSword(mainItem)) return false;
		if (activeSpell == null) return false;
		if (player.getCooldown(Material.BOOK) > 0) return false;
		if (player.getGameMode().equals(GameMode.CREATIVE)) return true;
		if (getTotalMana() >= activeSpell.cost) return true;
		return false;
	}
	
	// Check if a particular item is considered a spell;
	private boolean isSpell(ItemStack item) {
		if (item.getAmount() != 1) return false;
		if (!item.getType().equals(Material.BOOK)) return false;
		if (!item.getItemMeta().hasEnchant(new Glow(80))) return false;
		return true;
	}
	
	// Decrease the players' mana;
	public void decreaseMana(int decrement) {
		setTotalMana(getTotalMana() - decrement);
	}
	
	// Increase the players' mana;
	public void addMana(int increment) {
		setTotalMana(getTotalMana() + increment);
	}
	
	public int getTotalMana() {
		int experience = 0;
		int level = player.getLevel();
		
		if (level >= 0 && level <= 15) {
			experience = (int) Math.ceil(Math.pow(level, 2) + (6 * level));
			int requiredExperience = 2 * level + 7;
			double currentExp = Double.parseDouble(Float.toString(player.getExp()));
			experience += Math.ceil(currentExp * requiredExperience);
			return experience - 1;
		}
		else if (level > 15 && level <= 30) {
			experience = (int) Math.ceil((2.5 * Math.pow(level, 2) - (40.5 * level) + 360));
			int requiredExperience = 5 * level - 38;
			double currentExp = Double.parseDouble(Float.toString(player.getExp()));
			experience += Math.ceil(currentExp * requiredExperience);
			return experience - 1;
		}
		else {
			experience = (int) Math.ceil(((4.5 * Math.pow(level, 2) - (162.5 * level) + 2220)));
			int requiredExperience = 9 * level - 158;
			double currentExp = Double.parseDouble(Float.toString(player.getExp()));
			experience += Math.ceil(currentExp * requiredExperience);
			return experience - 1;
		}
	}
	
	public void setTotalMana(int xp) {
		// Levels 0 through 15
		if (xp >= 0 && xp < 351) {
			//Calculate Everything
			int a = 1; int b = 6; int c = -xp;
			int level = (int) (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
			int xpForLevel = (int) (Math.pow(level, 2) + (6 * level));
			int remainder = xp - xpForLevel;
			int experienceNeeded = (2 * level) + 7;
			float experience = (float) remainder / (float) experienceNeeded;
			experience = round(experience, 2);
			
			//Set Everything
			player.setLevel(level);
			player.setExp(experience);
		}
		// Levels 16 through 30
		else if (xp >= 352 && xp < 1507) {
			//Calculate Everything
			double a = 2.5; double b = -40.5; int c = -xp + 360;
			double dLevel = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
			int level = (int) Math.floor(dLevel);
			int xpForLevel = (int) (2.5 * Math.pow(level, 2) - (40.5 * level) + 360);
			int remainder = xp - xpForLevel;
			int experienceNeeded = (5 * level) - 38;
			float experience = (float) remainder / (float) experienceNeeded;
			experience = round(experience, 2);
			
			//Set Everything
			player.setLevel(level);
			player.setExp(experience); 
		}
		// Level 31 and greater
		else {
			//Calculate Everything
			double a = 4.5; double b = -162.5; int c = -xp + 2220;
			double dLevel = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
			int level = (int) Math.floor(dLevel);
			int xpForLevel = (int) (4.5 * Math.pow(level, 2) - (162.5 * level) + 2220);
			int remainder = xp - xpForLevel;
			int experienceNeeded = (9 * level) - 158;
			float experience = (float) remainder / (float) experienceNeeded;
			experience = round(experience, 2);
			
			//Set Everything
			player.setLevel(level);
			player.setExp(experience);
		}
	}
	
	private float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_DOWN);
		return bd.floatValue();
	}
}