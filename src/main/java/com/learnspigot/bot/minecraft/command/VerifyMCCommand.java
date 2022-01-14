package com.learnspigot.bot.minecraft.command;

import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import com.learnspigot.bot.minecraft.CodeHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public record VerifyMCCommand(@NotNull CodeHandler codeHandler) {
    @Command(label = "verifymc", usage = "/verifymc <code>", description = "Link your Minecraft account to gain access to the SMP.", log = true)
    public void onProfileCommand(final @NotNull CommandInfo info) {
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

        codeHandler.verifyOwnership(code);
        info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#E95151"))
                .setTitle("Linked Account")
                .setDescription("You have successfully linked " + uid + " and can join at `smp.learnspigot.com`")
                .build()
        ).queue();
    }
}
