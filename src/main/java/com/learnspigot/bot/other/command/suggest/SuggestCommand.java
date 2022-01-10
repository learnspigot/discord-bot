package com.learnspigot.bot.other.command.suggest;

import com.learnspigot.bot.LearnSpigotConstant;
import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

public final class SuggestCommand {
    @Command(label = "suggest", usage = "/suggest <idea>", description = "Create a suggestion that everyone can vote on!", log = true)
    public void onSuggestCommand(final @NotNull CommandInfo info) {
        if (!info.channel().getId().equals(LearnSpigotConstant.CHANNEL_SUGGESTIONS_ID.get())) {
            info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("This command can only be used in " + (Objects.requireNonNull(info.jda()
                                    .getTextChannelById((LearnSpigotConstant.CHANNEL_SUGGESTIONS_ID.get())))
                            .getAsMention() + "."))
                    .build()
            ).queue();
            return;
        }

        info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#EE8917"))
                .setTitle("Suggestion")
                .setDescription(String.join(" ", info.args()))
                .addField("Submitted by", info.author().getAsMention(), false)
                .build()
        ).queue(message -> {
            message.addReaction(Objects.requireNonNull(info.jda().getEmoteById(
                    (long) LearnSpigotConstant.EMOJI_YES_ID.get()))).queue();
            message.addReaction(Objects.requireNonNull(info.jda().getEmoteById(
                    (long) LearnSpigotConstant.EMOJI_NO_ID.get()))).queue();
        });
    }
}
