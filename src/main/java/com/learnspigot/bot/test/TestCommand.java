package com.learnspigot.bot.test;

import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

public final class TestCommand {
    @Command(label = "test", usage = "/test <test-arg>", aliases = {"test2"}, description = "A test command.", log = true)
    public void onTestCommand(final @NotNull CommandInfo info) {
        info.event().reply(info.toString()).queue();
    }
}