package com.learnspigot.bot.verification.profile;

import com.learnspigot.bot.LearnSpigotConstant;
import com.learnspigot.bot.minecraft.account.MinecraftAccount;
import com.learnspigot.bot.udemy.account.UdemyAccount;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class VerificationProfile {
    private final long id;
    private final @NotNull String name;

    private @Nullable UdemyAccount udemyAccount;
    private boolean verified;

    private @Nullable MinecraftAccount minecraftAccount;

    public VerificationProfile(final long id, final @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    public long id() {
        return id;
    }

    public @NotNull String name() {
        return name;
    }

    public @Nullable UdemyAccount udemyAccount() {
        return udemyAccount;
    }

    public void udemyAccount(@NotNull UdemyAccount udemyAccount) {
        this.udemyAccount = udemyAccount;
    }

    public boolean verified() {
        return verified;
    }

    public void verified(boolean verified) {
        this.verified = verified;
    }

    public @Nullable MinecraftAccount minecraftAccount() {
        return minecraftAccount;
    }

    public void minecraftAccount(@NotNull MinecraftAccount minecraftAccount) {
        this.minecraftAccount = minecraftAccount;
    }

    public boolean ownsCourse() {
        if (udemyAccount == null) {
            return false;
        }

        return udemyAccount.findCourse(LearnSpigotConstant.LEARN_SPIGOT_COURSE_ID.get()).isPresent();
    }

    @Override
    public String toString() {
        return "VerificationProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", udemyAccount=" + udemyAccount +
                ", verified=" + verified +
                '}';
    }
}
