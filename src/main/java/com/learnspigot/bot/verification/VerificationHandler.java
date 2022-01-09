package com.learnspigot.bot.verification;

import com.google.gson.JsonObject;
import com.learnspigot.bot.LearnSpigotBot;
import com.learnspigot.bot.verification.command.VerifyCommand;
import com.learnspigot.bot.verification.profile.VerificationProfile;
import com.learnspigot.bot.verification.udemy.account.UdemyAccount;
import com.learnspigot.bot.verification.udemy.course.UdemyCourse;
import com.learnspigot.bot.verification.udemy.service.UdemyService;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public final class VerificationHandler {
    private final @NotNull Set<VerificationProfile> profiles = new HashSet<>();
    private final @NotNull UdemyService udemyService;

    public VerificationHandler(final @NotNull LearnSpigotBot bot) throws IOException {
        udemyService = new UdemyService();
        bot.commandHandler().registerCommands(new VerifyCommand(this));
    }

    public VerificationProfile verify(final @NotNull User user, final @NotNull String url) throws IOException {
        VerificationProfile verificationProfile = new VerificationProfile(user.getIdLong(), user.getName());

        JsonObject userInfo = udemyService.getUserInfo(udemyService.getUserID(url));

        UdemyAccount udemyAccount = new UdemyAccount(
                userInfo.get("id").getAsLong(),
                userInfo.get("display_name").getAsString(),
                userInfo.get("image_100x100").getAsString(),
                "https://www.udemy.com" + userInfo.get("url").getAsString());

        udemyService.getUserCourses(udemyAccount.id()).iterator().forEachRemaining((course) -> {
            JsonObject jsonCourse = course.getAsJsonObject();
            udemyAccount.addCourse(new UdemyCourse(
                    jsonCourse.get("id").getAsLong(),
                    jsonCourse.get("title").getAsString(),
                    "https://www.udemy.com" + jsonCourse.get("url").getAsString()));
        });

        verificationProfile.udemyAccount(udemyAccount);
        if (verificationProfile.ownsCourse()) {
            verificationProfile.verified(true);
            profiles.add(verificationProfile);
        }
        return verificationProfile;
    }

    public boolean urlExists(final long id, final @NotNull String url) {
        for (VerificationProfile profile : profiles) {
            UdemyAccount account = profile.udemyAccount();
            if (account == null) {
                return false;
            }

            if (profile.id() != id && account.url().contains(url.split("user/")[1].split("/")[0])) {
                return true;
            }
        }

        return false;
    }
}
