package com.learnspigot.bot.minecraft;

import com.learnspigot.bot.minecraft.packet.PacketHandler;
import com.mongodb.client.MongoCollection;
import dev.devous.electron.Electron;
import dev.devous.electron.Packet;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class CodeHandler {
    private final @NotNull Map<Integer, UUID> codeMap = new HashMap<>();
    private final @NotNull ScheduledExecutorService scheduledExecutorService;
    private final @NotNull Electron electron;

    public CodeHandler(final @NotNull ScheduledExecutorService scheduledExecutorService,
                       final @NotNull MongoCollection<Document> collection) {
        this.scheduledExecutorService = scheduledExecutorService;
        electron = new Electron(collection, new PacketHandler(this), scheduledExecutorService);
    }

    public void removeIfExists(final int code) {
        codeMap.remove(code);
    }

    public @Nullable UUID uidFromCode(final int code) {
        return codeMap.get(code);
    }

    public void populate(final int code, final @NotNull UUID uid) {
        codeMap.put(code, uid);
    }

    public void verifyOwnership(final int code) {
        UUID uid = codeMap.remove(code);
        scheduledExecutorService.schedule(() -> {
            electron.packetQueue().queue(new Packet(uid, "WHITELIST", String.valueOf(code)));
        }, 0L, TimeUnit.SECONDS);
    }

    public @NotNull Map<Integer, UUID> codeMap() {
        return codeMap;
    }
}
