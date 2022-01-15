package com.learnspigot.bot;

import com.learnspigot.bot.framework.command.CommandHandler;
import com.learnspigot.bot.minecraft.CodeHandler;
import com.learnspigot.bot.minecraft.command.VerifyMCCommand;
import com.learnspigot.bot.mongo.MongoDatabase;
import com.learnspigot.bot.other.command.commission.CommissionCommand;
import com.learnspigot.bot.other.command.embed.EmbedCommand;
import com.learnspigot.bot.other.command.profile.ProfileCommand;
import com.learnspigot.bot.other.command.suggest.SuggestCommand;
import com.learnspigot.bot.other.command.wixstock.WixStockCommand;
import com.learnspigot.bot.udemy.lecture.UdemyLectureHandler;
import com.learnspigot.bot.verification.VerificationHandler;
import dev.devous.barter.Barter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class LearnSpigotBot {
    private final @NotNull JDA jda;
    private final @NotNull CommandHandler commandHandler;
    private final @NotNull Barter barter;
    private final @NotNull VerificationHandler verificationHandler;
    private final @NotNull UdemyLectureHandler udemyLectureHandler;
    private final @NotNull CodeHandler codeHandler;

    public LearnSpigotBot() throws LoginException, InterruptedException, IOException {
        jda = JDABuilder.createDefault(LearnSpigotConstant.BOT_TOKEN.get()).setActivity(Activity.watching(
                LearnSpigotConstant.ACTIVITY.get())).build().awaitReady();

        MongoDatabase mongoDatabase = new MongoDatabase(LearnSpigotConstant.MONGO_DATABASE_URI.get());

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        commandHandler = new CommandHandler(this);
        barter = new Barter(scheduledExecutorService);
        verificationHandler = new VerificationHandler(this, scheduledExecutorService);
        udemyLectureHandler = new UdemyLectureHandler(this, scheduledExecutorService);
        codeHandler = new CodeHandler(scheduledExecutorService, mongoDatabase.getCollection("codes"),
                verificationHandler);

        commandHandler.registerCommands(new CommissionCommand(), new SuggestCommand(), new WixStockCommand(),
                new ProfileCommand(verificationHandler), new EmbedCommand(),
                new VerifyMCCommand(verificationHandler, codeHandler, barter));

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

    public @NotNull Barter barter() {
        return barter;
    }

    public @NotNull VerificationHandler verificationHandler() {
        return verificationHandler;
    }

    public @NotNull UdemyLectureHandler udemyLectureHandler() {
        return udemyLectureHandler;
    }

    public @NotNull CodeHandler codeHandler() {
        return codeHandler;
    }
}
