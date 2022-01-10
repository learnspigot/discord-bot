package com.learnspigot.bot.udemy.course;

import org.jetbrains.annotations.NotNull;

public final class UdemyCourse {
    private final long id;
    private final @NotNull String title;
    private final @NotNull String url;

    public UdemyCourse(final long id, final @NotNull String title, final @NotNull String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public long id() {
        return id;
    }

    public @NotNull String title() {
        return title;
    }

    public @NotNull String url() {
        return url;
    }
}
