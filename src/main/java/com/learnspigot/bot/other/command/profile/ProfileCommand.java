package com.learnspigot.bot.other.command.profile;

import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import com.learnspigot.bot.udemy.account.UdemyAccount;
import com.learnspigot.bot.verification.VerificationHandler;
import com.learnspigot.bot.verification.profile.VerificationProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public record ProfileCommand(@NotNull VerificationHandler verificationHandler) {
    @Command(label = "profile", usage = "/profile <mentioned-user>", description = "View a user's linked Udemy account.", roleId = 0L, log = true)
    public void onProfileCommand(final @NotNull CommandInfo info) {
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
        Optional<VerificationProfile> verificationProfile = verificationHandler.profile(user.getIdLong());
        if (verificationProfile.isEmpty()) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription(user.getAsMention() + " does not have a Udemy account linked.")
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        VerificationProfile profile = verificationProfile.get();

        if (profile.udemyAccount() == null) {
            info.event().getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.decode("#E95151"))
                            .setTitle("Error")
                            .setDescription(user.getAsMention() + " does not have a Udemy account linked.")
                            .build()
            ).queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            return;
        }

        UdemyAccount account = profile.udemyAccount();

        StringBuilder courses = new StringBuilder();
        account.courses().forEach(course -> {
            courses.append("• [").append(course.title()).append("](").append(course.url()).append(")\n");
        });

        info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#EE8917"))
                .setTitle("Profile Lookup")
                .addField("Discord", profile.name() + " (" + user.getAsMention() + ")", false)
                .addField("Udemy", "[" + account.displayName() + "](" + account.url() + ")" + " (" + account.id() + ")", true)
                .addField("Courses", courses.toString(), false)
                .setThumbnail(account.avatarURL())
                .build()
        ).queue();
    }
}
