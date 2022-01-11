package com.learnspigot.bot.udemy.lecture.command;

import com.learnspigot.bot.framework.command.Command;
import com.learnspigot.bot.framework.command.CommandInfo;
import com.learnspigot.bot.udemy.lecture.UdemyLecture;
import com.learnspigot.bot.udemy.lecture.UdemyLectureHandler;
import com.learnspigot.bot.udemy.lecture.UdemyQuiz;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;

public final record QuizCommand(@NotNull UdemyLectureHandler udemyLectureHandler) {
    @Command(label = "quiz", usage = "/quiz <title>", description = "Search for a quiz.", log = true)
    public void onQuizCommand(final @NotNull CommandInfo info) {
        if (udemyLectureHandler.udemyQuizzes().isEmpty()) {
            info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("We're currently indexing quizzes. Please try again later.")
                    .build()
            ).queue();
            return;
        }

        Optional<UdemyLecture> optionalQuiz = udemyLectureHandler().searchQuiz(String.join(" ", info.args()));
        if (optionalQuiz.isEmpty()) {
            info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.decode("#E95151"))
                    .setTitle("Error")
                    .setDescription("Our search engine is pretty flexible, but no quizzes matched your search term. Sorry :(")
                    .build()
            ).queue();
            return;
        }

        UdemyQuiz quiz = (UdemyQuiz) optionalQuiz.get();

        info.event().getHook().sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.decode("#EE8917"))
                .setTitle(quiz.title())
                .setDescription("To pass this you need to get at least " + quiz.passPercentage() + "%")
                .addField("Take Quiz", quiz.url(), false)
                .build()
        ).queue();
    }
}
