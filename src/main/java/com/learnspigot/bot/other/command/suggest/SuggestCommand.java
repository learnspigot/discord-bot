package com.learnspigot.bot.other.command.suggest;

import com.learnspigot.bot.LearnSpigotConstant;
import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
            ).queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
            return;
        }

        String description = String.join(" ", info.args());
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

        info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#EE8917"))
                .setTitle("Suggestion")
                .setDescription(description)
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
