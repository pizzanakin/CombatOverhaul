package net.libercraft.combatoverhaul.particleanimator;


import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Main;
import net.libercraft.combatoverhaul.Tracer;

public class ParticleAnimation implements Tracer {
	
	private Main plugin;
	private double[] keys;
	private Vector[] lines;
	private double randomFactor;
	public ParticleSprite particle;
	public Animation animation;
	public Shape shape;
	public double progression;
	public double base;
	public double factor;
	public double speed;
	public boolean reverse;
	
	public ParticleAnimation(Main plugin, ParticleSprite particle, Animation animation, Shape shape, double factor, double base, double speed, boolean reverse) {
		this.plugin = plugin;
		this.particle = particle;
		this.animation = animation;
		this.shape = shape;
		this.base = base;
		this.factor = factor;
		this.speed = speed;
		this.reverse = reverse;
		this.progression = 0.0;
		this.randomFactor = Math.random();
		
		keys = animation.loadKeys();
	}
	
	public void setParticleSprite(ParticleSprite particle) {
		this.particle = particle;
	}
	
	public void setAnimation(Animation animation) {
		this.animation = animation;
	}
	
	private void calculateFrame(int frame, Vector vector) {
		double multiplier;
		
		switch (animation) {
		case LIGHTNING:
			multiplier = (keys[frame] * factor) + base;
			if (reverse) multiplier = 1 - multiplier;
			vector.multiply(multiplier);
			vector.add(new Vector(0, 0, 1).multiply((Math.random() * 0.5) - 0.25));
			break;
		case LINEAR:
			multiplier = (keys[frame] * factor) + base;
			if (reverse) multiplier = 1 - multiplier;
			vector.multiply(multiplier);
			break;
		case SINUS:
			multiplier = (keys[frame] * factor) + base + 0.25;
			if (reverse) multiplier = 1 - multiplier;
			vector.multiply(multiplier);
			break;
		case TWIST:
			multiplier = keys[frame];
			if (reverse) multiplier = 1 - multiplier;
			Vector lineCross = vector.clone().crossProduct(new Vector(1, 0, 0)).normalize().multiply(multiplier);
			vector.add(lineCross).normalize().multiply(factor);
			break;
		}
	}
	
	public boolean showNextFrame(Location location) {
		lines = shape.loadShape(randomFactor);
		
		int frame = (int) Math.floor(progression * keys.length);
		
		for (Vector line:lines) {
			calculateFrame(frame, line);
			Location loc = location.clone().add(line);
			particle.summon(loc);
		}
		
		progression = progression + (speed / keys.length);
		if (progression >= 1) {
			progression = 0.0;
			keys = animation.loadKeys();
			return true;
		}
		return false;
	}
	
	public boolean showNextFrame(Location location, Vector vector) {
		lines = shape.loadShape(vector, randomFactor);
		
		int frame = (int) Math.floor(progression * keys.length);
		
		for (Vector line:lines) {
			calculateFrame(frame, line);
			Location loc = location.clone().add(line);
			particle.summon(loc);
		}
		
		progression = progression + (speed / keys.length);
		if (progression >= 1) {
			progression = 0.0;
			keys = animation.loadKeys();
			return true;
		}
		return false;
	}
	
	public void play(Location location) {
		progression = 0.0;
		
		new BukkitRunnable() {
			@Override public void run() {
				for (int i = 0; i < 8; i++) if (showNextFrame(location)) {
					this.cancel();
					break;
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	public void play(Location location, Vector vector) {
		progression = 0.0;

		new BukkitRunnable() {
			@Override public void run() {
				for (int i = 0; i < 8; i++) if (showNextFrame(location, vector)) {
					this.cancel();
					break;
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	public enum Animation implements Tracer {
		SINUS(80),
		TWIST(80),
		LINEAR(150),
		LIGHTNING();
		
		public int frames;
		private double oldLength;
		
		private Animation(int frames) {
			this.frames = frames;
		}
		
		private Animation() {
			this.frames = (int) (Math.ceil(Math.random() * 10)) * 10;
			this.oldLength = 0.0;
		}
		
		public double[] loadKeys() {
			double[] keys = new double[frames];
			switch (this) {
			case SINUS:
				for (int i = 0; i < frames; i++) {
					keys[i] = Math.sin(i * (18.0 / frames)) / (frames/20);
				}
				return keys;
			case TWIST:
				for (int i = 0; i < frames; i++) {
					keys[i] = Math.sin(90 * ((90 / frames) * i)) / Math.cos(90 * ((90 / frames) * i));
				}
				return keys;
			case LINEAR:
				for (int i = 0; i < frames; i++) {
					keys[i] = (i + 0.0) / frames;
				}
				return keys;
			case LIGHTNING:
				double newLength = 0.0;
				if (oldLength == 0.0) newLength += Math.random() - 0.5;
				if (oldLength > 0.0) newLength += Math.random() * 0.5 + 0.5;
				if (oldLength > 0.0) newLength -= Math.random() * 0.5 + 0.5;
				double difference = newLength - oldLength;
				for (int i = 0; i < frames; i++) {
					double increment = (difference / frames) * i;
					keys[i] = oldLength + increment;
				}
				oldLength = newLength;
				this.frames = (int) (Math.ceil(Math.random() * 10)) * 10;
				return keys;
			}
			return null;
		}
	}
		
	public enum Shape {
		ALTAR_1(1),
		ALTAR_2(1),
		ALTAR_3(1),
		ALTAR_4(1),
		ALTAR(4),
		CROSS_TWO(2),
		CROSS_THREE(3),
		CROSS_FOUR(4),
		CROSS_EIGHT(8),
		RANDOM_SPHERE(4),
		RANDOM_HALF_SPHERE(4),
		SINGLE_LINE(1),
		SINGLE_LINE_RANDOM(1);
		
		private int lines;
		
		private Shape(int lines) {
			this.lines = lines;
		}
		
		public Vector[] loadShape(double randomFactor) {
			Vector[] vectors = new Vector[lines];
			switch (this) {
			case ALTAR_1:
				vectors[0] = new Vector(-0.5, 1.7, -0.5);
				return vectors;
			case ALTAR_2:
				vectors[0] = new Vector(-0.5, 1.7, 0.5);
				return vectors;
			case ALTAR_3:
				vectors[0] = new Vector(0.5, 1.7, 0.5);
				return vectors;
			case ALTAR_4:
				vectors[0] = new Vector(0.5, 1.7, -0.5);
				return vectors;
			case ALTAR:
				vectors[0] = new Vector(1, -0.2, 1);
				vectors[1] = new Vector(1, -0.2, -1);
				vectors[2] = new Vector(-1, -0.2, -1);
				vectors[3] = new Vector(-1, -0.2, 1);
				return vectors;
			case CROSS_TWO:
				vectors[0] = new Vector(0, 1, 0).normalize();
				vectors[1] = new Vector(0, 0, 1).normalize();
				return vectors;
			case CROSS_THREE:
				vectors[0] = new Vector(0, 1, 0).normalize();
				vectors[1] = new Vector(0, 0, 1).add(new Vector(0, -0.6, 0)).normalize();
				vectors[2] = new Vector(0, 0, -1).add(new Vector(0, -0.6, 0)).normalize();
				return vectors;
			case CROSS_FOUR:
				vectors[0] = new Vector(0, 1, 0).normalize();
				vectors[1] = new Vector(0, 0, 1).normalize();
				vectors[2] = new Vector(0, -1, 0).normalize();
				vectors[3] = new Vector(0, 0, -1).normalize();
				return vectors;
			case CROSS_EIGHT:
				vectors[0] = new Vector(0, 1, 0).normalize();
				vectors[1] = new Vector(0, 0, 1).normalize();
				vectors[2] = new Vector(0, -1, 0).normalize();
				vectors[3] = new Vector(0, 0, -1).normalize();
				vectors[4] = vectors[0].clone().add(vectors[1]).normalize();
				vectors[5] = vectors[1].clone().add(vectors[2]).normalize();
				vectors[6] = vectors[2].clone().add(vectors[3]).normalize();
				vectors[7] = vectors[2].clone().add(vectors[0]).normalize();
				return vectors;
			case RANDOM_SPHERE:
				Vector radian = new Vector(0, 1, 0).normalize();
				Vector angle = new Vector(0, 0, 1).normalize();
				
				for (int i = 0; i < lines; i++) {
					Vector randomVector = radian.clone();
					randomVector.add(radian.clone().multiply((Math.random() * 2) - 1));
					randomVector.add(angle.clone().multiply((Math.random() * 2) - 1));
					randomVector.add(angle.clone().crossProduct(radian).normalize().multiply((Math.random() * 2) - 1));
					randomVector.normalize();
					vectors[i] = randomVector;
				}
				
				return vectors;
			case RANDOM_HALF_SPHERE:
				radian = new Vector(0, 1, 0).normalize().multiply(-1);
				angle = new Vector(0, 0, 1).normalize();
				
				for (int i = 0; i < lines; i++) {
					Vector randomVector = radian.clone();
					randomVector.add(radian.clone().multiply((Math.random() * 2) - 1));
					randomVector.add(angle.clone().multiply((Math.random() * 2) - 1));
					randomVector.add(angle.clone().crossProduct(radian).normalize().multiply((Math.random() * 2) - 1));
					randomVector.normalize();
					vectors[i] = randomVector;
				}
				
				return vectors;
			case SINGLE_LINE:
				vectors[0] = new Vector(0, 1, 0).normalize();
				return vectors;
			case SINGLE_LINE_RANDOM:
				vectors[0] = new Vector(0, 1, 0).multiply((randomFactor * 2) - 1);
				vectors[0].add(new Vector(0, 0, 1).multiply((randomFactor * 2) - 1));
				vectors[0].add(new Vector(1, 0, 0).multiply((randomFactor * 2) - 1));
				vectors[0].normalize();
				return vectors;
			default:
				break;
			}
			return null;
		}
		
		public Vector[] loadShape(Vector vector, double randomFactor) {
			Vector[] vectors = new Vector[lines];
			switch (this) {
			case CROSS_TWO:
				vectors[0] = vector.clone().crossProduct(new Vector(0, 1, 0)).normalize();
				vectors[1] = vectors[0].clone().crossProduct(vector).normalize();
				return vectors;
			case CROSS_THREE:
				vectors[0] = vector.clone().crossProduct(new Vector(0, 1, 0)).normalize();
				vectors[1] = vectors[0].clone().crossProduct(vector).normalize();
				vectors[2] = vectors[0].clone().multiply(-1).add(vectors[1].clone().multiply(-0.6)).normalize();
				vectors[0] = vectors[0].add(vectors[1].clone().multiply(-0.6).normalize());
				return vectors;
			case CROSS_FOUR:
				vectors[0] = vector.clone().crossProduct(new Vector(0, 1, 0)).normalize();
				vectors[1] = vectors[0].clone().crossProduct(vector).normalize();
				vectors[2] = vectors[0].clone().multiply(-1).normalize();
				vectors[3] = vectors[1].clone().multiply(-1).normalize();
				return vectors;
			case CROSS_EIGHT:
				vectors[0] = vector.clone().crossProduct(new Vector(0, 1, 0)).normalize();
				vectors[1] = vectors[0].clone().crossProduct(vector).normalize();
				vectors[2] = vectors[0].clone().multiply(-1).normalize();
				vectors[3] = vectors[1].clone().multiply(-1).normalize();
				vectors[4] = vectors[0].clone().add(vectors[1]).normalize();
				vectors[5] = vectors[1].clone().add(vectors[2]).normalize();
				vectors[6] = vectors[2].clone().add(vectors[3]).normalize();
				vectors[7] = vectors[2].clone().add(vectors[0]).normalize();
				return vectors;
			case RANDOM_SPHERE:
				Vector radian = vector.clone().normalize().multiply(-1);
				Vector crossProduct = radian.clone().crossProduct(new Vector(0, 1, 0)).normalize();
				
				for (int i = 0; i < lines; i++) {
					Vector randomVector = radian.clone();
					randomVector.add(radian.clone().multiply((Math.random() * 2) - 1));
					randomVector.add(crossProduct.clone().multiply((Math.random() * 2) - 1));
					randomVector.add(crossProduct.clone().crossProduct(radian).normalize().multiply((Math.random() * 2) - 1));
					randomVector.normalize();
					vectors[i] = randomVector;
				}
				
				return vectors;
			case RANDOM_HALF_SPHERE:
				radian = vector.clone().normalize().multiply(-1);
				crossProduct = radian.clone().crossProduct(new Vector(0, 1, 0)).normalize();
				
				for (int i = 0; i < lines; i++) {
					Vector randomVector = radian.clone();
					randomVector.add(radian.clone().multiply((Math.random() * 2) - 1));
					randomVector.add(crossProduct.clone().multiply((Math.random() * 2) - 1));
					randomVector.add(crossProduct.clone().crossProduct(radian).normalize().multiply((Math.random() * 2) - 1));
					randomVector.normalize();
					vectors[i] = randomVector;
				}
				
				return vectors;
			case SINGLE_LINE:
				vectors[0] = vector.clone().crossProduct(new Vector(0, 1, 0));
				return vectors;
			case SINGLE_LINE_RANDOM:
				Vector line = vector.clone().crossProduct(new Vector(0, 1, 0)).normalize();
				vectors[0] = line.clone();
				vectors[0].add(line.clone().crossProduct(vector).normalize().multiply((randomFactor * 2) - 1));
				vectors[0].add(line.clone().multiply((randomFactor * 2) - 1));
				vectors[0].normalize();
				return vectors;
			default:
				return null;
			}
		}
	}
}
