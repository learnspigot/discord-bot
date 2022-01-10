package com.learnspigot.bot.udemy.lecture.command;

import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import com.learnspigot.bot.udemy.lecture.UdemyLectureHandler;
import com.learnspigot.bot.udemy.lecture.UdemyQuiz;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public final record QuizCommand(@NotNull UdemyLectureHandler udemyLectureHandler) {
    @Command(label = "quiz", usage = "/quiz <title>", description = "Search for a quiz.", log = true)
    public void onQuizCommand(final @NotNull CommandInfo info) {
        if (udemyLectureHandler.udemyQuizzes().isEmpty()) {
            info.event().replyEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("We're currently indexing quizzes. Please try again later.")
                    .build()
            ).queue();
            return;
        }

        UdemyQuiz udemyQuiz = udemyLectureHandler().searchQuiz(String.join(" ", info.args()));
        if (udemyQuiz.id() == 0L) {
            info.event().replyEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("Our search engine is pretty flexible, but no quizzes matched your search term. Sorry :(")
                    .build()
            ).queue();
            return;
        }

        info.event().replyEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#EE8917"))
                .setTitle(udemyQuiz.title())
                .setDescription("To pass this you need to get at least " + udemyQuiz.passPercentage() + "%")
                .addField("Take Quiz", udemyQuiz.url(), false)
                .build()
        ).queue();
    }
}
