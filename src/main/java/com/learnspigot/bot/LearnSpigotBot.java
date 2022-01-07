package com.learnspigot.bot;

import com.learnspigot.bot.framework.command.CommandHandler;
import com.learnspigot.bot.test.TestCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public final class LearnSpigotBot {
    private final @NotNull JDA jda;
    private final @NotNull CommandHandler commandHandler;

    public LearnSpigotBot() throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault(LearnSpigotConstant.BOT_TOKEN.get()).build();
        jda.awaitReady();
        commandHandler = new CommandHandler(jda);
        commandHandler.registerCommands(new TestCommand());
    }

    public @NotNull JDA jda() {
        return jda;
    }

    public @NotNull CommandHandler commandHandler() {
        return commandHandler;
    }
}
