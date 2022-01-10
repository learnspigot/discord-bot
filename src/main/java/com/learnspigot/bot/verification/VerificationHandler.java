package com.learnspigot.bot.verification;

import com.google.gson.JsonObject;
import com.learnspigot.bot.LearnSpigotBot;
import com.learnspigot.bot.data.ProfileData;
import com.learnspigot.bot.udemy.account.UdemyAccount;
import com.learnspigot.bot.udemy.course.UdemyCourse;
import com.learnspigot.bot.udemy.service.UdemyService;
import com.learnspigot.bot.verification.command.ForceVerifyCommand;
import com.learnspigot.bot.verification.command.VerifyCommand;
import com.learnspigot.bot.verification.profile.VerificationProfile;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public final class VerificationHandler {
    private final @NotNull LearnSpigotBot bot;
    private final @NotNull Set<VerificationProfile> profiles = new HashSet<>();
    private final @NotNull ProfileData profileData = new ProfileData();
    private final @NotNull UdemyService udemyService = UdemyService.instance();

    public VerificationHandler(final @NotNull LearnSpigotBot bot) throws IOException {
        this.bot = bot;
        bot.commandHandler().registerCommands(new VerifyCommand(this),
                new ForceVerifyCommand(this));
        loadProfiles();
    }

    private void loadProfiles() {
        profileData.data().forEach(jsonElement -> profiles.add(profileData.gson().fromJson(jsonElement,
                VerificationProfile.class)));
    }

    public @NotNull VerificationProfile verify(final @NotNull User user, final @NotNull String url) throws IOException {
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
            profileData.cache(verificationProfile);
        }
        return verificationProfile;
    }

    public void forceVerify(final @NotNull User user) {
        VerificationProfile verificationProfile = new VerificationProfile(user.getIdLong(), user.getName());
        verificationProfile.verified(true);
        profiles.add(verificationProfile);
        profileData.cache(verificationProfile);
    }

    public @Nullable VerificationProfile urlExists(final long id, final @NotNull String url) {
        for (VerificationProfile profile : profiles) {
            UdemyAccount account = profile.udemyAccount();
            if (account == null) {
                return null;
            }

            if (profile.id() != id && account.url().split("user/")[1].split("/")[0].equals(
                    url.split("user/")[1].split("/")[0])) {
                return profile;
            }
        }

        return null;
    }

    public @NotNull LearnSpigotBot bot() {
        return bot;
    }
}
