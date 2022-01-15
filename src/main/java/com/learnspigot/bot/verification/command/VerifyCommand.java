package com.learnspigot.bot.verification.command;

import com.learnspigot.bot.LearnSpigotConstant;
import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import com.learnspigot.bot.verification.VerificationHandler;
import com.learnspigot.bot.verification.profile.VerificationProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public record VerifyCommand(@NotNull VerificationHandler verificationHandler) {
    @Command(label = "verify", usage = "/verify <url>", description = "Verify you own the course.", log = true)
    public void onVerifyCommand(final @NotNull CommandInfo info) {
        if (!info.args()[0].matches(LearnSpigotConstant.VALID_LINK_REGEX.get()) || !info.args()[0].contains("udemy.com/user/")) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("Please enter a valid public profile url.")
                            .addField("Confused?", "If you're stuck or confused, you can ping a Specialist.", false)
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        if (verificationHandler.urlExists(info.author().getIdLong(), info.args()[0]) != null) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("This Udemy account is already linked.")
                            .addField("Confused?", "If you're stuck or confused, you can ping a Specialist.", false)
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        VerificationProfile profile;
        try {
            profile = verificationHandler.verify(info.author(), info.args()[0]);
        } catch (FileNotFoundException e) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("Please enter a valid public profile url.")
                            .addField("Confused?", "If you're stuck or confused, you can ping a Specialist.", false)
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        } catch (NumberFormatException e) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("Please make sure that you display your courses on your profile in privacy settings.")
                            .addField("Confused?", "If you're stuck or confused, you can ping a Specialist.", false)
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        } catch (IOException e) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("We couldn't verify that you own the course.")
                            .addField("Confused?", "If you're stuck or confused, you can ping a Specialist.", false)
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        if (!profile.verified()) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("We couldn't verify that you own the course.")
                            .addField("Confused?", "If you're stuck or confused, you can ping a Specialist.", false)
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        info.event().getHook().sendMessageEmbeds(
                new EmbedBuilder()
                        .setColor(Color.decode("#89F27B"))
                        .setTitle("Verified")
                        .setDescription("Welcome to the course!")
                        .build()
        ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
        TextChannel channel = Objects.requireNonNull(info.event().getGuild()).getTextChannelById((long)
                LearnSpigotConstant.CHANNEL_GENERAL_ID.get());
        assert channel != null;

        channel.sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#EE8917"))
                .setTitle("Welcome")
                .setDescription("Please welcome " + info.author().getAsMention() + " as a student! ❤")
                .build()
        ).queue();
        info.event().getGuild().addRoleToMember(Objects.requireNonNull(info.member()),
                        Objects.requireNonNull(info.event().getGuild().getRoleById(LearnSpigotConstant.ROLE_STUDENT_ID.get())))
                .queue();
        channel.sendMessage(info.author().getAsMention()).queue(message -> message.delete().queue());
    }

    @Command(label = "verifyother", usage = "/verifyother <mentioned-user> <url>", description = "Verify another user with their url.", roleId = 929465000379187230L, log = true)
    public void onVerifyOtherCommand(final @NotNull CommandInfo info) {
        if (!info.args()[1].matches(LearnSpigotConstant.VALID_LINK_REGEX.get()) || !info.args()[1].contains("udemy.com/user/")) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("Please enter a valid public profile url.")
                            .addField("Confused?", "You shouldn't be, you're staff.", false)
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        Member member = info.optionData().get(0).getAsMember();
        if (member == null) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("Please mention a valid user.")
                            .addField("Confused?", "You shouldn't be, you're staff.", false)
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        User user = member.getUser();

        VerificationProfile potentialProfile = verificationHandler.urlExists(user.getIdLong(), info.args()[1]);
        if (potentialProfile != null) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("This Udemy account is already linked to " + "<@" + potentialProfile.id()
                                    + ">")
                            .addField("Confused?", "You shouldn't be, you're staff.", false)
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        VerificationProfile profile;
        try {
            profile = verificationHandler.verify(user, info.args()[1]);
        } catch (FileNotFoundException e) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("Please enter a valid public profile url.")
                            .addField("Confused?", "You shouldn't be, you're staff.", false)
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        } catch (NumberFormatException e) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("Please make sure that " + user.getAsMention() + " displays their courses on their profile in privacy settings.")
                            .addField("Confused?", "You shouldn't be, you're staff.", false)
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        } catch (IOException e) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("We couldn't verify that " + user.getAsMention() + " owns the course.")
                            .addField("Confused?", "You shouldn't be, you're staff.", false)
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        if (!profile.verified()) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription("We couldn't verify that " + user.getAsMention() + " owns the course.")
                            .addField("Confused?", "You shouldn't be, you're staff.", false)
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        info.event().getHook().sendMessageEmbeds(
                new EmbedBuilder()
                        .setColor(Color.decode("#89F27B"))
                        .setTitle("Verified")
                        .setDescription("Welcome to the course!")
                        .build()
        ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
        TextChannel channel = Objects.requireNonNull(info.event().getGuild()).getTextChannelById((long)
                LearnSpigotConstant.CHANNEL_GENERAL_ID.get());
        assert channel != null;

        channel.sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#EE8917"))
                .setTitle("Welcome")
                .setDescription("Please welcome " + user.getAsMention() + " as a student! ❤")
                .build()
        ).queue();
        info.event().getGuild().addRoleToMember(member,
                Objects.requireNonNull(info.event().getGuild().getRoleById(LearnSpigotConstant.ROLE_STUDENT_ID.get())))
                .queue();
        channel.sendMessage(user.getAsMention()).queue(message -> message.delete().queue());
    }
}