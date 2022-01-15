package com.learnspigot.bot.minecraft.account;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class MinecraftAccount {
    private @NotNull UUID uid;
    private @NotNull String name;

    public MinecraftAccount(final @NotNull UUID uid, final @NotNull String name) {
        this.uid = uid;
        this.name = name;
    }

    public @NotNull UUID uid() {
        return uid;
    }

    public void uid(final @NotNull UUID uid) {
        this.uid = uid;
    }

    public @NotNull String name() {
        return name;
    }

    public void name(final @NotNull String name) {
        this.name = name;
    }
}
