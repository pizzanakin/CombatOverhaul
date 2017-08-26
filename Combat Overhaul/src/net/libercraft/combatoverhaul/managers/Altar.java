package net.libercraft.combatoverhaul.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.libercraft.combatoverhaul.ArmorStandPlacer;
import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation;
import net.libercraft.combatoverhaul.particleanimator.ParticleSprite;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation.Animation;
import net.libercraft.combatoverhaul.particleanimator.ParticleAnimation.Shape;
import net.libercraft.combatoverhaul.spell.Spellbook;

public class Altar implements Tracer {

	public enum AltarMode {
		EMPTY,
		CHARGED;
	}
	
	public enum AltarType {
		FIRE(Material.NETHERRACK, Material.NETHERRACK, Material.NETHER_BRICK, ParticleSprite.SINGLE_FIRE, (byte) 0),
		WATER(Material.PRISMARINE, Material.PRISMARINE, Material.PACKED_ICE, ParticleSprite.SINGLE_WATER, (byte) 2),
		ENERGY(Material.IRON_BLOCK, Material.STONE, Material.IRON_BLOCK, ParticleSprite.SINGLE_ENERGY, (byte) 0);
		
		public Material buildmaterial;
		public Material primarymaterial;
		public Material secondaryMaterial;
		public ParticleSprite particle;
		public byte data;
		
		private AltarType(Material buildmaterial, Material material, Material secondaryMaterial, ParticleSprite particle, byte data) {
			this.buildmaterial = buildmaterial;
			this.primarymaterial = material;
			this.secondaryMaterial = secondaryMaterial;
			this.particle = particle;
			this.data = data;
		}
	}

	private Main plugin;
	private AltarMode mode;
	private AltarType type;
	private int stage;
	private boolean exists;
	
	private World world;
	private Block block;
	private Location location;
	
	public Altar(Main plugin, Location location, AltarMode mode, AltarType type, int stage) {
		this.plugin = plugin;
		this.mode = mode;
		this.type = type;
		this.stage = stage;
		this.exists = true;
		
		this.world = location.getWorld();
		this.block = location.getBlock();
		this.location = location.clone().add(0.5, 0, 0.5);
		
		update();
	}
	
	public void update() {

		// Update the altar;
		new BukkitRunnable() {
			private int counter = 0;
			@Override public void run() {
				if (!exists) {
					this.cancel();
					return;
				}

				// Add particles if charging
				if (mode.equals(AltarMode.CHARGED)) {
					type.particle.summon(location.clone().add(0.5, 2.5, 0.5));
					type.particle.summon(location.clone().add(0.5, 2.5, -0.5));
					type.particle.summon(location.clone().add(-0.5, 2.5, -0.5));
					type.particle.summon(location.clone().add(-0.5, 2.5, 0.5));
				}
				
				// Execute spellbook particles;
				if (counter >= 40) {
					counter = 0;
					if (mode.equals(AltarMode.CHARGED)) executeParticles();
				}
				counter++;
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	public static Material[] getAltarBlueprint(AltarType type) {
		Material[] list = new Material[27];
		list[0] = Material.LAPIS_BLOCK;
		list[1] = Material.LAPIS_BLOCK;
		list[2] = Material.LAPIS_BLOCK;
		list[3] = type.buildmaterial;
		list[4] = Material.OBSIDIAN;
		list[5] = type.buildmaterial;
		list[6] = type.buildmaterial;
		list[7] = null;
		list[8] = type.buildmaterial;
		list[9] = Material.LAPIS_BLOCK;
		list[10] = Material.LAPIS_BLOCK;
		list[11] = Material.LAPIS_BLOCK;
		list[12] = Material.OBSIDIAN;
		list[13] = Material.DIAMOND_BLOCK;
		list[14] = Material.OBSIDIAN;
		list[15] = null;
		list[16] = null;
		list[17] = null;
		list[18] = Material.LAPIS_BLOCK;
		list[19] = Material.LAPIS_BLOCK;
		list[20] = Material.LAPIS_BLOCK;
		list[21] = type.buildmaterial;
		list[22] = Material.OBSIDIAN;
		list[23] = type.buildmaterial;
		list[24] = type.buildmaterial;
		list[25] = null;
		list[26] = type.buildmaterial;
		return list;
	}
	
	public void killAltar() {
		exists = false;

		for (Entity entity:world.getEntities()) {
			if (!(entity instanceof ArmorStand)) continue;
			if (entity.getLocation().distance(location) > 2) continue;
			
			for (String tag:entity.getScoreboardTags()) {
				if (tag.equals("ALTARSTAND")|tag.equals("SPELL")) entity.remove();
			}
		}
		
		for (int x = -1; x < 2; x++) {
			for (int z = -2; z < 3; z++) {
				block.getLocation().clone().add(x, -1, z).getBlock().setType(Material.DIRT);
			}
		}
		for (int x = -2; x < 3; x++) {
			for (int z = -1; z < 2; z++) {
				block.getLocation().clone().add(x, -1, z).getBlock().setType(Material.DIRT);
			}
		}

		block.setType(Material.AIR);
	}
	
	public void executeParticles() {
		
		for (int i = 0; i < this.stage; i++) {
			new ParticleAnimation(plugin, type.particle, Animation.LINEAR, Shape.valueOf("ALTAR_" + (i + 1)), 1, 0, 3, true).play(location.clone().add(0, 1, 0));
		}
	}
	
	public void checkPillarCollision(PlayerMoveEvent e) {

		Location[] first = new Location[4];
		Location[] second = new Location[4];
		
		first[0] = this.location.clone().add(0.5, 0, 0.5);
		second[0] = this.location.clone().add(1.5, 2.5, 1.5);
		first[1] = this.location.clone().add(-1.5, 0, 0.5);
		second[1] = this.location.clone().add(-0.5, 2.5, 1.5);
		first[2] = this.location.clone().add(-1.5, 0, -1.5);
		second[2] = this.location.clone().add(-0.5, 2.5, -0.5);
		first[3] = this.location.clone().add(0.5, 0, -1.5);
		second[3] = this.location.clone().add(1.5, 2.5, -0.5);
		
		for (int i = 0; i < 4; i++) {

			if (
					e.getTo().getX() >= first[i].getX()&&
					e.getTo().getZ() >= first[i].getZ()&&
					e.getTo().getY() >= first[i].getY()&&
					e.getTo().getX() < second[i].getX()&&
					e.getTo().getY() < second[i].getY()&&
					e.getTo().getZ() < second[i].getZ()
					) {
				double x = e.getFrom().getX() - first[i].getX();
				double z = e.getFrom().getZ() - first[i].getZ();
				
				if (x < z && x + z < 1) {
					while (e.getTo().getX() > first[i].getX()) {
						e.getTo().add(-0.005, 0, 0);
					}
				}
				
				if (x < z && x + z > 1) {
					
					while (e.getTo().getZ() < second[i].getZ()) {
						e.getTo().add(0, 0, 0.005);
					}
				}
				
				if (x > z && x + z > 1) {
					while (e.getTo().getX() < second[i].getX()) {
						e.getTo().add(0.005, 0, 0);
					}
				}
				
				if (x > z && x + z < 1) {

					
					while (e.getTo().getZ() >= first[i].getZ()) {
						e.getTo().add(0, 0, -0.005);
					}
				}
			}
		}
	}
	
	public void setMode(AltarMode mode) {
		this.mode = mode;
		switch (mode) {
		case CHARGED:
			
			// Create the book stand model
			//block.setType(Material.ENCHANTMENT_TABLE);
			ArmorStandPlacer.createArmorStand(plugin, location, false, Material.REDSTONE_BLOCK, (byte) 0, 0.0, -1.2, 0.0, 0.0, 0.0, 0.0, "SPELL");
			
			ArmorStandPlacer.createArmorStand(plugin, location, false, type.secondaryMaterial, (byte) 0, 0.4, -1.15, 0.0, 0.0, 0.0, 25.0, "SPELL");
			ArmorStandPlacer.createArmorStand(plugin, location, false, type.secondaryMaterial, (byte) 0, -0.4, -1.15, 0.0, 0.0, 0.0, -25.0, "SPELL");
			ArmorStandPlacer.createArmorStand(plugin, location, false, type.secondaryMaterial, (byte) 0, 0.0, -1.15, 0.4, 25.0, 0.0, 0.0, "SPELL");
			ArmorStandPlacer.createArmorStand(plugin, location, false, type.secondaryMaterial, (byte) 0, 0.0, -1.15, -0.4, -25.0, 0.0, 0.0, "SPELL");
			
			ArmorStandPlacer.createArmorStand(plugin, location, false, Material.DIAMOND_BLOCK, (byte) 0, 0.3, -1.2, 0.3, -10.0, 0.0, -10.0, "SPELL");
			ArmorStandPlacer.createArmorStand(plugin, location, false, Material.DIAMOND_BLOCK, (byte) 0, -0.3, -1.2, 0.3, -10.0, 0.0, 10.0, "SPELL");
			ArmorStandPlacer.createArmorStand(plugin, location, false, Material.DIAMOND_BLOCK, (byte) 0, -0.3, -1.2, -0.3, 10.0, 0.0, 10.0, "SPELL");
			ArmorStandPlacer.createArmorStand(plugin, location, false, Material.DIAMOND_BLOCK, (byte) 0, 0.3, -1.2, -0.3, 10.0, 0.0, -10.0, "SPELL");
			
			ArmorStandPlacer.createArmorStand(plugin, location, false, type.primarymaterial, (byte) 0, 0.4, -1.3, 0.4, 0.0, 0.0, 0.0, "SPELL");
			ArmorStandPlacer.createArmorStand(plugin, location, false, type.primarymaterial, (byte) 0, -0.4, -1.3, 0.4, 0.0, 0.0, 0.0, "SPELL");
			ArmorStandPlacer.createArmorStand(plugin, location, false, type.primarymaterial, (byte) 0, -0.4, -1.3, -0.4, 0.0, 0.0, 0.0, "SPELL");
			ArmorStandPlacer.createArmorStand(plugin, location, false, type.primarymaterial, (byte) 0, 0.4, -1.3, -0.4, 0.0, 0.0, 0.0, "SPELL");
			
			world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, 1);
			break;
		case EMPTY:
			block.setType(Material.AIR);
			for (Entity entity:world.getEntities()) {
				if (!(entity instanceof ArmorStand)) continue;
				if (entity.getLocation().distance(block.getLocation()) > 3) continue;
				for (String tag:entity.getScoreboardTags()) {
					if (tag.equals("SPELL")) entity.remove();
				}
			}
			world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
			break;
		}
	}
	
	public AltarMode getMode() {
		return mode;
	}
	
	public AltarType getType() {
		return type;
	}
	
	public void setStage(int stage) {
		this.stage = stage;
	}
	
	public void advance() {
		stage++;
	}
	
	public int getStage() {
		return stage;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public Location getLocation() {
		return block.getLocation();
	}
	
	public Spellbook getSpellbook() {
		return Spellbook.valueOf(type.toString());
	}
}
