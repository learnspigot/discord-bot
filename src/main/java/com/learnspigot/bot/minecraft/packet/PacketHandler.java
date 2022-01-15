package com.learnspigot.bot.minecraft.packet;

import com.learnspigot.bot.minecraft.CodeHandler;
import dev.devous.electron.Packet;
import org.jetbrains.annotations.NotNull;

public record PacketHandler(@NotNull CodeHandler codeHandler) implements dev.devous.electron.handler.PacketHandler {
    @Override
    public void handle(final @NotNull Packet packet) {
        if (packet.header().equals("EXPIRED")) {
            codeHandler.removeIfExists(Integer.parseInt(packet.content()));
            return;
        }

        if (!packet.header().equals("DISCORD")) {
            return;
        }

        codeHandler.populate(Integer.parseInt(packet.content()), packet.uid());
    }
}
