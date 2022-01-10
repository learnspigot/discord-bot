package com.learnspigot.bot.verification.command;

import com.learnspigot.bot.LearnSpigotConstant;
import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import com.learnspigot.bot.verification.VerificationHandler;
import com.learnspigot.bot.verification.profile.VerificationProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public record ForceVerifyCommand(@NotNull VerificationHandler verificationHandler) {
    @Command(label = "forceverify", usage = "/forceverify <mentioned-user> <url>", description = "Forcefully verify a user with their profile url.", roleId = 749450748244394094L, log = true)
    public void onManualVerifyCommand(final @NotNull CommandInfo info) {
        if (!info.args()[1].matches(LearnSpigotConstant.VALID_LINK_REGEX.get()) || !info.args()[1].contains("udemy.com/user/")) {
            info.event().replyEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("Please enter a valid public profile url.")
                            .addField("Confused?", "You shouldn't be, you're staff.", false)
                            .build()
            ).queue(message -> message.deleteOriginal().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        Member member = info.optionData().get(0).getAsMember();
        if (member == null) {
            info.event().replyEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("Please mention a valid user.")
                            .addField("Confused?", "You shouldn't be, you're staff.", false)
                            .build()
            ).queue(message -> message.deleteOriginal().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        VerificationProfile potentialProfile = verificationHandler.urlExists(member.getIdLong(), info.args()[1]);
        if (potentialProfile != null) {
            info.event().replyEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("This Udemy account is already linked to " + Objects.requireNonNull(
                                    verificationHandler.bot().jda().getUserById(potentialProfile.id())).getAsMention()
                                    + ".")
                            .addField("Confused?", "You shouldn't be, you're staff.", false)
                            .build()
            ).queue(message -> message.deleteOriginal().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        verificationHandler().forceVerify(member.getUser());

        info.event().replyEmbeds(
                new EmbedBuilder()
                        .setColor(Color.decode("#89F27B"))
                        .setTitle("Verified")
                        .setDescription("Successfully verified " + member.getAsMention() + ".")
                        .build()
        ).queue(message -> message.deleteOriginal().queueAfter(15L, TimeUnit.SECONDS));
        TextChannel channel = Objects.requireNonNull(info.event().getGuild()).getTextChannelById((long)
                LearnSpigotConstant.CHANNEL_GENERAL_ID.get());
        assert channel != null;

        channel.sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#EE8917"))
                .setTitle("Welcome")
                .setDescription("Please welcome " + member.getAsMention() + " as a student! ❤")
                .build()
        ).queue();
        channel.sendMessage(member.getAsMention()).queue(message -> message.delete().queue());
    }
}
