package net.libercraft.combatoverhaul.particleanimator;


import org.bukkit.Location;
import org.bukkit.util.Vector;

import net.libercraft.combatoverhaul.Tracer;

public class ParticleAnimation implements Tracer {
	
	public ParticleSprite particle;
	public Animation animation;
	public Shape shape;
	public double progression;
	public double base;
	public double factor;
	public double speed;
	
	public ParticleAnimation(ParticleSprite particle, Animation animation, Shape shape, double factor, double base, double speed) {
		this.particle = particle;
		this.animation = animation;
		this.shape = shape;
		this.base = base;
		this.factor = factor;
		this.speed = speed;
		progression = 0.0;
	}
	
	public void showNextFrame(Location location, Vector vector) {
		double[] keys = animation.loadKeys();
		Vector[] lines = shape.loadShape(vector);
		
		int frame = (int) Math.floor(progression * animation.frames);
		
		for (Vector line:lines) {
			switch (animation) {
			case SINUS:
				line.multiply((keys[frame] * factor) + base + 0.25);
				break;
			case TWIST:
				Vector lineCross = line.clone().crossProduct(vector).normalize().multiply(keys[frame]);
				line.add(lineCross).normalize().multiply(factor);
				break;
			}
			
			Location loc = location.clone().add(line);
			particle.summon(loc);
		}
		
		progression = progression + (speed / animation.frames);
		if (progression >= 1) progression = 0.0;
	}
	
	public enum Animation {
		SINUS,
		TWIST;
		
		public int frames = 80;
		
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
			}
			return null;
		}
	}
		
	public enum Shape {
		CROSS_FOUR(4),
		CROSS_EIGHT(8);
		
		private int lines;
		
		private Shape(int lines) {
			this.lines = lines;
		}
		
		public Vector[] loadShape(Vector vector) {
			Vector[] vectors = new Vector[lines];
			switch (this) {
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
			}
			return null;
		}
	}
}
