package com.learnspigot.bot.minecraft.command;

import com.learnspigot.bot.LearnSpigotConstant;
import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import com.learnspigot.bot.minecraft.CodeHandler;
import com.learnspigot.bot.minecraft.account.MinecraftAccount;
import com.learnspigot.bot.verification.VerificationHandler;
import com.learnspigot.bot.verification.profile.VerificationProfile;
import dev.devous.barter.Barter;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public record VerifyMCCommand(@NotNull VerificationHandler verificationHandler, @NotNull CodeHandler codeHandler,
                              @NotNull Barter barter) {
    @Command(label = "verifymc", usage = "/verifymc <code>", description = "Link your Minecraft account to gain access to the SMP.", log = true)
    public void onProfileCommand(final @NotNull CommandInfo info) throws ExecutionException, InterruptedException {
        int code;
        try {
            code = Integer.parseInt(info.args()[0]);
        } catch (NumberFormatException e) {
            info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("Please enter a valid number.")
                    .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }
        UUID uid = codeHandler.uidFromCode(code);

        if (uid == null) {
            info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("That code has expired, please generate a new one.")
                    .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        Optional<VerificationProfile> optionalProfile = verificationHandler.profile(info.author().getIdLong());

        if (optionalProfile.isEmpty() && !Objects.requireNonNull(info.member()).getRoles()
                .contains(info.jda().getRoleById(LearnSpigotConstant.ROLE_STUDENT_ID.get()))) {
            info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("We ran into an issue confirming that you own the course." +
                            "Please contact a Specialist.")
                    .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        String name = barter.uuidToName(uid);

        VerificationProfile profile;
        if (optionalProfile.isPresent()) {
            profile = optionalProfile.get();
            if (profile.minecraftAccount() != null) {
                info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.decode("#E95151"))
                        .setTitle("Error")
                        .setDescription("You already have a Minecraft account linked!")
                        .build()
                ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
                return;
            }
        } else {
            profile = verificationHandler.forceVerify(info.author());
        }

        profile.minecraftAccount(new MinecraftAccount(uid, name));
        codeHandler.verifyOwnership(code, profile);

        info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#E95151"))
                .setTitle("Linked Account")
                .setDescription("You have successfully linked " + name + " (" + uid + ")" +
                        " and can join at `smp.learnspigot.com`")
                .build()
        ).queue();
    }
}
