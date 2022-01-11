package com.learnspigot.bot.other.command.commission;

import com.learnspigot.bot.LearnSpigotConstant;
import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class CommissionCommand {
    @Command(label = "offer", usage = "/offer <info> [image]", description = "Post an offer in #commission!", log = true)
    public void onOfferCommand(final @NotNull CommandInfo info) {
        if (!info.channel().getId().equals(LearnSpigotConstant.CHANNEL_COMMISSIONS_ID.get())) {
            info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("This command can only be used in " + Objects.requireNonNull(info.jda()
                                    .getTextChannelById(LearnSpigotConstant.CHANNEL_COMMISSIONS_ID.get()))
                            .getAsMention() + ".")
                    .build()
            ).queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
            return;
        }

        String description = String.join(" ", info.optionData().get(0).getAsString());
        char[] lol = new char[description.toCharArray().length];
        char last = ' ';
        int i = 0;
        for (char c : description.toLowerCase().toCharArray()) {
            lol[i] = c;
            if (last == '\\' && c == 'n') {
                lol[i-1] = '/';
                lol[i] = '%';
            }
            last = c;
            i++;
        }
        description = new String(lol).replaceAll("/%", "\n");

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.decode("#FA7FFF"))
                .setTitle("Offer")
                .setDescription(description)
                .addField("Offer by", info.author().getAsMention(), false)
                .setFooter("Directly contact the user to find out more. Want to make your own post? Use /request or /offer.");

        if (info.optionData().size() >= 2) {
            embed.setImage(info.optionData().get(1).getAsString());
        }

        info.event().getHook().sendMessageEmbeds(embed.build()).queue();
    }

    @Command(label = "request", usage = "/request <info> [image]", description = "Post a request in #commission!", log = true)
    public void onRequestCommand(final @NotNull CommandInfo info) {
        if (!info.channel().getId().equals(LearnSpigotConstant.CHANNEL_COMMISSIONS_ID.get())) {
            info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("This command can only be used in " + Objects.requireNonNull(info.jda()
                                    .getTextChannelById(LearnSpigotConstant.CHANNEL_COMMISSIONS_ID.get()))
                            .getAsMention() + ".")
                    .build()
            ).queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
            return;
        }

        String description = String.join(" ", info.optionData().get(0).getAsString());
        char[] lol = new char[description.toCharArray().length];
        char last = ' ';
        int i = 0;
        for (char c : description.toLowerCase().toCharArray()) {
            lol[i] = c;
            if (last == '\\' && c == 'n') {
                lol[i-1] = '/';
                lol[i] = '%';
            }
            last = c;
            i++;
        }
        description = new String(lol).replaceAll("/%", "\n");

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.decode("#9D7FFF"))
                .setTitle("Request")
                .setDescription(description)
                .addField("Request by", info.author().getAsMention(), false)
                .setFooter("Directly contact the user to find out more. Want to make your own post? Use /request or /offer.");

        if (info.optionData().size() >= 2) {
            embed.setImage(info.optionData().get(1).getAsString());
        }

        info.event().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
