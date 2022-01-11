package com.learnspigot.bot.udemy.lecture.command;

import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import com.learnspigot.bot.udemy.lecture.UdemyLecture;
import com.learnspigot.bot.udemy.lecture.UdemyLectureHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;

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

        Optional<UdemyLecture> optionalLecture = udemyLectureHandler().searchLecture(String.join(" ", info.args()));
        if (optionalLecture.isEmpty()) {
            info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("Our search engine is pretty flexible, but no lectures matched your search term. Sorry :(")
                    .build()
            ).queue();
            return;
        }

        UdemyLecture lecture = optionalLecture.get();

        info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#EE8917"))
                .setTitle(lecture.title())
                .setDescription(lecture.description())
                .addField("Watch", lecture.url(), false)
                .build()
        ).queue();
    }
}
