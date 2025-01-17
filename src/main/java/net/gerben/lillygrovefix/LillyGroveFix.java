package net.gerben.lillygrovefix;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LillyGroveFix implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("LillyGroveFix");
	private static boolean wasFlying = false; // Tracks flight state

	@Override
	public void onInitializeClient() {
		// Register client tick event to monitor player's flight state
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;

			ClientPlayerEntity player = client.player;

			// Store flight status
			if (player.getAbilities().allowFlying) wasFlying = true;

			// If player was flying and is no longer flying, restore it
			if (!player.getAbilities().allowFlying && wasFlying) {
				player.getAbilities().allowFlying = true;
				LOGGER.info("Restored flight state for player.");
			}
		});

		// Listen to world transitions and handle flight restoration
		monitorWorldChange();
	}

	private void monitorWorldChange() {
		MinecraftClient client = MinecraftClient.getInstance();

		// Use a simple check for world changes or dimension changes.
		client.execute(() -> {
			if (client.world != null && client.player != null && wasFlying) {
				client.player.getAbilities().allowFlying = true;
				LOGGER.info("Flight state restored after world transition.");
			}
		});
	}
}
