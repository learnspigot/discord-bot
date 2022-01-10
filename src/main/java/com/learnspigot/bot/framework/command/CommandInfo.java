package com.learnspigot.bot.framework.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public final class CommandInfo {
    private final @NotNull CommandHandler handler;
    private final @NotNull SlashCommandEvent event;
    private final @NotNull String usage;

    public CommandInfo(final @NotNull CommandHandler handler, final @NotNull SlashCommandEvent event,
                       final @NotNull String usage) {
        this.handler = handler;
        this.event = event;
        this.usage = usage;
    }

    public @NotNull CommandHandler handler() {
        return handler;
    }

    public @NotNull SlashCommandEvent event() {
        return event;
    }

    public @NotNull String usage() {
        return usage;
    }

    public @NotNull List<OptionMapping> optionData() {
        return event.getOptions();
    }

    public @NotNull MessageChannel channel() {
        return event.getChannel();
    }

    public @NotNull String message() {
        return "/" + event.getName() + " " + buildMessage();
    }

    private @NotNull String buildMessage() {
        StringBuilder builder = new StringBuilder();
        for (OptionMapping option : event.getOptions()) {
            builder.append(option.getAsString()).append(" ");
        }
        return builder.toString();
    }

    public @NotNull String[] args() {
        return Arrays.copyOfRange(message().split("\\s+"), 1,
                message().split("\\s+").length);
    }

    public @NotNull User author() {
        return event.getUser();
    }

    public @Nullable Member member() {
        return event.getMember();
    }

    public @NotNull JDA jda() {
        return event.getJDA();
    }

    @Override
    public String toString() {
        return "CommandInfo{" +
                "handler=" + handler +
                ", message=" + message() +
                ", usage='" + usage + '\'' +
                '}';
    }
}
