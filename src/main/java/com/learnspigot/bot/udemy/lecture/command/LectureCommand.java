package com.learnspigot.bot.udemy.lecture.command;

import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import com.learnspigot.bot.udemy.lecture.UdemyLecture;
import com.learnspigot.bot.udemy.lecture.UdemyLectureHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public final record LectureCommand(@NotNull UdemyLectureHandler udemyLectureHandler) {
    @Command(label = "lecture", usage = "/lecture <title>", description = "Search for a lecture.", log = true)
    public void onLectureCommand(final @NotNull CommandInfo info) {
        if (udemyLectureHandler.udemyLectures().isEmpty()) {
            info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("We're currently indexing lectures. Please try again later.")
                    .build()
            ).queue();
            return;
        }

        UdemyLecture udemyLecture = udemyLectureHandler().searchLecture(String.join(" ", info.args()));
        if (udemyLecture.id() == 0L) {
            info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("Our search engine is pretty flexible, but no lectures matched your search term. Sorry :(")
                    .build()
            ).queue();
            return;
        }

        info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#EE8917"))
                .setTitle(udemyLecture.title())
                .setDescription(udemyLecture.description())
                .addField("Watch", udemyLecture.url(), false)
                .build()
        ).queue();
    }
}
