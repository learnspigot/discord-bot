package com.learnspigot.bot.verification.udemy.course;

import org.jetbrains.annotations.NotNull;

public record UdemyCourse(long id, @NotNull String title, @NotNull String url) {
}
