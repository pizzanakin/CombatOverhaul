package net.libercraft.combatoverhaul.particleanimator;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Tracer;

public class ParticleAnimator implements Tracer {
	
	public enum Animation {
		SINUS(12),
		TURN(8);
		
		private int frames;
		
		private Animation(int frames) {
			this.frames = frames;
		}
		
		private double[] loadKeys() {
			double[] keys = new double[frames];
			switch (this) {
			case SINUS:
				keys[0] = 0;
				keys[1] = 0.04;
				keys[2] = 0.12;
				keys[3] = 0.20;
				keys[4] = 0.28;
				keys[5] = 0.32;
				keys[6] = 0.32;
				keys[7] = 0.32;
				keys[8] = 0.28;
				keys[9] = 0.20;
				keys[10] = 0.12;
				keys[11] = 0.04;
				return keys;
			case TURN:
				keys[0] = 0;
				keys[1] = 0.06;
				keys[2] = 0.12;
				keys[3] = 0.21;
				keys[4] = 0.3;
				keys[5] = 0.48;
				keys[6] = 0.72;
				keys[7] = 1.4;
				return keys;
			default:
				return null;
			}
		}
		
		private void executePlus(Particle particle, Location location, int count, double x, double y, double z, double speed, int frame, Vector vector) {
			double[] keys = loadKeys();
			
			Vector line1 = vector.clone().crossProduct(new Vector(0, 1, 0)).normalize();
			Vector line2 = line1.clone().crossProduct(vector).normalize();
			Vector line3 = line1.clone().multiply(-1);
			Vector line4 = line2.clone().multiply(-1);
			
			switch (this) {
			case SINUS:
				line1.multiply(keys[frame] + 0.15);
				line2.multiply(keys[frame] + 0.15);
				line3.multiply(keys[frame] + 0.15);
				line4.multiply(keys[frame] + 0.15);
				break;
			case TURN:
				line1.multiply(0.3);
				line2.multiply(0.3);
				line3.multiply(0.3);
				line4.multiply(0.3);
				
				System.out.println(line1);
				System.out.println(vector);
				System.out.println(keys);
				System.out.println(frame);
				System.out.println(keys[frame]);
				Vector line1Cross = line1.clone().crossProduct(vector).normalize().multiply(keys[frame]);
				Vector line2Cross = line2.clone().crossProduct(vector).normalize().multiply(keys[frame]);
				Vector line3Cross = line3.clone().crossProduct(vector).normalize().multiply(keys[frame]);
				Vector line4Cross = line4.clone().crossProduct(vector).normalize().multiply(keys[frame]);
				
				line1.add(line1Cross).normalize().multiply(0.3);
				line2.add(line2Cross).normalize().multiply(0.3);
				line3.add(line3Cross).normalize().multiply(0.3);
				line4.add(line4Cross).normalize().multiply(0.3);
				break;
			default:
				break;
			}
			
			
			Location loc1 = location.clone().add(line1);
			Location loc2 = location.clone().add(line2);
			Location loc3 = location.clone().add(line3);
			Location loc4 = location.clone().add(line4);
			
			location.getWorld().spawnParticle(particle, loc1, count, x, y, z, speed);
			location.getWorld().spawnParticle(particle, loc2, count, x, y, z, speed);
			location.getWorld().spawnParticle(particle, loc3, count, x, y, z, speed);
			location.getWorld().spawnParticle(particle, loc4, count, x, y, z, speed);
		}
		
		private void executeEightCross(Particle particle, Location location, int count, double x, double y, double z, double speed, int frame, Vector vector) {
			double[] keys = loadKeys();
			
			Vector line1 = vector.clone().crossProduct(new Vector(0, 1, 0)).normalize();
			Vector line2 = line1.clone().crossProduct(vector).normalize();
			Vector line3 = line1.clone().multiply(-1);
			Vector line4 = line2.clone().multiply(-1);
			Vector line5 = line1.clone().add(line2);
			Vector line6 = line2.clone().add(line3);
			Vector line7 = line3.clone().add(line4);
			Vector line8 = line4.clone().add(line1);
			
			switch (this) {
			case SINUS:
				line1.multiply(keys[frame] + 0.15);
				line2.multiply(keys[frame] + 0.15);
				line3.multiply(keys[frame] + 0.15);
				line4.multiply(keys[frame] + 0.15);
				line5.multiply(keys[frame] + 0.15);
				line6.multiply(keys[frame] + 0.15);
				line7.multiply(keys[frame] + 0.15);
				line8.multiply(keys[frame] + 0.15);
				break;
			case TURN:
				
				break;
			default:
				break;
			}
			
			
			Location loc1 = location.clone().add(line1);
			Location loc2 = location.clone().add(line2);
			Location loc3 = location.clone().add(line3);
			Location loc4 = location.clone().add(line4);
			Location loc5 = location.clone().add(line5);
			Location loc6 = location.clone().add(line6);
			Location loc7 = location.clone().add(line7);
			Location loc8 = location.clone().add(line8);
			
			location.getWorld().spawnParticle(particle, loc1, count, x, y, z, speed);
			location.getWorld().spawnParticle(particle, loc2, count, x, y, z, speed);
			location.getWorld().spawnParticle(particle, loc3, count, x, y, z, speed);
			location.getWorld().spawnParticle(particle, loc4, count, x, y, z, speed);
			location.getWorld().spawnParticle(particle, loc5, count, x, y, z, speed);
			location.getWorld().spawnParticle(particle, loc6, count, x, y, z, speed);
			location.getWorld().spawnParticle(particle, loc7, count, x, y, z, speed);
			location.getWorld().spawnParticle(particle, loc8, count, x, y, z, speed);
		}
	}

	public static int animate(Particle particle, Location location, Animation animation, int count, double x, double y, double z, double speed, int frame, Vector vector, String shape) {
		switch (shape) {
		case "PLUS":
			animation.executePlus(particle, location, count, x, y, z, speed, frame, vector);
			break;
		case "EIGHT":
			animation.executeEightCross(particle, location, count, x, y, z, speed, frame, vector);
			break;
		default:
			break;
		}
		
		if (frame + 1 >= animation.frames) return frame = 0;
		return frame + 1;
	}
	
}
