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

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.decode("#FA7FFF"))
                .setTitle("Offer")
                .setDescription(String.join(" ", info.optionData().get(0).getAsString()))
                .addField("Offer by", info.author().getAsMention(), false)
                .setFooter("Directly contact the user to find out more. Want to make your own post? Use /request or /offer.");

        if (info.args().length >= 2) {
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

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.decode("#9D7FFF"))
                .setTitle("Request")
                .setDescription(String.join(" ", info.optionData().get(0).getAsString()))
                .addField("Request by", info.author().getAsMention(), false)
                .setFooter("Directly contact the user to find out more. Want to make your own post? Use /request or /offer.");

        if (info.args().length >= 2) {
            embed.setImage(info.optionData().get(1).getAsString());
        }

        info.event().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
