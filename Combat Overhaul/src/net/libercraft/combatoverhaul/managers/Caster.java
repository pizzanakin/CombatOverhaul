package net.libercraft.combatoverhaul.managers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;
import net.libercraft.combatoverhaul.ability.AbilityScroll;
import net.libercraft.combatoverhaul.ability.HomingAbility;
import net.libercraft.combatoverhaul.ability.VolleyAbility;
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
	public Vector bodyVector;
	private boolean walkRight;
	private boolean walkLeft;
	
	public void updatePlayerRotation(PlayerMoveEvent e) {
		
		walkRight = false;
		walkLeft = false;
		
		if (e.getFrom().getX() != e.getTo().getX() | e.getFrom().getZ() != e.getTo().getZ()) {
			// Compare walking direction and player eye direction to see if the player is walking straight
			double x = e.getTo().getX() - e.getFrom().getX();
			double z = e.getTo().getZ() - e.getFrom().getZ();
			
			Vector walkDirection = new Vector(x, 0, z).normalize();
			Vector playerDirection = player.getLocation().getDirection().normalize();
			playerDirection.setY(0);
			
			// Compare the two angles of the vectors
			double angle1 = Math.atan2(playerDirection.getX(), playerDirection.getZ()) * (-180 / Math.PI);
			double angle2 = Math.atan2(walkDirection.getX(), walkDirection.getZ()) * (-180 / Math.PI);
			if ((angle2 - angle1) > 180) angle1 += 360;
			if ((angle2 - angle1) < -180) angle1 -= 360;
			
			// If the walk direction is not the same as the direction the player is looking, change the body accordingly
			if (((angle2 - angle1) > 20 && (angle2 - angle1) < 160)	|| ((angle2 - angle1) < -20 && (angle2 - angle1) > -160)) {
				double difference = angle2 - angle1;
				if (130 > difference && difference > 0) walkLeft = true;
				else if (difference > 130) walkRight = true;
				else if (-130 < difference && difference < 0) walkRight = true;
				else if (difference < -130) walkLeft = true;
			}
			// Slowly reset the body back to the center
			else new BukkitRunnable() {
				@Override public void run() {
					bodyYaw = e.getTo().getYaw();
					while (bodyYaw < 180) bodyYaw += 360;
					while (bodyYaw > 180) bodyYaw -= 360;
				}
			}.runTaskLater(plugin, 2);
		}
		
		// Compare the player eye direction to the body rotation and update the body rotation if necessary
		float faceYaw = e.getTo().getYaw();
		while (faceYaw < 180) faceYaw += 360;
		while (faceYaw > 180) faceYaw -= 360;
		float difference = faceYaw - bodyYaw;
		if (difference < -250) difference +=360;
		if (difference > 250) difference -=360;
		if (difference > 50) bodyYaw = faceYaw - 50;
		if (difference < -50) bodyYaw = faceYaw + 50;
	}
	
	public Location getThirdPersonHandLocation() {
		String spellHand = null;
		if (player.getInventory().getItemInOffHand().getType().equals(Material.AIR)) spellHand = "OFF";
		if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) spellHand = "MAIN";
		if (spellHand == null) return null;
		
		String mainHand = null;
		if (player.getMainHand().equals(MainHand.LEFT)) mainHand = "LEFT";
		if (player.getMainHand().equals(MainHand.RIGHT)) mainHand = "RIGHT";
		if (mainHand == null) return null;
		
		Location loc1 = player.getLocation();
		Vector vec1 = bodyVector.clone();
		Vector vec2 = vec1.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(0.4);
		vec2.normalize();
		vec2.multiply(0.4);
		
		// Place the particle location at the correct hand
		if (mainHand.equals("LEFT") && spellHand.equals("MAIN")) vec2.multiply(-1);
		if (mainHand.equals("RIGHT") && spellHand.equals("OFF")) vec2.multiply(-1);
		
		vec2.add(vec1.normalize().multiply(0.1));
		
		loc1.setY(loc1.getY() + 0.75);
		Location loc2 = loc1.add(vec2);
		
		return loc2;
	}
	
	public Location getFirstPersonHandLocation() {
		String spellHand = null;
		if (player.getInventory().getItemInOffHand().getType().equals(Material.AIR)) spellHand = "OFF";
		if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) spellHand = "MAIN";
		if (spellHand == null) return null;
		
		String mainHand = null;
		if (player.getMainHand().equals(MainHand.LEFT)) mainHand = "LEFT";
		if (player.getMainHand().equals(MainHand.RIGHT)) mainHand = "RIGHT";
		if (mainHand == null) return null;
		
		Location loc1 = player.getEyeLocation();
		Vector vec1 = player.getEyeLocation().getDirection();
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
	
	
	
		// -- Caster Methods -- //
	public Caster(Player player, Main plugin) {
		this.player = player;
		this.plugin = plugin;
		passiveAbilities = new ArrayList<AbilityScroll>();
		passiveSpells = new ArrayList<Spellbook>();
		
		new BukkitRunnable() {
			@Override public void run() {
				if (!exists()) this.cancel();
				passiveAbilities.clear();
				passiveSpells.clear();
				for (int i = 0; i < 40; i++) {
					ItemStack item = player.getInventory().getItem(i);
					if (item == null) continue;
					if (!item.getItemMeta().hasLore()) continue;
					
					List<String> lore = item.getItemMeta().getLore();
					if (!(lore.get(0).equals(ChatColor.GREEN + "Ability:")|lore.get(0).equals(ChatColor.AQUA + "Spell:"))) continue;
					
					if (item.getType().equals(Material.EMPTY_MAP)) passiveAbilities.add(AbilityScroll.fromItem(item));
					if (item.getType().equals(Material.BOOK)) passiveSpells.add(Spellbook.fromItem(item));
				}
				mainItem = player.getInventory().getItemInMainHand();
				offItem = player.getInventory().getItemInOffHand();
				
				double radianBody = (bodyYaw + 90) * (Math.PI / 180);
				bodyVector = new Vector(Math.cos(radianBody), 0, Math.sin(radianBody)).normalize();
				if (walkRight) {
					Vector addVector = player.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(-0.25);
					bodyVector.add(addVector).normalize();
				}
				else if (walkLeft) {
					Vector addVector = player.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(0.25);
					bodyVector.add(addVector).normalize();
				}
				
				if (Spellbook.isSpell(mainItem)) activeSpell = Spellbook.fromItem(mainItem);
				else if (Spellbook.isSpell(offItem)) activeSpell = Spellbook.fromItem(offItem);
				else activeSpell = null;
				
			}
		}.runTaskTimer(plugin, 0, 1);
		
		new BukkitRunnable() {
			@Override public void run() {
				if ((mainItem.getType().equals(Material.AIR)|offItem.getType().equals(Material.AIR))&&activeSpell != null) activeSpell.handEffect(plugin.getCasterManager().get(player));
			}
		}.runTaskTimer(plugin, 0, 6);
	}
	
	private boolean exists() {
		return plugin.getCasterManager().casters().contains(this);
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
			player.sendMessage("New Ability: " + activeAbility.getItemName());
			return true;
		}

		return false;
	}
	
	// Check if the player can execute anything by right clicking;
	public boolean handleRightClick() {
		
		// If player has an ability in main hand, select the ability;
		if (AbilityScroll.isAbility(mainItem)) {
			activeAbility = AbilityScroll.fromItem(mainItem);
			player.sendMessage("New Ability: " + activeAbility.getItemName());
			return true;
		}
		
		if (Spellbook.isSpell(mainItem) && Spellbook.isSpell(offItem)) return false;
		if (Spellbook.isSpell(mainItem) && !offItem.getType().equals(Material.AIR)) return false;
		if (Spellbook.isSpell(offItem) && !mainItem.getType().equals(Material.AIR)) return false;
		
		// If player has a staff in offhand, cast spell;
		if (Spellbook.isSpell(mainItem) && canCastSpell()) {
			activeSpell.cast(plugin, player);
			return true;
		}
		
		// If player has a staff in offhand, cast spell;
		if (Spellbook.isSpell(offItem) && canCastSpell()) {
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
			if (activeAbility.equals(AbilityScroll.VOLLEY)) {
				volley = (VolleyAbility) activeAbility.cast(plugin, player);
			}
			else activeAbility.cast(plugin, player);
			trace(activeAbility);
			return true;
		}
		
		return false;
	}
	
	// Check if the player can execute anything by dual clicking;
	public boolean handleDualClickEvent() {
		if ( (Spellbook.isSpell(mainItem)|Spellbook.isSpell(offItem)) && canCastSpell() ) {
			activeSpell.dualCast(plugin, player);
		}
		return false;
	}
	
	/// -===- ABILITY CODE -===- ///
	
		// -- Ability Variables -- //
	private AbilityScroll activeAbility;
	private List<AbilityScroll> passiveAbilities;
	public boolean hasActivatedVolley;
	public boolean hasActivatedHoming;
	public VolleyAbility volley;
	public HomingAbility homing;
	
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
		if (getTotalMana() >= activeSpell.getCost()) return true;
		return false;
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
