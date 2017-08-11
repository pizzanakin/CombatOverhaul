package net.libercraft.combatoverhaul;

import org.bukkit.Bukkit;

public interface Tracer {

	public default void trace(Object input) {
		System.out.println(this.getClass().getSimpleName() + ": " + input);
	}
	
	public default void traceChat(Object input) {
		Bukkit.broadcastMessage((String) input);
	}
}