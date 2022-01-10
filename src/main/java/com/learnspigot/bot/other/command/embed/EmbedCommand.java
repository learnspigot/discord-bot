package com.learnspigot.bot.other.command.embed;

import com.learnspigot.bot.LearnSpigotConstant;
import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public final class EmbedCommand {
    @Command(label = "embed", usage = "/embed <channel> <hex-color> <title> <description> [thumbnail]", description = "Post an embed", log = true)
    public void onEmbedCommand(final @NotNull CommandInfo info) {
        MessageChannel channel = info.optionData().get(0).getAsMessageChannel();
        if (channel == null) {
            info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("Please enter a valid channel.")
                    .build()
            ).queue();
            return;
        }

        Color color = null;
        try {
            color = Color.decode(info.optionData().get(1).getAsString());
        } catch (NumberFormatException e) {
            info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("Please enter a valid hex code.")
                    .build()
            ).queue();
            return;
        }

        String thumbnail = info.optionData().get(5).getAsString();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(color)
                .setTitle(info.optionData().get(2).getAsString())
                .setDescription(info.optionData().get(3).getAsString())
                .setFooter(info.optionData().get(4).getAsString());

        if (!thumbnail.isEmpty() && thumbnail.matches(LearnSpigotConstant.VALID_LINK_REGEX.get())) {
            embed.setThumbnail(thumbnail);
        }

        info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#89F27B"))
                .setTitle("Success")
                .setDescription("Your embed was sent to " + channel.getAsMention() + ".")
                .build()).queue();
        channel.sendMessageEmbeds(embed.build()).queue();
    }
}
