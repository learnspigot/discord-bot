package com.learnspigot.bot;

import com.learnspigot.bot.framework.command.CommandHandler;
import com.learnspigot.bot.verification.VerificationHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public final class LearnSpigotBot {
    private final @NotNull JDA jda;
    private final @NotNull CommandHandler commandHandler;
    private final @NotNull VerificationHandler verificationHandler;

    public LearnSpigotBot() throws LoginException, InterruptedException, IOException {
        jda = JDABuilder.createDefault(LearnSpigotConstant.BOT_TOKEN.get()).setActivity(Activity.watching(
                LearnSpigotConstant.ACTIVITY.get())).build().awaitReady();

        commandHandler = new CommandHandler(this);
        verificationHandler = new VerificationHandler(this);
    }

    public void test() throws IOException {
    }

    public @NotNull JDA jda() {
        return jda;
    }

    public @NotNull CommandHandler commandHandler() {
        return commandHandler;
    }

    public @NotNull VerificationHandler verificationHandler() {
        return verificationHandler;
    }
}
