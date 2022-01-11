package com.learnspigot.bot;

import com.learnspigot.bot.framework.command.CommandHandler;
import com.learnspigot.bot.other.command.commission.CommissionCommand;
import com.learnspigot.bot.other.command.embed.EmbedCommand;
import com.learnspigot.bot.other.command.profile.ProfileCommand;
import com.learnspigot.bot.other.command.suggest.SuggestCommand;
import com.learnspigot.bot.other.command.wixstock.WixStockCommand;
import com.learnspigot.bot.udemy.lecture.UdemyLectureHandler;
import com.learnspigot.bot.verification.VerificationHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public final class LearnSpigotBot {
    private final @NotNull JDA jda;
    private final @NotNull CommandHandler commandHandler;
    private final @NotNull VerificationHandler verificationHandler;
    private final @NotNull UdemyLectureHandler udemyLectureHandler;

    public LearnSpigotBot() throws LoginException, InterruptedException, IOException {
        jda = JDABuilder.createDefault(LearnSpigotConstant.BOT_TOKEN.get()).setActivity(Activity.watching(
                LearnSpigotConstant.ACTIVITY.get())).build().awaitReady();

        commandHandler = new CommandHandler(this);
        verificationHandler = new VerificationHandler(this);
        udemyLectureHandler = new UdemyLectureHandler(this);

        commandHandler.registerCommands(new CommissionCommand(), new SuggestCommand(), new WixStockCommand(),
                new ProfileCommand(verificationHandler), new EmbedCommand());

        preventTalking();
    }

    private void preventTalking() {
        jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onMessageReceived(final @NotNull MessageReceivedEvent event) {
                boolean suggestionsChannel = event.getChannel().getId().equals(LearnSpigotConstant.CHANNEL_SUGGESTIONS_ID.get());
                boolean suggestCommand = event.getMessage().getContentRaw().startsWith("/suggest")
                        || event.getMessage().getContentRaw().equals("");
                boolean commissionsChannel = event.getChannel().getId().equals(LearnSpigotConstant.CHANNEL_COMMISSIONS_ID.get());
                boolean requestOrOfferCommand = event.getMessage().getContentRaw().startsWith("/request")
                        || event.getMessage().getContentRaw().startsWith("/offer")
                        || event.getMessage().getContentRaw().equals("");

                if ((suggestionsChannel && !suggestCommand) || (commissionsChannel && !requestOrOfferCommand)) {
                    System.out.println("here");
                    event.getMessage().delete().queue();
                }
            }
        });
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

    public @NotNull UdemyLectureHandler udemyLectureHandler() {
        return udemyLectureHandler;
    }
}
