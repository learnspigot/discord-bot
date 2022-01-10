package com.learnspigot.bot.udemy.lecture;

import org.jetbrains.annotations.NotNull;

public final class UdemyQuiz extends UdemyLecture {
    private final int passPercentage;

    public UdemyQuiz(final long id, final @NotNull String title, final @NotNull String description,
                     final int passPercentage) {
        super(id, title, description);
        this.passPercentage = passPercentage;
    }

    public int passPercentage() {
        return passPercentage;
    }

    @Override
    public @NotNull String url() {
        return "https://www.udemy.com/course/develop-minecraft-plugins-java-programming/learn/quiz/" + id();
    }

    @Override
    public @NotNull String toString() {
        return "UdemyQuiz{" +
                "id=" + id() +
                ", title='" + title() + '\'' +
                ", description='" + description() + '\'' +
                ", passPercentage='" + passPercentage + '\'' +
                '}';
    }
}
