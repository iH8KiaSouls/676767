package com.deathmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import org.spongepowered.tools.obfuscation.interfaces.IMessagerEx;

public class DeathModClient implements ClientModInitializer {
    public static final String MOD_ID = "skyblock-death-mod";

    private record DeathPattern(java.util.regex.Pattern pattern, String message) {}

    private static final DeathPattern[] DEATH_PATTERNS = {
            new DeathPattern(
                    java.util.regex.Pattern.compile(".*\u2620\\s+(\\S+)\\s+was killed by\\s+.+?\\s+and became a ghost.*"),
                    "pc ♿"
            ),
            new DeathPattern(
                    java.util.regex.Pattern.compile(".*\u2620\\s+(\\S+)\\s+were killed by\\s+.+?\\s+and became a ghost.*"),
                    "pc ♿ I suck!!!"
            ),
            new DeathPattern(
                    java.util.regex.Pattern.compile(".*\u2620\\s+(\\S+)\\s+died to a mob.*"),
                    "pc ♿ tank nuked"
            ),
            new DeathPattern(
                    java.util.regex.Pattern.compile(".*\u2620\\s+(\\S+)\\s+died to a trap.*"),
                    "pc ♿ You know I see dead people, I just tell 'em, \"Get a life\""
            ),
            new DeathPattern(
                    java.util.regex.Pattern.compile(".*\u2620\\s+(\\S+)\\s+was crushed and became a ghost.*"),
                    "pc ♿ zzz"
            ),
            new DeathPattern(
                    java.util.regex.Pattern.compile(".*\u2620\\s+(\\S+)\\s+burned*"),
                    "pc ♿ skill issue ngl"
            ),

    };

    @Override
    public void onInitializeClient() {
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) ->
                checkMessage(message.getString())
        );
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!overlay) checkMessage(message.getString());
        });
    }

    private void checkMessage(String raw) {
        for (DeathPattern dp : DEATH_PATTERNS) {
            if (dp.pattern().matcher(raw).matches()) {
                sendPartyChat(dp.message());
                return;
            }
        }
    }

    private void sendPartyChat(String message) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> {
            if (client.player != null) {
                client.player.connection.sendCommand(message);
            }
        });
    }
}