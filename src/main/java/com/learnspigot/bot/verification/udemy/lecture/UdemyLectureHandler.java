package com.learnspigot.bot.udemy.lecture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.learnspigot.bot.LearnSpigotBot;
import com.learnspigot.bot.udemy.lecture.command.LectureCommand;
import com.learnspigot.bot.udemy.lecture.command.QuizCommand;
import com.learnspigot.bot.udemy.service.UdemyService;
import dev.devous.searcher.SearchResult;
import dev.devous.searcher.Searcher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class UdemyLectureHandler {
    private final @NotNull List<UdemyLecture> lectures = new ArrayList<>();
    private final @NotNull List<String> lectureNames = new ArrayList<>();
    private final @NotNull List<String> quizNames = new ArrayList<>();
    private final @NotNull UdemyService udemyService = UdemyService.instance();

    public UdemyLectureHandler(final @NotNull LearnSpigotBot bot) {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::update, 0L, 1L,
                TimeUnit.HOURS);
        bot.commandHandler().registerCommands(new LectureCommand(this),
                new QuizCommand(this));
    }

    public void update() {
        JsonArray lecturesArray = new JsonArray();
        try {
            lecturesArray = udemyService.getLectures();
        } catch (IOException e) {
            System.out.println("Unable to get lectures from endpoint.");
        }

        lectures.clear();

        lecturesArray.forEach(jsonElement -> {
            JsonObject lecture = jsonElement.getAsJsonObject();
            if (lecture.get("_class").getAsString().equals("lecture")) {
                UdemyLecture udemyLecture = new UdemyLecture(lecture.get("id").getAsLong(), lecture.get("title").getAsString(),
                        format(lecture.get("description").getAsString()));
                lectureNames.add(udemyLecture.title());
                lectures.add(udemyLecture);
            } else {
                UdemyQuiz udemyQuiz = new UdemyQuiz(lecture.get("id").getAsLong(), lecture.get("title").getAsString(),
                        format(lecture.get("description").getAsString()), lecture.get("pass_percent").getAsInt());
                quizNames.add(udemyQuiz.title());
                lectures.add(udemyQuiz);
            }
        });
    }

    public @NotNull List<String> udemyLectures() {
        return Collections.unmodifiableList(lectureNames);
    }

    public @NotNull List<String> udemyQuizzes() {
        return Collections.unmodifiableList(quizNames);
    }

    public @NotNull UdemyLecture searchLecture(final @NotNull String string) {
        SearchResult result = Searcher.instance().searchFor(string, lectureNames);
        return lectures.stream().filter(lecture -> lecture.title().equals(result.result())).findFirst().orElse(
                new UdemyLecture(0L, "", ""));
    }

    public @NotNull UdemyQuiz searchQuiz(final @NotNull String string) {
        System.out.println(quizNames);
        SearchResult result = Searcher.instance().searchFor(string, quizNames);
        System.out.println(result);
        return (UdemyQuiz) lectures.stream().filter(lecture -> lecture.title().equals(result.result())).findFirst().orElse(
                new UdemyQuiz(0L, "", "", 0));
    }

    private @NotNull String format(final @NotNull String string) {
        return string
                .replaceAll("<p>", "")
                .replaceAll("</p>", "")
                .replaceAll("<ul>", "")
                .replaceAll("</ul>", "")
                .replaceAll("<li>", "\n• ")
                .replaceAll("</li>", "")
                .replaceAll("&amp;", "&");
    }
}
