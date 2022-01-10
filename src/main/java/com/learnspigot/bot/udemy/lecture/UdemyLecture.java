package com.learnspigot.bot.udemy.lecture;

import org.jetbrains.annotations.NotNull;

public class UdemyLecture {
    private final long id;
    private final @NotNull String title;
    private final @NotNull String description;

    public UdemyLecture(long id, @NotNull String title, @NotNull String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public long id() {
        return id;
    }

    public @NotNull String title() {
        return title;
    }

    public @NotNull String description() {
        return description;
    }

    public @NotNull String url() {
        return "https://www.udemy.com/course/develop-minecraft-plugins-java-programming/learn/lecture/" + id;
    }

    @Override
    public @NotNull String toString() {
        return "UdemyLecture{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
