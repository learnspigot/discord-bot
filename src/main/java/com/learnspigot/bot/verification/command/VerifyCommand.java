package com.learnspigot.bot.verification.command;

import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import com.learnspigot.bot.verification.VerificationHandler;
import com.learnspigot.bot.verification.profile.VerificationProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public record VerifyCommand(@NotNull VerificationHandler verificationHandler) {
    private static final String urlPattern = "^(?:(?:(?:https?|ftp):)?\\/\\/)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z0-9\\u00a1-\\uffff][a-z0-9\\u00a1-\\uffff_-]{0,62})?[a-z0-9\\u00a1-\\uffff]\\.)+(?:[a-z\\u00a1-\\uffff]{2,}\\.?))(?::\\d{2,5})?(?:[/?#]\\S*)?$";

    @Command(label = "verify", usage = "/verify <url>", description = "Use this command to verify you own the course.", log = true)
    public void onVerifyCommand(final @NotNull CommandInfo info) {
        if (!info.args()[0].matches(urlPattern) || !info.args()[0].contains("udemy.com/user/")) {
            info.event().replyEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("Error")
                            .setDescription("Please enter a valid public profile url.")
                            .build()
            ).queue();
            return;
        }

        if (verificationHandler.urlExists(info.author().getIdLong(), info.args()[0])) {
            info.event().replyEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("Error")
                            .setDescription("This Udemy account is already linked.")
                            .build()
            ).queue();
            return;
        }

        VerificationProfile profile;
        try {
            profile = verificationHandler.verify(info.author(), info.args()[0]);
        } catch (FileNotFoundException e) {
            info.event().replyEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("Error")
                            .setDescription("Please enter a valid public profile url.")
                            .build()
            ).queue();
            return;
        } catch (NumberFormatException e) {
            info.event().replyEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("Error")
                            .setDescription("Please make sure that you display your courses on your profile in privacy settings.")
                            .build()
            ).queue();
            return;
        } catch (IOException e) {
            info.event().replyEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("Error")
                            .setDescription("We couldn't verify that you own the course. If this is a mistake, please ping a Specialist.")
                            .build()
            ).queue();
            return;
        }

        if (!profile.verified()) {
            info.event().replyEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("Error")
                            .setDescription("We couldn't verify that you own the course. If this is a mistake, please ping a Specialist.")
                            .build()
            ).queue();
            return;
        }

        info.event().replyEmbeds(
                new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle("Verified")
                        .setDescription("Welcome to the course!")
                        .build()
        ).queue();
    }
}