package com.learnspigot.bot.verification.udemy.account;

import com.learnspigot.bot.verification.udemy.course.UdemyCourse;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UdemyAccount {
    private final long id;
    private final @NotNull String displayName;
    private final @NotNull String avatarURL;
    private final @NotNull String url;
    private final @NotNull Set<UdemyCourse> courses = new HashSet<>();

    public UdemyAccount(final long id, final @NotNull String displayName, final @NotNull String avatarURL,
                        final @NotNull String url) {
        this.id = id;
        this.displayName = displayName;
        this.avatarURL = avatarURL;
        this.url = url;
    }

    public long id() {
        return id;
    }

    public @NotNull String displayName() {
        return displayName;
    }

    public @NotNull String avatarURL() {
        return avatarURL;
    }

    public @NotNull String url() {
        return url;
    }

    public @NotNull Set<UdemyCourse> courses() {
        return Collections.unmodifiableSet(courses);
    }

    public void addCourse(final @NotNull UdemyCourse course) {
        courses.add(course);
    }

    public @NotNull Optional<UdemyCourse> findCourse(final long id) {
        return courses.stream().filter(course -> course.id() == id).findFirst();
    }

    @Override
    public @NotNull String toString() {
        return "UdemyAccount{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", url='" + url + '\'' +
                ", courses=" + courses +
                '}';
    }
}
