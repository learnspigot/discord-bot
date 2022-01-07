package com.learnspigot.bot.framework.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class CommandInfo {
    private final @NotNull CommandHandler handler;
    private final @NotNull SlashCommandEvent event;
    private final @NotNull String usage;
    private @Nullable OptionData[] optionData;

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

    public @Nullable OptionData[] optionData() {
        return optionData;
    }

    public void optionData(@NotNull OptionData[] optionData) {
        this.optionData = optionData;
    }

    public @NotNull MessageChannel channel() {
        return event.getChannel();
    }

    public @NotNull String message() {
        return event.getName() + " " + event.getOptions().get(0).getAsString();
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

    @Override
    public String toString() {
        return "CommandInfo{" +
                "handler=" + handler +
                ", message=" + message() +
                ", usage='" + usage + '\'' +
                '}';
    }
}
